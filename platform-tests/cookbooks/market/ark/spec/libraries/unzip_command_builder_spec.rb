require 'spec_helper'
require './libraries/default'

describe ChefArk::UnzipCommandBuilder do

  let(:subject) { described_class.new(resource) }

  let(:resource) do
    double(release_file: "release_file",
           creates: "creates",
           path: "path",
           strip_components: 0)
  end

  describe "#unpack" do

    context 'when the resource does not strip components' do

      it "generates the correct command" do
        expected_command = "unzip -q -u -o release_file -d path"
        expect(subject.unpack).to eq(expected_command)
      end
    end

    context 'when the resource does strip components' do

      let(:resource) do
        double(release_file: "release_file",
               creates: "creates",
               path: "path",
               strip_components: 1)
      end

      it "generates the correct command" do
        expected_command = "unzip -q -u -o release_file -d temp_directory && rsync -a temp_directory/*/ path && rm -rf temp_directory"
        allow(subject).to receive(:make_temp_directory) { "temp_directory" }
        expect(subject.unpack).to eq(expected_command)
      end
    end
  end

  describe "#dump" do
    it "generates the correct command" do
      expected_command = "unzip  -j -q -u -o \"release_file\" -d \"path\""
      expect(subject.dump).to eq(expected_command)
    end
  end

  describe "#cherry_pick" do
    it "generates the correct command" do
      expected_command = "unzip -t release_file \"*/creates\" ; stat=$? ;if [ $stat -eq 11 ] ; then unzip  -j -o release_file \"creates\" -d path ;elif [ $stat -ne 0 ] ; then false ;else unzip  -j -o release_file \"*/creates\" -d path ;fi"
      expect(subject.cherry_pick).to eq(expected_command)
    end
  end
end
