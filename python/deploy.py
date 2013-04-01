#!/usr/bin/env python

# Simple deploy script for varviewer, right now just copies contents of war/ directory to /usr/share/tomcat/webapps/VarViewer
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

# get hostname
username = 'brendan'
hostname = sys.argv[1]
port = 22

password = getpass.getpass('Password for %s: ' % (hostname))

#First, use sftp to copy the contents of the war/ directory to the destination directory on the host
sourceDir = "/home/brendan/workspace/VarViewer/"
destDir = "/usr/share/tomcat6/webapps/VarViewer/"

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
	client.exec_command("cp -r war/* " + destDir)
	client.exec_command("cd " + destDir)
	client.exec_command("cp spring-" + hostname + ".xml spring.xml")

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
