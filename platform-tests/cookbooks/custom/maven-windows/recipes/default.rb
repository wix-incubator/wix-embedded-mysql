dir = "apache-maven-#{node[:maven][:version]}"
zip = "#{dir}-bin.zip"
download_url = "#{node[:maven][:mirror]}/apache/maven/maven-#{node[:maven][:major]}/#{node[:maven][:version]}/binaries/#{zip}"
home = "C:\\"
path = "#{home}\\#{dir}"
szip = "#{node['7-zip']['home']}\\7z.exe"

#Note: windows_zipfile fails to extract, so use remote_file + execute instead
remote_file "#{home}\\#{zip}" do
  source download_url
  not_if {::File.exists?("#{home}\\#{zip}")}
end

execute "#{szip} x #{home}\\#{zip} -y > nul" do
  cwd home
  not_if {::File.exists?("#{path}\\bin\\mvn.bat")}
end

env "M2_HOME" do
  value path
end

windows_path "#{path}\\bin\\" do
  action :add
end