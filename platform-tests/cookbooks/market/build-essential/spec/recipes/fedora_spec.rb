require 'spec_helper'

describe 'build-essential::_fedora' do
  let(:chef_run) do
    ChefSpec::ServerRunner.new(platform: 'fedora', version: '19')
      .converge(described_recipe)
  end

  it 'installs the correct packages' do
    expect(chef_run).to install_package('autoconf')
    expect(chef_run).to install_package('bison')
    expect(chef_run).to install_package('flex')
    expect(chef_run).to install_package('gcc')
    expect(chef_run).to install_package('gcc-c++')
    expect(chef_run).to install_package('gettext')
    expect(chef_run).to install_package('kernel-devel')
    expect(chef_run).to install_package('make')
    expect(chef_run).to install_package('m4')
    expect(chef_run).to install_package('ncurses-devel')
  end
end
