#!/usr/bin/python

#Import variant annotation data into the database


import sys
import MySQLdb as mdb

separator = "\t"

if ((len(sys.argv) < 4) or sys.argv[1]=="help"):
	print
	print "Import variant annotation data into the sql database"
	print
	print "Usage: sqlImportGene.py [datafile.csv] [database name] [columnname]"
	print
	print " ARG count: " + str(len(sys.argv))
	exit()

infile = sys.argv[1]
dbname = sys.argv[2]
tablename = "geneInfo"
column = sys.argv[3]
newcolumnName = column;
if (column.count(":") > 0):
	idx = column.index(":")
	newcolumnName = column[idx+1:]
	column = column[0:idx]

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


#Determine if a column with the given name already exists. If so, use it. If not, create a new one.
colType = "VARCHAR(256)"
if (len(sys.argv) > 4):
	colType = sys.argv[4]

cur.execute("show columns from " + tablename)
rows = cur.fetchall()
found = False
for row in rows:
	if (newcolumnName == row[0]):
		found = True

if (not found):
	print "Creating new column " + newcolumnName + " of type " + colType
	cur.execute("alter table " + tablename + " add `" + newcolumnName + "` " + colType)

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

	gene = toks[0]

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
		value = "\"" + value.replace("\"", "\\\"") + "\""

	#If variant at position already exists, we 'update', otherwise, make a new row
	cur.execute("select gene from " + dbname + "." + tablename + " where gene=\"" + gene + "\"")
	data = cur.fetchone()

	if (data == None):
		cur.execute("insert into " + dbname + "." + tablename + "(gene, `" + newcolumnName + "`) values(\"" + gene + "\", " + str(value) + ")")
		added = added + 1
	else:
		cur.execute("update " + tablename + " set `" + newcolumnName + "`=" + str(value) + " where gene=\"" + gene + "\"")
		updated = updated + 1
	line = fh.readline()
	con.commit()

print " Created new column with name " + newcolumnName + " of type " + colType + " from column " + column + " in " + dbname + "." + tablename
print " Added " + str(added) + " new rows"
print " Updated " + str(updated) + " existing rows"

cur.close()
con.close()
