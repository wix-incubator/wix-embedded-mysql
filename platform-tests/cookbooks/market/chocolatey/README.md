[![Cookbook Version](https://img.shields.io/cookbook/v/chocolatey.svg)](https://supermarket.getchef.com/cookbooks/chocolatey) [![Build Status](http://img.shields.io/travis/chocolatey/chocolatey-cookbook/master.svg)](https://travis-ci.org/chocolatey/chocolatey-cookbook)

# Description

Install Chocolatey with the default recipe and manage packages with a handy resource/provider.

# Requirements

## Platform:

* Windows
* Chef 11.6 or greater

## Cookbooks:

* windows (~> 1.31)

# Notes

As of Chocolatey version
[0.9.8.24](https://github.com/chocolatey/chocolatey/blob/master/CHANGELOG.md#09824-july-3-2014)
the install directory for Chocolatey has changed from `C:\Chocolatey` to
`C:\ProgramData\Chocolatey`.

More information can be gotten from the [Chocolateywiki](https://github.com/chocolatey/chocolatey/wiki/DefaultChocolateyInstallReasoning).

# Attributes

* `node['chocolatey']['Uri']` -  Defaults to `"https://chocolatey.org/install.ps1"`.
* `node['chocolatey']['upgrade']` -  Defaults to `"true"`.

# Recipes

* chocolatey::default

# Resources

* [chocolatey](#chocolatey)

## chocolatey

### Actions

- install: Install a chocolatey package (default)
- remove: Uninstall a chocolatey package
- upgrade: Update a chocolatey package

### Attribute Parameters

- package: package to manage (default name)
- source:
- version: The version of the package to use.
- args: arguments to the installation.

# Examples

``` ruby
include_recipe 'chocolatey'

%w{sysinternals 7zip notepadplusplus GoogleChrome Console2}.each do |pack|
  chocolatey pack
end

%w{bash openssh grep}.each do |pack|
  chocolatey pack do
    source 'cygwin'
  end
end

chocolatey 'DotNet4.5'

chocolatey 'PowerShell'
```

# License and Maintainer

Maintainer:: Guilhem Lettron (<guilhem@lettron.fr>)

License:: Apache 2.0
