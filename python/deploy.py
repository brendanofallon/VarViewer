#!/usr/bin/env python

# Simple deploy script for VarViewer. Just tar's up the contents of war/ directory, copies it to remote server,
# and untars and moves it to the right directory
# on remote server

import base64
import getpass
import os
import socket
import sys
import traceback

import paramiko


# setup logging
paramiko.util.log_to_file('deploy.log')

username = 'brendan'
hostname = sys.argv[1]
webAppDir = "/usr/share/tomcat6/webapps/"
destDir = webAppDir + "VarViewer/"
if (len(sys.argv)>2):
	destDir = webAppDir + sys.argv[2]
port = 22

print "Deploying to host: " + hostname + " : " + destDir

sourceDir = "/home/brendan/workspace/VarViewer/"

password = getpass.getpass('Password for %s: ' % (hostname))

#First, use sftp to copy the contents of the war/ directory to the destination directory on the host
#sftp doesn't support recursive file copying, so instead tar and gzip the sourceDir to transfer
print "** Compressing war directory"
os.chdir(sourceDir)
cmd = "tar zcf /tmp/varviewer.war.tgz war/"
os.system(cmd)
print "** done, now transferring files"

try:
	transport = paramiko.Transport((hostname, port))
	transport.connect(username = username, password = password)
	sftp = paramiko.SFTPClient.from_transport(transport)
	sftp.put('/tmp/varviewer.war.tgz', '/home/brendan/varviewer.war.tgz')
	sftp.close()
	transport.close()
except Exception, e:
	print '** Transfer failed, exception: %s: %s' % (e.__class__, e)
	traceback.print_exc()
	try:
		client.close()
	except:
        	pass
	sys.exit(1)

#Now, use paramiko Client to uncompress the archive and move it to the correct destination
try:
	client = paramiko.SSHClient()
	client.load_system_host_keys()
	client.set_missing_host_key_policy(paramiko.WarningPolicy)
	print "** Connecting to " + hostname
	client.connect(hostname, port, username, password)
	chan = client.invoke_shell()
	print repr(client.get_transport())
	print "** Moving files on " + hostname
	client.exec_command("tar zxvf varviewer.war.tgz")
	client.exec_command("mkdir " + destDir)
	client.exec_command("cp -r war/* " + destDir)
	client.exec_command("cd " + destDir)
	print "** Executing server side deployment scripts"
	stdin, stdout,stderr = client.exec_command("ls -1 " + destDir + "/deploy_scripts")
	script = stdout.readline()
	while(script):
		script = script.strip()
		if (script.endswith(".sh") or script.endswith(".py") or script.endswith(".pl")):
			print "Executing : " + script
			scriptin, scriptout, scripterr = client.exec_command(destDir + "/deploy_scripts/" + script)
			msg = scriptout.readline()
			while(msg):
				print msg
				msg = scriptout.readline()
			err = scripterr.readline()
			while(err):
				print err,
				err = scripterr.readline()
		script = stdout.readline()


	chan.close()
	client.close()

except Exception, e:
	print '*** Caught exception: %s: %s' % (e.__class__, e)
	traceback.print_exc()
	try:
		client.close()
	except:
        	pass
	sys.exit(1)
