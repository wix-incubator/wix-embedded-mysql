if defined?(ChefSpec)
  ChefSpec::Runner.define_runner_method(:chef_handler)
  def enable_chef_handler(resource_name)
    ChefSpec::Matchers::ResourceMatcher.new(:chef_handler, :enable, resource_name)
  end
  def disable_chef_handler(resource_name)
    ChefSpec::Matchers::ResourceMatcher.new(:chef_handler, :disable, resource_name)
  end
end
