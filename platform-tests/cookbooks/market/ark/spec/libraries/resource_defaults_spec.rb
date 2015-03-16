require 'spec_helper'
require './libraries/default'

describe ChefArk::ResourceDefaults do

  before(:each) do
    allow_any_instance_of(ChefArk::ResourceDefaults).to receive(:file_cache_path).and_return("/var/chef/cache")
  end

  describe "#extension" do
    it "returns the extension parameter specified on the resource" do
      resource = double(extension: "me")
      defaults = described_class.new(resource)
      expect(defaults.extension).to eq "me"
    end

    context "when the extension is nil" do

      it "creates an extension based on the file specified in the URL" do
        resource = double(extension: nil, url: "http://localhost/file.tgz")
        defaults = described_class.new(resource)
        expect(defaults.extension).to eq "tgz"
      end

      it "creates an extension based on the file specified in the URL (and not other words with similar names to extensions)" do
        resource = double(extension: nil, url: "https://jar.binfiles.tbz/file.tar.bz2")
        defaults = described_class.new(resource)
        expect(defaults.extension).to eq "tar.bz2"
      end

      it "creates an extension for tar files" do
        resource = double(extension: nil, url: "https://jar.binfiles.tbz/file.tar")
        defaults = described_class.new(resource)
        expect(defaults.extension).to eq "tar"
      end

      context "when the archive format is not supported" do

        it "it returns a nil extension" do
          resource = double(extension: nil, url: "http://localhost/file.stuffit")
          defaults = described_class.new(resource)
          expect(defaults.extension).to eq nil
        end

      end

      context "when the url contains a query string" do

        it "creates an extension based on the file specified in the URL" do
          resource = double(extension: nil, url: "http://localhost/file.version.txz-bin?latest=true")
          defaults = described_class.new(resource)
          expect(defaults.extension).to eq "txz"
        end

      end

    end
  end

  describe "#prefix_bin" do
    context 'when the prefix bin has been specified' do
      it "uses the value specified" do
        resource = double(prefix_bin: "prefix_bin")
        defaults = described_class.new(resource)
        expect(defaults.prefix_bin).to eq "prefix_bin"
      end
    end

    context 'when the prefix bin has not been specified' do
      it "uses the value on the node" do
        resource = double(prefix_bin: nil)
        defaults = described_class.new(resource)
        allow(defaults).to receive(:prefix_bin_from_node_in_run_context) { "node_bin" }
        expect(defaults.prefix_bin).to eq "node_bin"
      end
    end
  end

  describe "#prefix_root" do
    context 'when the prefix root has been specified' do
      it "uses the value specified" do
        resource = double(prefix_root: "prefix_root")
        defaults = described_class.new(resource)
        expect(defaults.prefix_root).to eq "prefix_root"

      end
    end

    context 'when the prefix root has not been specified' do
      it "uses the value on the node" do
        resource = double(prefix_root: nil)
        defaults = described_class.new(resource)
        allow(defaults).to receive(:prefix_root_from_node_in_run_context) { "node_root" }
        expect(defaults.prefix_root).to eq "node_root"
      end
    end
  end

  describe "#home_dir" do
    context 'when the home dir has been specified' do
      it "uses the value specified" do
        resource = double(prefix_home: "prefix_home", name: "application", home_dir: "home_dir")
        defaults = described_class.new(resource)
        expect(defaults.home_dir).to eq "home_dir"
      end
    end

    context 'when the prefix home has been specified' do
      it "uses the value specified" do
        resource = double(prefix_home: "prefix_home", name: "application", :home_dir => nil)
        defaults = described_class.new(resource)
        expect(defaults.home_dir).to eq "prefix_home/application"
      end
    end

    context 'when the prefix home has not been specified' do
      it "uses the value on the node" do
        resource = double(prefix_home: nil, name: "application", :home_dir => nil)
        defaults = described_class.new(resource)
        allow(defaults).to receive(:prefix_home_from_node_in_run_context) { "node_home" }
        expect(defaults.home_dir).to eq "node_home/application"
      end
    end
  end

  describe "#version" do
    context 'when the version is specified' do
      it "uses the version on the resource" do
        resource = double(version: "99")
        defaults = described_class.new(resource)
        expect(defaults.version).to eq "99"
      end
    end

    context 'when the version is not specified' do
      it "defaults to a version" do
        resource = double(version: nil)
        defaults = described_class.new(resource)
        expect(defaults.version).to eq "1"
      end
    end
  end

  describe "#path" do
    context 'when on windows' do
      it "uses the windows install dir" do
        resource = double(extension: "tgz", win_install_dir: "C:\\win_install_dir")
        defaults = described_class.new(resource)
        allow(defaults).to receive(:windows?) { true }
        expect(defaults.path).to eq "C:\\win_install_dir"
      end
    end

    context 'when not on windows' do
      it "gives the correct default" do
        resource = double(name: "application", prefix_root: "prefix/root", version: "99")
        defaults = described_class.new(resource)
        allow(defaults).to receive(:windows?) { false }
        expect(defaults.path).to eq "prefix/root/application-99"
      end
    end
  end

  describe "#path_without_version" do
    context 'when the path is specified' do
      it "gives the correct default" do
        resource = double(extension: "tgz", name: "filename", path: "path")
        defaults = described_class.new(resource)
        expect(defaults.path_without_version).to eq "path/filename"
      end
    end

    context 'when the path is not specified' do
      it "gives the correct default" do
        resource = double(extension: "tgz", name: "filename", path: nil)
        defaults = described_class.new(resource)
        allow(defaults).to receive(:prefix_root_from_node_in_run_context) { "prefix/root" }
        expect(defaults.path_without_version).to eq "prefix/root/filename"
      end
    end
  end

  describe "#release_file" do
    it "gives the correct default" do
      resource = double(extension: "tgz", version: "1.1", name: "filename")
      defaults = described_class.new(resource)
      expect(defaults.release_file).to eq "/var/chef/cache/filename-1.1.tgz"
    end
  end

  describe "#release_file_without_version" do
    it "gives the correct default" do
      resource = double(extension: "zip", name: "filename")
      defaults = described_class.new(resource)
      expect(defaults.release_file_without_version).to eq "/var/chef/cache/filename.zip"
    end
  end

end
