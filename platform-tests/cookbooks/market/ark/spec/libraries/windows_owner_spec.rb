require 'spec_helper'
require './libraries/default'

describe ChefArk::WindowsOwner do

  let(:subject) { described_class.new(resource) }

  let(:resource) { double(path: "/resource/path", owner: "/resource/owner") }

  it "generates the correct command for windows file ownership" do
    expect(subject.command).to eq("icacls /resource/path\\* /setowner /resource/owner")
  end

end
