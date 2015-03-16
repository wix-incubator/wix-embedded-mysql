
ark 'foo_dont_strip' do
  version '2'
  url 'https://github.com/opscode-cookbooks/ark/raw/master/files/default/foo.tar.gz'
  checksum '5996e676f17457c823d86f1605eaa44ca8a81e70d6a0e5f8e45b51e62e0c52e8'
  strip_components 0
  action :install
end
