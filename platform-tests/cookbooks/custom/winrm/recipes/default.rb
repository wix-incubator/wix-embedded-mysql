new_limit = node['winrm']['limit_mb']

batch "update winrm mem-limit" do
  code <<-EOH
    powershell -command \"Set-Item WSMan:\\localhost\\Shell\\MaxMemoryPerShellMB #{new_limit}\"
    EOH
  action :run
end