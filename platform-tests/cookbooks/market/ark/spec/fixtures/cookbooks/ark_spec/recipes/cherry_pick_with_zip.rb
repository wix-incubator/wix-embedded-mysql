
ark 'cherry_pick_with_zip' do
  url 'https://github.com/opscode-cookbooks/ark/raw/master/files/default/foo.zip'
  checksum 'deea3a324115c9ca0f3078362f807250080bf1b27516f7eca9d34aad863a11e0'
  path '/usr/local/foo_cherry_pick_from_zip'
  creates 'foo_sub/foo1.txt'
  action :cherry_pick
end
