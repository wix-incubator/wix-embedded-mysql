require 'spec_helper'

describe 'build-essential::_rhel' do
  let(:chef_run) do
    ChefSpec::ServerRunner.new(platform: 'redhat', version: '6.5')
      .converge(described_recipe)
  end

  it 'installs the correct packages' do
    expect(chef_run).to install_package('autoconf')
    expect(chef_run).to install_package('bison')
    expect(chef_run).to install_package('flex')
    expect(chef_run).to install_package('gcc')
    expect(chef_run).to install_package('gcc-c++')
    expect(chef_run).to install_package('kernel-devel')
    expect(chef_run).to install_package('make')
    expect(chef_run).to install_package('m4')
    expect(chef_run).to install_package('patch')
  end

  context 'on rhel < 6' do
    let(:chef_run) do
      ChefSpec::Runner.new(platform: 'redhat', version: '5.9')
        .converge(described_recipe)
    end

    it 'installs more packages' do
      expect(chef_run).to install_package('gcc44')
      expect(chef_run).to install_package('gcc44-c++')
    end
  end
end
