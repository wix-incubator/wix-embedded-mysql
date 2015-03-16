require 'spec_helper'

describe 'build-essential::_suse' do
  let(:chef_run) do
    ChefSpec::ServerRunner.new(platform: 'suse', version: '11.3')
      .converge(described_recipe)
  end

  it 'installs the correct packages' do
    expect(chef_run).to install_package('autoconf')
    expect(chef_run).to install_package('bison')
    expect(chef_run).to install_package('flex')
    expect(chef_run).to install_package('gcc')
    expect(chef_run).to install_package('gcc-c++')
    expect(chef_run).to install_package('kernel-default-devel')
    expect(chef_run).to install_package('make')
    expect(chef_run).to install_package('m4')
  end
end
