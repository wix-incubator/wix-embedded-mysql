require "spec_helper"

describe_recipe "ark::default" do

  def node_attributes
    { platform: "windows", version: "2008R2" }
  end

  #
  # NOTE: This is a work around to the fact that when you specify a cookbook
  # as a suggestion or recommendation the attributes are not proprerly loaded
  # unless the recipe is on the run_list.
  #
  let(:chef_run) { ChefSpec::Runner.new(node_attributes).converge("7-zip", described_recipe) }

  let(:expected_packages) do
    %w( libtool autoconf unzip rsync make gcc autogen xz-lzma-compat )
  end

  it "does not installs packages" do
    expected_packages.each do |package|
      expect(chef_run).not_to install_package(package)
    end
  end

  it "does include the 7-zip recipe" do
    expect(chef_run).to include_recipe("7-zip")
  end

  context "sets default attributes" do

    it "tar binary" do
      expect(default_cookbook_attribute("tar")).to eq %("\\7-zip\\7z.exe")
    end

  end

end
