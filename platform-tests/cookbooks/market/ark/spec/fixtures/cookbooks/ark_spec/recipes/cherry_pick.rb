
ark 'test_cherry_pick' do
  url 'https://github.com/opscode-cookbooks/ark/raw/master/files/default/foo.tar.gz'
  checksum '5996e676f17457c823d86f1605eaa44ca8a81e70d6a0e5f8e45b51e62e0c52e8'
  path '/usr/local/foo_cherry_pick'
  owner 'foobarbaz'
  group 'foobarbaz'
  creates 'foo_sub/foo1.txt'
  action :cherry_pick
end
