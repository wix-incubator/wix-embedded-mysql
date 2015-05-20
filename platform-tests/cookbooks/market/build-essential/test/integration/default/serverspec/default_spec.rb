require 'serverspec'
require 'pathname'

set :backend, :exec

# FreeBSD 10+ uses clang
compilers = if (os[:family] == 'freebsd') && (os[:release] == 10)
              %w(cc c++)
            else
              %w(gcc g++ cc c++)
            end

compilers.each do |compiler|
  describe command("#{compiler} --version") do
    its(:exit_status) { should eq 0 }
  end
end

# On FreeBSD `make` is actually BSD make
gmake_bin = if os[:family] == 'freebsd'
              'gmake'
            else
              'make'
            end

# Ensure GNU Make exists
describe command("#{gmake_bin} --version") do
  its(:exit_status) { should eq 0 }
  its(:stdout) { should match(/GNU/) }
end
