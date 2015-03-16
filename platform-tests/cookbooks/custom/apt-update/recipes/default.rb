bash "update-apt-repository" do
  user "root"
  code <<-EOH
  apt-get update
  EOH
end