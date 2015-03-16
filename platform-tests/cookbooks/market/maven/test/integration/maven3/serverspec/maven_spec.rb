require 'serverspec'
include Serverspec::Helper::Exec

describe 'Maven LWRP' do
  describe file('/usr/local/foobar/lib/mysql-connector-java-5.1.19.jar') do
    it { should be_a_file }
    it { should be_mode('755') }
    it { should be_owned_by('foobarbaz') }
    it { should be_grouped_into('foobarbaz') }
  end

  describe file('/usr/local/foobar/lib/javax.persistence-2.0.0.jar') do
    it { should be_a_file }
  end

  describe file('/usr/local/foobar/lib/postgresql-9.0-801.jdbc4.jar') do
    it { should be_a_file }
  end

  describe file('/usr/local/foobar/lib/mm-mysql-2.0.13.pom') do
    it { should be_a_file }
  end

  describe file('/usr/local/foobar/lib/solr-foo.war') do
    it { should be_a_file }
  end

  describe file('/etc/mavenrc') do
    it { should be_a_file }
  end

  describe file('/usr/local/notifyOne') do
    it { should be_a_file }
  end

  describe file('/usr/local/notifyTwo') do
    it { should be_a_file }
  end
end
