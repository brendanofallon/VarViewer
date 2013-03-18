#!/usr/bin/python

#Import variant annotation data into the database


import sys
import MySQLdb as mdb

separator = "\t"

if ((len(sys.argv) < 5) or sys.argv[1]=="help"):
	print
	print "Import variant annotation data into the sql database"
	print
	print "Usage: sqlImport.py [datafile.csv] [database name] [tablename] [columnname]"
	print
	print " ARG count: " + str(len(sys.argv))
	exit()

infile = sys.argv[1]
dbname = sys.argv[2]
tablename = sys.argv[3]
column = sys.argv[4]

#Open input file and read first non-blank line as the 'header'
fh = open(infile, "r")
line = fh.readline().strip()
while(len(line)==0):
	line = fh.readline().strip()

headerTokens = line.split(separator)

try:
	columnIndex = headerTokens.index(column)
except ValueError:
	print "Could not find column header '" + column + "' in " + infile
	exit()
	



	
#Open db connection
con = mdb.connect('localhost', 'arup', 'arup', dbname)
cur = con.cursor()

#Add new column to the table
colType = "VARCHAR(255)"
if (len(sys.argv) > 5):
	colType = sys.argv[5]


cur.execute("alter table " + tablename + " add " + column + " " + colType)

#Add each row to the table, blank lines and those starting with # are ignored

added = 0
updated = 0
line = fh.readline()
while(line):
	line = line.strip()
	if (len(line)==0 or (line[0] == "#")):
		line = fh.readline()
		continue;
	toks = line.split(separator)
	if (not (len(toks) == len(headerTokens))):
		print "Incorrect number of tokens (" + str(len(toks)) + " on line: " + line
		line = fh.readline()
		continue

	pos = int(toks[1])
	ref = toks[2]
	alt = toks[3]

	value = toks[columnIndex]
	if (colType.count("DECIMAL") > 0):
		if (value == "." or value=="-"):
			value = 0.0
		else:
			value = float(toks[columnIndex])
	if (colType.count("INT") > 0):
		if (value=="." or value=="-"):
			value = 1
		else:
			value = int(toks[columnIndex])
	if (colType.count("VARCHAR") > 0):
		value = "\"" + value + "\""

	#If variant at position already exists, we 'update', otherwise, make a new row
	cur.execute("select pos from " + dbname + "." + tablename + " where pos=" + str(pos))
	data = cur.fetchone()

	if (data == None):
		cur.execute("insert into " + dbname + "." + tablename + "(pos, ref, alt, " + column + ") values(" + str(pos) + ", \"" + ref + "\", \"" + alt + "\", " + str(value) + ")")
		added = added + 1
	else:
		cur.execute("update " + tablename + " set " + column + "=" + str(value) + " where pos=" + str(pos))
		updated = updated + 1
	line = fh.readline()

con.commit()

print " Created new column " + column + " of type " + colType + " in " + dbname + "." + tablename
print " Added " + str(added) + " new rows"
print " Updated " + str(updated) + " existing rows"

cur.close()
con.close()
