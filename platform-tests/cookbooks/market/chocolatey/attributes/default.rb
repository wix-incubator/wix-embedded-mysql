if node['platform_family'] == 'windows'
  default['chocolatey']['Uri']         = 'https://chocolatey.org/install.ps1'
  default['chocolatey']['upgrade']     = true
end
