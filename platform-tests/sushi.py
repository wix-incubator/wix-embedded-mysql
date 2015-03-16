#!/usr/bin/env python

import sys
import os
import os.path
import subprocess

MACHINES_PATH = "machines"
PROCESSES = []

def get_machines(*args):
	"""return list of machines if no args, else list with one machine in args"""
	machines = [Machine(i, name) for i, name in enumerate(os.listdir(MACHINES_PATH))
			if os.path.isdir(os.path.join(MACHINES_PATH, name))]

	if args:
		return [machines[int(args[0])]]
	else:
		return machines

class Machine(object):
	def __init__(self, index, name):
		self.index = index
		self.name = name
		self.path = "%s/%s" % (MACHINES_PATH, name)
		self.absolute_path = "%s/%s" % (os.getcwd(), self.path)

	def is_running(self):
		res = Command("vagrant status", self.absolute_path).await().iter()
		return any("The VM is running. To stop this VM, you can run `vagrant halt`" in s for s in res)

	def print_out(self, say_what):
		print "\033[1;32m  %s: %s %40s\033[0m" % (self.index, self.name, say_what)

	def print_out_simple(self, say_what):
		print "\033[1;32m  %s - %s\033[0m" % (self.name, say_what)		

class Command(object):
	def __init__(self, command, path = os.getcwd()):
		self.command = command
		self.path = path
		self.process = subprocess.Popen(self.command, stdout=subprocess.PIPE, shell = True, stderr=subprocess.STDOUT, cwd = self.path)
		PROCESSES.append(self.process)

	def print_out(self, pattern = "    %s"):
		for line in self.iter():
			print pattern % line.rstrip()
		return self

	def await(self):
		ret_code = self.process.wait()
		if ret_code != 0:
			print "\033[91mCommand '%s' failed with error code: %s\033[0m" % (self.command, ret_code)
			sys.exit(ret_code)
		else:
			return self

	def iter(self):
		return iter(self.process.stdout.readline, b'')

class Sushi(object):

	def handle_list(self):
		"""List available vagrant machines."""
		print "Available machines:"
		for box in get_machines():
			box.print_out("(path: %s)" % box.path)

	def handle_status(self, *args):
		"""Get status for handled machines."""
		print "Status for machines:"
		for box in get_machines():
			box.print_out("(running: %s)" % box.is_running())

	def handle_halt(self, *args):
		"""Shutdown machines. If no machines provided, shuts-off all of them, else one provided via args(index)."""
		for box in get_machines(*args):
			if box.is_running():
				box.print_out("(running: True) - shutting down....")
				Command("vagrant halt --force", box.absolute_path).print_out().await()
			else:
				box.print_out("(running: False) - nothing to do, skipping")
			# hack to clean-up shared-folders due to bug in vagrant 1.7.1 and later
			Command("rm -rf .vagrant/machines/default/virtualbox/synced_folders", box.absolute_path).print_out().await()


	def handle_provision(self, *args):
		"""Provision machines. If no machines provided, shuts-off all of them, else one provided via args(index)."""
		self.handle_sync()

		for box in get_machines(*args):
			if box.is_running():
				box.print_out_simple("running 'vagrant provision")
				Command("vagrant provision", box.absolute_path).print_out().await()
			else:
				box.print_out_simple("running 'vagrant up --provision'")
				Command("vagrant up --provision", box.absolute_path).print_out().await()

	def handle_destroy(self, *args):
		"""Destroy machines. If no machines provided, shuts-off all of them, else one provided via args(index)."""
		for box in get_machines(*args):
			box.print_out_simple("- destroying....")
			Command("vagrant destroy --force", box.absolute_path).print_out().await()

	def handle_sync(self):
		"""Sync sources from ../wix-embedded-mysql to cookbooks/custom/embed-mysql/files/default for chef provisioning."""
		source = "cookbooks/custom/embed-mysql/files/default/wix-embedded-mysql"

		Command("mkdir -p %s" % source).await()
		Command("rm -rf %s/*" % source).await()
		Command("cp ../pom.xml %s/" % source).await()
		Command("cp -R ../wix-embedded-mysql %s/" % source).await()
		Command("rm -rf %s/wix-embedded-mysql/target" % source).await()

def main():
    program_name, args = sys.argv[0], sys.argv[1:]
    if not args or 'help' in args:
        print 'Usage: %s command' % program_name
        print_usage()
        sys.exit(1)

    vee = Sushi()
    command = args[0]
    method = 'handle_%s' % command.replace(':', '_')
    if hasattr(vee, method):
        try:
            getattr(vee, method)(*args[1:])
        except (KeyboardInterrupt, SystemExit):
            for p in PROCESSES:
                try:
                    p.terminate()
                except:
                    pass
            print "\033[91m Script interrupted \033[0m"
            sys.exit(1)
        except:
            for p in PROCESSES:
                try:
                    p.terminate()
                except:
                    pass
            sys.exit(1)
        
        sys.exit(0)
    else:
        print 'No such command: %s' % command
        print_usage()


def print_usage():
    vee = Sushi()
    methods = [name for name in dir(vee) if name.startswith('handle_')]
    print 'Known commands:'
    for method in methods:
        command = method[7:].replace('_', ':')
        print '%20s\t%s' % (command, getattr(vee, method).__doc__)


if __name__ == '__main__':
    main()