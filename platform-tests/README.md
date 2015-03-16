# About
A module containing vagrant-based environments to run embedded-mysql tests on.

Simple way to run these tests is via parent module:
```
mvn clean verify -DskipTests -Pfull
```
What it does is executed sushi.py bia exec plugin with commands:
 - sushi.py halt - stops all handled machines. Handled machines are all under machines folder;
 - sushi.py provision - gets each machine up and running, runs chef and tests on target machine;
 - sushi.py halt - clean-up, stop all machines;

# Requirements
 - *nix based OS - tested on OSX;
 - latest version of vagrant;
 - latest version of VirtualBox;

# Preparation - OS X

Just run the following commands and you should be good to go:

```bash
$ ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
$ brew tap caskroom/cask
$ brew cask install virtualbox
$ brew cask install vagrant
```

# Running
Environment tests can be run in a multitude of ways

**Maven**
```
mvn clean test -DskipPlatformTests=false
```

**Sushi - helper tool to manage machines**

Simple way to provision machines:
```
$ sushi provision; sushi halt;
```

If you want to provision single machine, you can do:
```
$ ./sushi.py list
Available machines:
  0: linux32-jdk7            (path: machines/linux32-jdk7)
  1: windows7-64-jdk7        (path: machines/windows7-64-jdk7)
$ ./sushi.py provision 0
```

To analyse possible commands further:
```
$ ./sushi.py
```

**Vagrant**

It's always possible to fallback to pure vagrant commands as machines are simple vagrant boxes with chef-solo provisioner.

Just make sure you synced sources via './sushi.py sync' before running vagrant machines located in 'machines/' folder.

# IMPROVEMENTS
 - make market cookbook fetch dynamic - via script and not in repo - easier to keep them up-to-date;
 - maybe cache mysql versions to local disk and use shared folders? For *nix it's ok unless probs with permissions, but for win it's somewhat harder and makes solution more fragile although faster;
 - maybe destroy vm before every run? Will be more stable, but way slower.

