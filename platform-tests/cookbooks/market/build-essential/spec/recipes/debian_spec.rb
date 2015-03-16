require 'spec_helper'

describe 'build-essential::_debian' do
  let(:chef_run) do
    ChefSpec::ServerRunner.new(platform: 'debian', version: '7.4')
      .converge(described_recipe)
  end

  it 'installs the correct packages' do
    expect(chef_run).to install_package('autoconf')
    expect(chef_run).to install_package('binutils-doc')
    expect(chef_run).to install_package('bison')
    expect(chef_run).to install_package('build-essential')
    expect(chef_run).to install_package('flex')
    expect(chef_run).to install_package('gettext')
    expect(chef_run).to install_package('ncurses-dev')
  end
end
