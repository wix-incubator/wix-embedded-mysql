#
# Provider:: chocolatey
# Author:: Guilhem Lettron <guilhem.lettron@youscribe.com>
#
# Copyright 20012, Societe Publica.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

use_inline_resources

# Support whyrun
def whyrun_supported?
  true
end

def load_current_resource
  @current_resource = Chef::Resource::Chocolatey.new(@new_resource.name)
  @current_resource.name(@new_resource.name)
  @current_resource.version(@new_resource.version)
  @current_resource.source(@new_resource.source)
  @current_resource.args(@new_resource.args)
  @current_resource.options(@new_resource.options)
  @current_resource.package(@new_resource.package)
  @current_resource.exists = true if package_exists?(@current_resource.package, @current_resource.version)
  #  @current_resource.installed = true if package_installed?(@current_resource.package)
end

action :install do
  if @current_resource.exists
    Chef::Log.info "#{ @current_resource.package } already installed - nothing to do."
  elsif @current_resource.version
    install_version(@current_resource.package, @current_resource.version)
  else
    install(@current_resource.package)
  end
end

action :upgrade do
  package_name = @current_resource.package
  if upgradeable?(package_name)
    upgrade(package_name)
  else
    Chef::Log.info("Package #{@current_resource} already to latest version")
  end
end

action :remove do
  if @current_resource.exists
    package_name = @current_resource.package
    converge_by("uninstall package #{package_name}") do
      execute "uninstall package #{package_name}" do
        command "#{::ChocolateyHelpers.chocolatey_executable} uninstall -y #{package_name}"
      end
    end
  else
    Chef::Log.info "#{@current_resource} not installed - nothing to do."
  end
end

def cmd_args
  output = ''
  output += " -source #{@current_resource.source}" if @current_resource.source
  output += " -ia '#{@current_resource.args}'" unless @current_resource.args.to_s.empty?
  @current_resource.options.each do |k, v|
    output += " -#{k}"
    output += " #{v}" if v
  end
  output
end

def package_installed?(name)
  package_exists?(name, nil)
end

def package_exists?(name, version)
  cmd = Mixlib::ShellOut.new("#{::ChocolateyHelpers.chocolatey_executable} list -l -r #{name}")
  cmd.run_command
  software = cmd.stdout.split("\r\n").each_with_object({}) do |s, h|
    v, k = s.split('|')
    h[String(v).strip.downcase] = String(k).strip.downcase
    h
  end

  if version
    software[name.downcase] == version.downcase
  else
    !software[name.downcase].nil?
  end
end

def upgradeable?(name)
  return false unless @current_resource.exists
  unless package_installed?(name)
    Chef::Log.debug("Package isn't installed... we can upgrade it!")
    return true
  end

  Chef::Log.debug("Checking to see if this chocolatey package is installed/upgradable: '#{name}'")
  cmd = Mixlib::ShellOut.new("#{::ChocolateyHelpers.chocolatey_executable} upgrade -r --noop #{cmd_args} #{name}")
  cmd.run_command
  result = cmd.stdout.chomp
  package_name, current_version, updated_version, is_pinned = result.split('|')
  raise "Wrong package name #{name} != #{package_name}" if package_name != name
  current_version != updated_version && is_pinned != 'true'
end

def install(name)
  execute "install package #{name}" do
    command "#{::ChocolateyHelpers.chocolatey_executable} install -y #{cmd_args} #{name}"
  end
end

def upgrade(name)
  execute "updating #{name} to latest" do
    command "#{::ChocolateyHelpers.chocolatey_executable} upgrade -y #{cmd_args} #{name}"
  end
end

def install_version(name, version)
  execute "install package #{name} version #{version}" do
    command "#{::ChocolateyHelpers.chocolatey_executable} install -y -version  #{version} #{cmd_args} #{name}"
  end
end
