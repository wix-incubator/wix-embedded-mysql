require 'spec_helper'

describe 'maven::default' do
  let(:chef_run) { ChefSpec::ChefRunner.new(cookbook_path: ['vendor/cookbooks']).converge(described_recipe) }

  it 'includes the java recipe' do
    expect(chef_run).to include_recipe('java::default')
  end

  it 'includes the ark recipe' do
    expect(chef_run).to include_recipe('ark::default')
  end

  it 'downloads ark' do
    expect(chef_run).to install_ark('maven')
  end

  it 'writes the `/etc/mavenrc`' do
    template = chef_run.template('/etc/mavenrc')
    expect(template).to be
    expect(template.source).to eq('mavenrc.erb')
    expect(template.mode).to eq('0755')
  end
end
