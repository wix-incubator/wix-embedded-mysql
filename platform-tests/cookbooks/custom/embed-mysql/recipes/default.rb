path = node[:embed_mysql][:path]

if node["platform"] == "windows"
	remote_directory "#{path}" do
  	files_mode '0770'
  	files_owner 'vagrant'
  	files_group 'vagrant'
  	mode '0770'
  	owner 'vagrant'
  	group 'vagrant'
    inherits true      
  	purge true
  	source "wix-embedded-mysql"
	end
else
  remote_directory "#{path}" do
    files_mode '0770'
    files_owner 'vagrant'
    files_group 'vagrant'
    mode '0770'
    owner 'vagrant'
    group 'vagrant'
    purge true
    source "wix-embedded-mysql"
  end
end
