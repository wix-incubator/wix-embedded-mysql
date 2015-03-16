
ark 'test_dump' do
  url 'https://github.com/opscode-cookbooks/ark/raw/master/files/default/foo.zip'
  checksum 'deea3a324115c9ca0f3078362f807250080bf1b27516f7eca9d34aad863a11e0'
  path '/usr/local/foo_dump'
  creates 'foo1.txt'
  owner 'foobarbaz'
  group 'foobarbaz'
  action :dump
end
