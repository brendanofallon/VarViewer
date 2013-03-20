#!/usr/bin/python

# Creates a new database with empty tables for all chromosomes and as well as a gene info table

import sys
import getpass
import MySQLdb as mdb

dbname = sys.argv[1]

print "Enter MySQL dba password"
passwd = getpass.getpass()

con = mdb.connect('localhost', 'root', passwd)
cur = con.cursor()

print "Creating database " + dbname

cur.execute("create database " + dbname)
cur.execute("use " + dbname)

contigPrefix = "varChr"
contigs =["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y", "MT"]

print "Initializing tables..."
for contig in contigs:
	tablename = contigPrefix + contig
	cur.execute("create table " + tablename+ "(pos int not null, ref varchar(255) not null, alt varchar(255) not null, constraint posrefalt primary key (pos, ref, alt))")
	cur.execute("grant select,alter,update,insert on " + tablename + " to 'arup'@'localhost'")

cur.execute("create table geneInfo(gene varchar(64), primary key (gene))")
cur.execute("grant select,alter,update,insert on geneInfo to 'arup'@'localhost'")

con.commit()
cur.close()
con.close()
