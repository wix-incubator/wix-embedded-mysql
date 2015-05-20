include_recipe "chocolatey"
chocolatey "#{node[:java_windows][:package_name]}"
