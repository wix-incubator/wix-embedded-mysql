require 'spec_helper'
require './libraries/default'

describe_resource "ark" do

  before(:each) do
    Chef::Config[:file_cache_path] = "/var/chef/cache"
  end

  describe "install" do

    let(:example_recipe) { "ark_spec::install" }

    it "installs" do
      expect(chef_run).to install_ark("test_install")

      expect(chef_run).to create_directory("/usr/local/test_install-2")
      resource = chef_run.directory("/usr/local/test_install-2")
      expect(resource).to notify('execute[unpack /var/chef/cache/test_install-2.tar.gz]').to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_install-2.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_install-2.tar.gz")
      expect(resource).to notify('execute[unpack /var/chef/cache/test_install-2.tar.gz]').to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_install-2.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_install-2.tar.gz")
      expect(resource).to notify('execute[set owner on /usr/local/test_install-2]').to(:run)

      expect(chef_run).not_to run_execute("set owner on /usr/local/test_install-2")

      expect(chef_run).not_to create_template("/etc/profile.d/test_install.sh")
      expect(chef_run).not_to run_ruby_block("adding '/usr/local/test_install-2/bin' to chef-client ENV['PATH']")
    end
  end

  describe "install with binaries" do

    let(:example_recipe) { "ark_spec::install_with_binaries" }

    it "installs" do
      expect(chef_run).to install_ark("test_install")

      expect(chef_run).to create_directory("/usr/local/test_install-2")
      resource = chef_run.directory("/usr/local/test_install-2")
      expect(resource).to notify('execute[unpack /var/chef/cache/test_install-2.tar.gz]').to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_install-2.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_install-2.tar.gz")
      expect(resource).to notify('execute[unpack /var/chef/cache/test_install-2.tar.gz]').to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_install-2.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_install-2.tar.gz")
      expect(resource).to notify('execute[set owner on /usr/local/test_install-2]').to(:run)

      expect(chef_run).not_to run_execute("set owner on /usr/local/test_install-2")

      expect(chef_run).to create_link("/usr/local/bin/do_foo")
      expect(chef_run).to create_link("/usr/local/bin/do_more_foo")
      expect(chef_run).to create_link("/usr/local/test_install")

      expect(chef_run).not_to create_template("/etc/profile.d/test_install.sh")
      expect(chef_run).not_to run_ruby_block("adding '/usr/local/test_install-2/bin' to chef-client ENV['PATH']")
    end
  end

  describe "install with append_env_path" do

    context "binary is not already in the environment path" do

      let(:example_recipe) { "ark_spec::install_with_append_env_path" }

      it "installs" do
        expect(chef_run).to install_ark("test_install_with_append_env_path")

        expect(chef_run).to create_directory("/usr/local/test_install_with_append_env_path-7.0.26")
        resource = chef_run.directory("/usr/local/test_install_with_append_env_path-7.0.26")
        expect(resource).to notify("execute[unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz]").to(:run)

        expect(chef_run).to create_remote_file("/var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        resource = chef_run.remote_file("/var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        expect(resource).to notify("execute[unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz]").to(:run)

        expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        resource = chef_run.execute("unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        expect(resource).to notify("execute[set owner on /usr/local/test_install_with_append_env_path-7.0.26]").to(:run)

        expect(chef_run).not_to run_execute("set owner on /usr/local/test_install_with_append_env_path-7.0.26")

        expect(chef_run).to create_link("/usr/local/test_install_with_append_env_path")

        expect(chef_run).to create_template("/etc/profile.d/test_install_with_append_env_path.sh")
        expect(chef_run).to run_ruby_block("adding '/usr/local/test_install_with_append_env_path-7.0.26/bin' to chef-client ENV['PATH']")
      end

    end

    context "binary is already in the environment path" do

      let(:example_recipe) { "ark_spec::install_with_append_env_path" }

      # TODO: Using the ENV is terrible -- attempts to replace it with a helper
      #   method did not work or a class with a method. Explore different ways
      #   to inject the value instead of using this way.

      before do
        @old_paths = ENV['PATH']
        ENV['PATH'] = "/usr/local/test_install_with_append_env_path-7.0.26/bin"
      end

      after do
        ENV['PATH'] = @old_paths
      end

      it "installs" do

        expect(chef_run).to install_ark("test_install_with_append_env_path")

        expect(chef_run).to create_directory("/usr/local/test_install_with_append_env_path-7.0.26")
        resource = chef_run.directory("/usr/local/test_install_with_append_env_path-7.0.26")
        expect(resource).to notify("execute[unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz]").to(:run)

        expect(chef_run).to create_remote_file("/var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        resource = chef_run.remote_file("/var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        expect(resource).to notify("execute[unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz]").to(:run)

        expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        resource = chef_run.execute("unpack /var/chef/cache/test_install_with_append_env_path-7.0.26.tar.gz")
        expect(resource).to notify("execute[set owner on /usr/local/test_install_with_append_env_path-7.0.26]").to(:run)

        expect(chef_run).not_to run_execute("set owner on /usr/local/test_install_with_append_env_path-7.0.26")

        expect(chef_run).to create_link("/usr/local/test_install_with_append_env_path")

        expect(chef_run).to create_template("/etc/profile.d/test_install_with_append_env_path.sh")
        expect(chef_run).not_to run_ruby_block("adding '/usr/local/test_install_with_append_env_path-7.0.26/bin' to chef-client ENV['PATH']")

      end

    end
  end

  describe "install on windows" do

    let(:example_recipe) { "ark_spec::install_windows" }

    def node_attributes
      { platform: "windows", version: "2008R2" }
    end

    it "installs" do

      expect(chef_run).to install_ark("test_install")

      expect(chef_run).to create_directory("C:\\install")
      resource = chef_run.directory("C:\\install")
      expect(resource).to notify('execute[unpack /var/chef/cache/test_install-2.tar.gz]').to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_install-2.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_install-2.tar.gz")
      expect(resource).to notify('execute[unpack /var/chef/cache/test_install-2.tar.gz]').to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_install-2.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_install-2.tar.gz")
      expect(resource).to notify("execute[set owner on C:\\install]").to(:run)

      expect(chef_run).not_to run_execute("set owner on C:\\install")

      expect(chef_run).not_to create_link("/usr/local/bin/do_foo")
      expect(chef_run).not_to create_link("/usr/local/bin/do_more_foo")
      expect(chef_run).not_to create_link("/usr/local/test_install")

      expect(chef_run).not_to create_template("/etc/profile.d/test_install.sh")
      expect(chef_run).not_to run_ruby_block("adding 'C:\\install/bin' to chef-client ENV['PATH']")

    end
  end

  describe "put" do

    let(:example_recipe) { "ark_spec::put" }

    it "puts" do
      expect(chef_run).to put_ark("test_put")

      expect(chef_run).to create_directory("/usr/local/test_put")
      resource = chef_run.directory("/usr/local/test_put")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_put.tar.gz]").to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_put.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_put.tar.gz")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_put.tar.gz]").to(:run)

      expect(chef_run).to_not run_execute("unpack /var/chef/cache/test_put.tar.gz")
      expect(chef_run).to_not run_execute("set owner on /usr/local/test_put")
    end

  end

  describe "dump" do

    let(:example_recipe) { "ark_spec::dump" }

    it "dumps" do
      expect(chef_run).to dump_ark("test_dump")

      expect(chef_run).to create_directory("/usr/local/foo_dump")
      resource = chef_run.directory("/usr/local/foo_dump")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_dump.zip]").to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_dump.zip")
      resource = chef_run.remote_file("/var/chef/cache/test_dump.zip")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_dump.zip]").to(:run)

      expect(chef_run).to_not run_execute("unpack /var/chef/cache/test_dump.zip")
      expect(chef_run).to_not run_execute("set owner on /usr/local/foo_dump")
    end
  end

  describe "unzip" do

    let(:example_recipe) { "ark_spec::unzip" }

    it "unzips" do
      expect(chef_run).to unzip_ark("test_unzip")

      expect(chef_run).to create_directory("/usr/local/foo_dump")
      resource = chef_run.directory("/usr/local/foo_dump")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_unzip.zip]").to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_unzip.zip")
      resource = chef_run.remote_file("/var/chef/cache/test_unzip.zip")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_unzip.zip]").to(:run)

      expect(chef_run).to_not run_execute("unpack /var/chef/cache/test_unzip.zip")
      expect(chef_run).to_not run_execute("set owner on /usr/local/foo_dump")
    end
  end

  describe "cherry_pick" do

    let(:example_recipe) { "ark_spec::cherry_pick" }

    it "cherry picks" do

      expect(chef_run).to cherry_pick_ark("test_cherry_pick")

      expect(chef_run).to create_directory("/usr/local/foo_cherry_pick")
      resource = chef_run.directory("/usr/local/foo_cherry_pick")
      expect(resource).to notify("execute[cherry_pick foo_sub/foo1.txt from /var/chef/cache/test_cherry_pick.tar.gz]").to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_cherry_pick.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_cherry_pick.tar.gz")
      expect(resource).to notify("execute[cherry_pick foo_sub/foo1.txt from /var/chef/cache/test_cherry_pick.tar.gz]").to(:run)

      resource = chef_run.execute("cherry_pick foo_sub/foo1.txt from /var/chef/cache/test_cherry_pick.tar.gz")
      expect(resource).to notify("execute[set owner on /usr/local/foo_cherry_pick]").to(:run)

      expect(chef_run).to_not run_execute("cherry_pick foo_sub/foo1.txt from /var/chef/cache/test_cherry_pick.tar.gz")
      expect(chef_run).to_not run_execute("set owner on /usr/local/foo_cherry_pick")
    end
  end

  describe "setup_py_build" do
    let (:example_recipe) { "ark_spec::setup_py_build" }

    it "builds with python setup.py" do
      expect(chef_run).to setup_py_build_ark('test_setup_py_build')

      expect(chef_run).to create_directory("/usr/local/test_setup_py_build-1")
      resource = chef_run.directory("/usr/local/test_setup_py_build-1")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_setup_py_build-1.tar.gz]").to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_setup_py_build-1.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_setup_py_build-1.tar.gz")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_setup_py_build-1.tar.gz]").to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_setup_py_build-1.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_setup_py_build-1.tar.gz")
      expect(resource).to notify("execute[set owner on /usr/local/test_setup_py_build-1]")
      expect(resource).to notify("execute[python setup.py build /usr/local/test_setup_py_build-1]")

      expect(chef_run).not_to run_execute("set owner on /usr/local/test_setup_py_build-1")
      expect(chef_run).not_to run_execute("python setup.py build /usr/local/test_setup_py_build-1")
    end
  end

  describe "setup_py_install" do
    let (:example_recipe) { "ark_spec::setup_py_install" }

    it "installs with python setup.py" do
      expect(chef_run).to setup_py_install_ark('test_setup_py_install')

      expect(chef_run).to create_directory("/usr/local/test_setup_py_install-1")
      expect(chef_run).to create_remote_file("/var/chef/cache/test_setup_py_install-1.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_setup_py_install-1.tar.gz")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_setup_py_install-1.tar.gz]").to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_setup_py_install-1.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_setup_py_install-1.tar.gz")
      expect(resource).to notify("execute[set owner on /usr/local/test_setup_py_install-1]")
      expect(resource).to notify("execute[python setup.py install /usr/local/test_setup_py_install-1]")

      expect(chef_run).not_to run_execute("set owner on /usr/local/test_setup_py_install-1")
      expect(chef_run).not_to run_execute("python setup.py install /usr/local/test_setup_py_install-1")
    end
  end

  describe "setup_py" do
    let (:example_recipe) { "ark_spec::setup_py" }

    it "runs with python setup.py" do
      expect(chef_run).to setup_py_ark('test_setup_py')

      expect(chef_run).to create_directory("/usr/local/test_setup_py-1")
      expect(chef_run).to create_remote_file("/var/chef/cache/test_setup_py-1.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_setup_py-1.tar.gz")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_setup_py-1.tar.gz]").to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_setup_py-1.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_setup_py-1.tar.gz")
      expect(resource).to notify("execute[set owner on /usr/local/test_setup_py-1]")
      expect(resource).to notify("execute[python setup.py /usr/local/test_setup_py-1]")

      expect(chef_run).not_to run_execute("set owner on /usr/local/test_setup_py")
      expect(chef_run).not_to run_execute("python setup.py /usr/local/test_setup_py")
    end
  end

  describe "install_with_make" do

    let(:example_recipe) { "ark_spec::install_with_make" }

    it "installs with make" do
      expect(chef_run).to install_with_make_ark("test_install_with_make")

      expect(chef_run).to create_directory("/usr/local/test_install_with_make-1.5")
      resource = chef_run.directory("/usr/local/test_install_with_make-1.5")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_install_with_make-1.5.tar.gz]").to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_install_with_make-1.5.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_install_with_make-1.5.tar.gz")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_install_with_make-1.5.tar.gz]").to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_install_with_make-1.5.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_install_with_make-1.5.tar.gz")
      expect(resource).to notify("execute[set owner on /usr/local/test_install_with_make-1.5]")
      expect(resource).to notify("execute[autogen /usr/local/test_install_with_make-1.5]")
      expect(resource).to notify("execute[configure /usr/local/test_install_with_make-1.5]")
      expect(resource).to notify("execute[make /usr/local/test_install_with_make-1.5]")
      expect(resource).to notify("execute[make install /usr/local/test_install_with_make-1.5]")

      expect(chef_run).not_to run_execute("set owner on /usr/local/test_install_with_make-1.5")
      expect(chef_run).not_to run_execute("autogen /usr/local/test_install_with_make-1.5")
      expect(chef_run).not_to run_execute("configure /usr/local/test_install_with_make-1.5")
      expect(chef_run).not_to run_execute("make /usr/local/test_install_with_make-1.5")
      expect(chef_run).not_to run_execute("make install /usr/local/test_install_with_make-1.5")
    end
  end

  describe "configure" do

    let(:example_recipe) { "ark_spec::configure" }

    it "configures" do

      expect(chef_run).to configure_ark("test_configure")

      expect(chef_run).to create_directory("/usr/local/test_configure-1")
      resource = chef_run.directory("/usr/local/test_configure-1")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_configure-1.tar.gz]").to(:run)

      expect(chef_run).to create_remote_file("/var/chef/cache/test_configure-1.tar.gz")
      resource = chef_run.remote_file("/var/chef/cache/test_configure-1.tar.gz")
      expect(resource).to notify("execute[unpack /var/chef/cache/test_configure-1.tar.gz]").to(:run)

      expect(chef_run).not_to run_execute("unpack /var/chef/cache/test_configure-1.tar.gz")
      resource = chef_run.execute("unpack /var/chef/cache/test_configure-1.tar.gz")
      expect(resource).to notify("execute[set owner on /usr/local/test_configure-1]")
      expect(resource).to notify("execute[autogen /usr/local/test_configure-1]")
      expect(resource).to notify("execute[configure /usr/local/test_configure-1]")

      expect(chef_run).not_to run_execute("set owner on /usr/local/test_configure-1")
      expect(chef_run).not_to run_execute("autogen /usr/local/test_configure-1")
      expect(chef_run).not_to run_execute("configure /usr/local/test_configure-1")
    end
  end

end
