source "http://api.berkshelf.com"

metadata

cookbook '7-zip'
cookbook 'windows'

group :integration do
  cookbook 'apt'
  cookbook 'minitest-handler'
  cookbook 'build-essential'
end


cookbook "ark_spec", path: "spec/fixtures/cookbooks/ark_spec"