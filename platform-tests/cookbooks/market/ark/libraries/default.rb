require_relative 'platform_specific_builders'
require_relative 'resource_deprecations'
require_relative 'resource_defaults'
require_relative 'sevenzip_command_builder'
require_relative 'unzip_command_builder'
require_relative 'tar_command_builder'
require_relative 'general_owner'
require_relative 'windows_owner'

module ChefArk
  module ProviderHelpers
    extend ::ChefArk::PlatformSpecificBuilders

    generates_archive_commands_for :seven_zip,
      when_the: -> { node['platform_family'] == 'windows' },
      with_klass: ::ChefArk::SevenZipCommandBuilder

    generates_archive_commands_for :unzip,
      when_the: -> { new_resource.extension =~ /zip|war|jar/ },
      with_klass: ::ChefArk::UnzipCommandBuilder

    generates_archive_commands_for :tar,
      when_the: -> { true },
      with_klass: ::ChefArk::TarCommandBuilder

    generates_owner_commands_for :windows,
      when_the: -> { node['platform_family'] == 'windows' },
      with_klass: ::ChefArk::WindowsOwner

    generates_owner_commands_for :all_other_platforms,
      when_the: -> { true },
      with_klass: ::ChefArk::GeneralOwner

    def deprecations
      ::ChefArk::ResourceDeprecations.on(new_resource)
    end

    def show_deprecations
      deprecations.each { |message| Chef::Log.warn("DEPRECATED: #{message}") }
    end

    def defaults
      @resource_defaults ||= ::ChefArk::ResourceDefaults.new(new_resource)
    end

    def set_paths
      new_resource.extension = defaults.extension
      new_resource.prefix_bin = defaults.prefix_bin
      new_resource.prefix_root = defaults.prefix_root
      new_resource.home_dir = defaults.home_dir
      new_resource.version = defaults.version

      # TODO: what happens when the path is already set --
      #   with the current logic we overwrite it
      #   if you are in windows we overwrite it
      #   otherwise we overwrite it with the root/name-version
      new_resource.path = defaults.path
      new_resource.release_file = defaults.release_file
    end

    def set_put_paths
      new_resource.extension = defaults.extension

      # TODO: Should we be setting the prefix_root -
      #   as the prefix_root could be used in the path_with_version
      # new_resource.prefix_root = default.prefix_root
      new_resource.path = defaults.path_without_version
      new_resource.release_file = defaults.release_file_without_version
    end

    def set_dump_paths
      new_resource.extension = defaults.extension
      new_resource.release_file = defaults.release_file_without_version
    end

    def unpack_command
      archive_application.unpack
    end

    def dump_command
      archive_application.dump
    end

    def cherry_pick_command
      archive_application.cherry_pick
    end

    def unzip_command
      archive_application.unpack
    end

    def owner_command
      owner_builder_klass.new(new_resource).command
    end

    private

    def archive_application
      @archive_application ||= archive_builder_klass.new(new_resource)
    end

    def archive_builder_klass
      new_resource.extension ||= defaults.extension
      ChefArk::ProviderHelpers.archive_command_generators.find { |condition, _klass| instance_exec(&condition) }.last
    end

    def owner_builder_klass
      ChefArk::ProviderHelpers.owner_command_generators.find { |condition, _klass| instance_exec(&condition) }.last
    end
  end
end
