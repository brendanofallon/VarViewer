#!/usr/bin/python

# Convert an input vcf file into VarViewer format (annotated, tabix compressed csv)

import sys
import os

#Path to annotation template
template = "annotation_template.xml"
pipeline = "/home/brendan/workspace/Pipeline/pipeline.jar "
tabixDir = "/home/brendan/tabix-0.2.6/"

def createInput(vcfFile, nameOfAnnotatedOutput):
	fh = open(template, "r")
	ofh = open("tmpAnnotationInput.xml", "w")
	line = fh.readline()
	while(line):
		line = line.replace("$$INPUTFILE", vcfFile)
		line = line.replace("$$OUTPUTFILE", nameOfAnnotatedOutput)
		ofh.write(line)
		line = fh.readline()
	ofh.close()
	


for filename in sys.argv[1:]:
	outputName = filename.replace(".vcf", "") + ".annotated.csv"
	createInput(filename, outputName)
	os.system("java -Xmx4g -jar " + pipeline + " tmpAnnotationInput.xml")
	os.system(tabixDir + "bgzip " + outputName)
	os.system(tabixDir + "tabix -p vcf " + outputName + ".gz")
