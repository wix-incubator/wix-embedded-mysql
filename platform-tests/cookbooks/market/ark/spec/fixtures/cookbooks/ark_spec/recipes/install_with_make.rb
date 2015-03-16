
ark 'test_install_with_make' do
  url 'http://haproxy.1wt.eu/download/1.5/src/snapshot/haproxy-ss-20120403.tar.gz'
  version '1.5'
  checksum 'ba0424bf7d23b3a607ee24bbb855bb0ea347d7ffde0bec0cb12a89623cbaf911'
  make_opts ['TARGET=linux26']
  action :install_with_make
end
