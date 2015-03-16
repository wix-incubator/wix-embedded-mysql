require "spec_helper"
require './libraries/default'

describe_helpers ChefArk::ProviderHelpers do

  before(:each) do
    allow_any_instance_of(ChefArk::ResourceDefaults).to receive(:file_cache_path).and_return("/var/chef/cache")
  end

  describe "#owner_command" do
    context "when on windows" do
      it "generates a icacls command" do
        with_node_attributes(platform_family: "windows")
        with_resource_properties(owner: "Bobo", path: "C:\\temp")

        expect(owner_command).to eq("icacls C:\\temp\\* /setowner Bobo")
      end
    end

    context "when not on windows" do
      it "generates a chown command" do
        with_resource_properties(owner: "MouseTrap", group: "RatCatchers", path: "/opt/rathole")

        expect(owner_command).to eq("chown -R MouseTrap:RatCatchers /opt/rathole")
      end
    end
  end

  describe "#show_deprecations" do
    context "when setting the strip_leading_dir property on the resource" do
      it "warns that it is deprecated when set to true" do
        with_resource_properties(strip_leading_dir: true)
        expect(Chef::Log).to receive(:warn)
        show_deprecations
      end

      it "warns that it is deprecated when set to false" do
        with_resource_properties(strip_leading_dir: false)
        expect(Chef::Log).to receive(:warn)
        show_deprecations
      end

    end

    context "when the strip_leading_dir property is not set on the resource" do
      it "does not produce a warning" do
        with_resource_properties(strip_leading_dir: nil)
        expect(Chef::Log).not_to receive(:warn)
        show_deprecations
      end
    end
  end

  describe "#set_dump_paths" do
    it "sets the resource's release_file" do
      with_resource_properties(extension: "tar.gz", name: "what_is_a_good_name")
      set_dump_paths
      expect(new_resource.release_file).to eq("/var/chef/cache/what_is_a_good_name.tar.gz")
    end
  end

  describe "#set_put_paths" do

    context "when the resource path is not set" do
      it "sets the resource's release_file and path" do
        with_resource_properties(extension: "jar", name: "gustav-moomoo")
        allow(defaults).to receive(:prefix_root_from_node_in_run_context) { "/opt/default" }
        set_put_paths

        expect(new_resource.release_file).to eq("/var/chef/cache/gustav-moomoo.jar")
      end
    end

    context "when the resource path has been set" do
      it "sets the resource's release_file and path" do
        with_resource_properties(
          extension: "jar",
          name: "gustav-tootoo",
          path: "/path/piece")
        set_put_paths

        expect(new_resource.release_file).to eq("/var/chef/cache/gustav-tootoo.jar")
        expect(new_resource.path).to eq("/path/piece/gustav-tootoo")
      end
    end

  end

  describe "#set_paths" do

    it "uses all the defaults" do
      with_resource_properties(extension: "jar", name: "resource_name")

      allow(defaults).to receive(:prefix_bin) { "/default/prefix/bin" }
      allow(defaults).to receive(:prefix_root) { "/default/prefix/root" }
      allow(defaults).to receive(:home_dir) { "/default/prefix/home" }
      allow(defaults).to receive(:version) { "99" }
      allow(defaults).to receive(:path) { "/default/path" }

      set_paths

      expect(new_resource.release_file).to eq("/var/chef/cache/resource_name-99.jar")
    end

    it "sets the resource's release_file" do
      with_resource_properties(
        extension: "jar",
        prefix_root: "/resource/prefix/root",
        prefix_bin: "/resource/prefix/bin",
        prefix_home: "/resource/prefix/home",
        version: "23",
        name: "resource_name")

      allow(defaults).to receive(:path) { "/default/path" }

      set_paths

      chef_config_file_cache_path = "/var/chef/cache"

      expect(new_resource.release_file).to eq("#{chef_config_file_cache_path}/resource_name-23.jar")
    end

  end

  describe "#cherry_pick_command" do
    context "when the node's platform_family is windows" do
      it "generates a 7-zip command" do
        with_node_attributes(platform_family: "windows")
        with_resource_properties(
          url: "http://website.com/windows_package.zip",
          path: "/resource/path",
          creates: "/resource/creates",
          release_file: "/resource/release_file",
          run_context: double(node: { 'ark' => { 'tar' => "sevenzip_command" } })
        )

        expect(cherry_pick_command).to eq("sevenzip_command e \"/resource/release_file\" -o\"/resource/path\" -uy -r /resource/creates")
      end
    end

    context "when the node's platform_family is not windows" do
      context 'when the unpack_type is tar_xzf' do
        it "generates a cherry pick tar command with the correct options" do

          with_resource_properties(
            url: "http://website.com/package.tar.gz",
            path: "/resource/path",
            creates: "/resource/creates",
            release_file: "/resource/release_file",
            strip_components: 0,
            run_context: double(node: { 'ark' => { 'tar' => "tar" } })
          )

          expect(cherry_pick_command).to eq("tar xzf /resource/release_file -C /resource/path /resource/creates")
        end
      end

      context 'when the unpack_type is tar_xjf' do
        it "generates a cherry pick tar command with the correct options" do

          with_resource_properties(
            url: "http://website.com/package.tar.bz2",
            path: "/resource/path",
            creates: "/resource/creates",
            release_file: "/resource/release_file",
            strip_components: 0,
            run_context: double(node: { 'ark' => { 'tar' => "tar" } })
          )

          expect(cherry_pick_command).to eq("tar xjf /resource/release_file -C /resource/path /resource/creates")
        end
      end

      context 'when the unpack_type is tar_xJf' do
        it "generates a cherry pick tar command with the correct options" do

          with_resource_properties(
            url: "http://website.com/package.txz",
            path: "/resource/path",
            creates: "/resource/creates",
            release_file: "/resource/release_file",
            strip_components: 0,
            run_context: double(node: { 'ark' => { 'tar' => "tar" } })
          )

          expect(cherry_pick_command).to eq("tar xJf /resource/release_file -C /resource/path /resource/creates")
        end
      end

      context 'when the unpack_type is unzip' do
        it "generates an unzip command" do
          with_resource_properties(
            url: "http://website.com/package.zip",
            path: "/resource/path",
            creates: "/resource/creates",
            release_file: "/resource/release_file",
            run_context: double(node: { 'ark' => { 'tar' => "unzip_command" } })
          )

          expect(cherry_pick_command).to eq("unzip -t /resource/release_file \"*//resource/creates\" ; stat=$? ;if [ $stat -eq 11 ] ; then unzip  -j -o /resource/release_file \"/resource/creates\" -d /resource/path ;elif [ $stat -ne 0 ] ; then false ;else unzip  -j -o /resource/release_file \"*//resource/creates\" -d /resource/path ;fi")
        end
      end
    end
  end

  describe "#dump_command" do
    context "when the node's platform_family is windows" do
      it "generates a 7-zip command" do

      end
    end

    context "when the node's platform_family is not windows" do
      context 'when the unpack_type is tar_xzf' do
        it "generates a tar command" do
          with_resource_properties(
            url: "http://website.com/package.tgz",
            release_file: "/resource/release_file",
            path: "/resource/path")

          expect(dump_command).to eq("tar -mxf \"/resource/release_file\" -C \"/resource/path\"")
        end
      end

      context 'when the unpack_type is tar_xjf' do
        it "generates a tar command" do
          with_resource_properties(
            url: "http://website.com/package.tbz",
            release_file: "/resource/release_file",
            path: "/resource/path")

          expect(dump_command).to eq("tar -mxf \"/resource/release_file\" -C \"/resource/path\"")
        end
      end

      context 'when the unpack_type is tar_xJf' do
        it "generates a tar command" do
          with_resource_properties(
            url: "http://website.com/package.tar.xz",
            release_file: "/resource/release_file",
            path: "/resource/path")

          expect(dump_command).to eq("tar -mxf \"/resource/release_file\" -C \"/resource/path\"")
        end
      end

      context 'when the unpack_type is unzip' do
        it "generates an unzip command" do
          with_resource_properties(
            url: "http://website.com/package.jar",
            release_file: "/resource/release_file",
            path: "/resource/path")

          expect(dump_command).to eq("unzip  -j -q -u -o \"/resource/release_file\" -d \"/resource/path\"")
        end
      end
    end
  end
end
