require 'spec_helper'
require './libraries/default'

describe ChefArk::GeneralOwner do

  let(:subject) { described_class.new(resource) }

  let(:resource) do
    double(owner: "owner",
           group: "group",
           path: "/resource/path")
  end

  it "generates the correct command for windows file ownership" do
    expect(subject.command).to eq("chown -R owner:group /resource/path")
  end

end
