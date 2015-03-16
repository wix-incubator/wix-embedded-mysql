require "spec_helper"

describe_recipe "ark::default" do

  let(:expected_core_packages) do
    %w( libtool autoconf unzip rsync make gcc autogen )
  end

  it "installs core packages" do
    expected_core_packages.each do |package|
      expect(chef_run).to install_package(package)
    end
  end

  it "does not install the gcc-c++ package" do
    expect(chef_run).not_to install_package("gcc-c++")
  end

  it "does not include the 7-zip recipe" do
    expect(chef_run).not_to include_recipe("7-zip")
  end

  context "sets default attributes" do

    it "apache mirror" do
      expect(default_cookbook_attribute("apache_mirror")).to eq "http://apache.mirrors.tds.net"
    end

    it "prefix root" do
      expect(default_cookbook_attribute("prefix_root")).to eq "/usr/local"
    end

    it "prefix bin" do
      expect(default_cookbook_attribute("prefix_bin")).to eq "/usr/local/bin"
    end

    it "prefix home" do
      expect(default_cookbook_attribute("prefix_home")).to eq "/usr/local"
    end

    it "tar binary" do
      expect(default_cookbook_attribute("tar")).to eq "/bin/tar"
    end

  end

end
