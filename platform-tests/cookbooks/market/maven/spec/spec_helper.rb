require 'chefspec'
require_relative 'support/matchers/ark_matchers'

Berkshelf.ui.mute do
  Berkshelf::Berksfile.from_file('Berksfile').install(path: 'vendor/cookbooks')
end
