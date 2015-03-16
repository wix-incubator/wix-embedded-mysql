module ChefSpec
  # Custom matchers for ark
  module Matchers
    define_resource_matchers([:install], [:ark], :name)
  end
end
