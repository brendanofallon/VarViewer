#!/usr/bin/python

#Import variant annotation data from dbNSFP files into the database.


import sys
import MySQLdb as mdb

#chr	pos(1-coor)	ref	alt	aaref	aaalt	hg18_pos(1-coor)	genename	Uniprot_acc	Uniprot_id	Uniprot_aapos	Interpro_domain	cds_strand	refcodon	SLR_test_statistic 	codonpos	fold-degenerate	Ancestral_allele	Ensembl_geneid	Ensembl_transcriptid	aapos	SIFT_score	Polyphen2_HDIV_score	Polyphen2_HDIV_pred	Polyphen2_HVAR_score	Polyphen2_HVAR_pred	LRT_score	LRT_pred	MutationTaster_score	MutationTaster_pred	MutationAssessor_score	MutationAssessor_pred	GERP++_NR	GERP++_RS	phyloP	29way_pi	29way_logOdds	LRT_Omega	UniSNP_ids	1000Gp1_AC	1000Gp1_AF	1000Gp1_AFR_AC	1000Gp1_AFR_AF	1000Gp1_EUR_AC	1000Gp1_EUR_AF	1000Gp1_AMR_AC	1000Gp1_AMR_AF	1000Gp1_ASN_AC	1000Gp1_ASN_AF	ESP6500_AA_AF	ESP6500_EA_AF

columnsToImport = [ ["1000Gp1_AF:pop.freq", "DECIMAL(6,4)"], 
	["1000Gp1_AFR_AF:afr.freq", "DECIMAL(6,4)"],
	["1000Gp1_EUR_AF:eur.freq", "DECIMAL(6,4)"],
	["1000Gp1_ASN_AF:asn.freq", "DECIMAL(6,4)"],
	["1000Gp1_AMR_AF:amr.freq", "DECIMAL(6,4)"],
	["Polyphen2_HDIV_score:pp.score", "DECIMAL(6,4)"],
	["Polyphen2_HVAR_score:pp.hvar.score", "DECIMAL(6,4)"],
	["GERP++_NR:gerp.nr.score", "DECIMAL(6,4)"],
	["GERP++_RS:gerp.score", "DECIMAL(6,4)"],
	["phyloP:phylop.score", "DECIMAL(6,4)"],
	["LRT_score:lrt.score", "DECIMAL(6,4)"],
	["29way_logOdds:siphy.score", "DECIMAL(6,4)"],
	["MutationAssessor_score:mut.assessor.score", "DECIMAL(6,4)"],
	["MutationTaster_score:mt.score", "DECIMAL(6,4)"],
	["SIFT_score:sift.score", "DECIMAL(6,4)"] ]

separator = "\t"


def parseContigFromFilename(filename):
	contig = filename
	idx = filename.rindex(".")
	if (idx > -1):
		contig = filename[idx+1:]
	return contig.replace("chr", "")


dbname = sys.argv[1]

#Open db connection
con = mdb.connect('localhost', 'arup', 'arup', dbname)
cur = con.cursor()

for infile in sys.argv[2:]:
	if (infile.count("variant")==0):
		continue
	contig = parseContigFromFilename(infile)
	tablename = "varChr" + contig
	print "Processing " + infile 
	print " Contig: " + contig
	print " Table name: " + tablename
	#Open input file and read first non-blank line as the 'header'
	fh = open(infile, "r")
	line = fh.readline().strip()
	while(len(line)==0):
		line = fh.readline().strip()

	headerTokens = line.split(separator)
	
	columnIndices = []
	for columnInfo in columnsToImport:
		column = columnInfo[0]
		colType = columnInfo[1]
		if (column.count(":") > 0):
			idx = column.index(":")
			newColumnName = column[idx+1:]
			column = column[0: column.index(":")]
		try:
			columnIndices.append([column, headerTokens.index(column), colType, newColumnName] )
		except ValueError:
			print "Could not find column header " + column + " in file " + infile
			exit(1)
		print "Adding column " + newColumnName + " " + colType + " to table " + tablename
		cur.execute("alter table " + tablename + " add `" + newColumnName + "` " + colType)

	added = 0	
	line = fh.readline()
	while(line):
		line = line.strip()
		if (len(line)==0 or line[0]=="#"):
			line = fh.readline()
			continue
		
		toks = line.split(separator)
		if (not (len(toks) == len(headerTokens))):
			print "Incorrect number of tokens (" + str(len(toks)) + " on line: " + line
			line = fh.readline()
			continue

		pos = int(toks[1])
		ref = toks[2]
		alt = toks[3]

		for columnInfo in columnIndices:
			column = columnInfo[0]
			columnIndex = columnInfo[1]
			colType = columnInfo[2]
			newcolumnName = columnInfo[3];


			value = toks[columnIndex]
			if (value.count(";") > 0):
				value = value[0: value.index(";")]
			if (colType.count("DECIMAL") > 0):
				if (value == "." or value=="-"):
					value = 0.0
				else:
					try:
						value = float(value)
					except:
						print "Could not parse float for column " + column + " from line:" + line
						line = fh.readline()
						continue

			if (colType.count("INT") > 0):
				if (value=="." or value=="-"):
					value = 1
				else:
					value = int(toks[columnIndex])
			if (colType.count("VARCHAR") > 0):
				value = "\"" + value + "\""

			#If variant at position already exists, we 'update', otherwise, make a new row
			cur.execute("select pos from " + dbname + "." + tablename + " where pos=" + str(pos) + " and alt= \"" + alt + "\"")
			data = cur.fetchone()

			if (data == None):
				cur.execute("insert into " + dbname + "." + tablename + "(pos, ref, alt, `" + newcolumnName + "`) values(" + str(pos) + ", \"" + ref + "\", \"" + alt + "\", " + str(value) + ")")
				added = added + 1
			else:
				cur.execute("update " + tablename + " set `" + newcolumnName + "`=" + str(value) + " where pos=" + str(pos) + " and alt=\"" + alt + "\"")

		line = fh.readline()
		con.commit()
		#if (added > 1000):
		#	break
		

	con.commit()
	fh.close()
	print " Added " + str(added) + " new rows"

cur.close()
con.close()
