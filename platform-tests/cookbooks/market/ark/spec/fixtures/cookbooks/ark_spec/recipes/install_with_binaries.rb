
ark 'test_install' do
  url 'https://github.com/opscode-cookbooks/ark/raw/master/files/default/foo.tar.gz'
  checksum '5996e676f17457c823d86f1605eaa44ca8a81e70d6a0e5f8e45b51e62e0c52e8'
  version '2'
  prefix_root '/usr/local'
  owner 'foobarbaz'
  group 'foobarbaz'
  has_binaries ['bin/do_foo', 'bin/do_more_foo']
  action :install
end
