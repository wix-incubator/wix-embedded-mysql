require 'chefspec'
ChefSpec::Coverage.start!

RSpec.configure do |config|
  config.order = 'random'
end
