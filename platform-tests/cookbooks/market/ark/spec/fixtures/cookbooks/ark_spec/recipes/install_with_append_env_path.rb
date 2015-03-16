
ark 'test_install_with_append_env_path' do
  version '7.0.26'
  url 'https://github.com/opscode-cookbooks/ark/raw/master/files/default/foo.tar.gz'
  checksum '5996e676f17457c823d86f1605eaa44ca8a81e70d6a0e5f8e45b51e62e0c52e8'
  append_env_path true
  action :install
end
