require 'spec_helper'

describe 'Host File' do

describe file('c:/opscode') do
  it { should be_directory }
end

end