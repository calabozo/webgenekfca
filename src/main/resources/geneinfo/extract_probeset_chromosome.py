#!/usr/bin/python
'''
Created on Jul 30, 2013

@author: jose
'''

import re
import sys
import os


def parsedescription(fich,chrid):
   probesets=[]
   f = open(fich, 'r') 
   skipline=True
   for line in f:
       if not line.startswith('#'):
           if skipline:
               skipline=False
               continue
           
           linec=re.sub("\'","\\\'",line)

           M=re.split('\",\"',linec)
           probesetId=M[0][1:]
           annotationDate=M[3];
           sequenceType=M[4];
           sequenceSource=M[5];
           transcriptID=M[6];
           description=M[7]; 
           representativePublicID=M[8];
           archivalUnigeneCluster=M[9];
           uniGeneID=M[10];
           genomeVersion=M[11];
           alignments=M[12];
           geneTitle=M[13]; 
           geneSymbol=M[14];
           chromosomalLocation=M[15];
           uniGeneClusterType=M[16];
           ensembl=M[17];
           entrezGene=M[18];
           EC=M[20];
           OMIM=M[21];
           flybase=M[24];
           strAgiName=M[25]
           wormBase=M[26];
           MGIName=M[27];
           RGDName=M[28];
           SGDAccesionNumber=M[29];
           strGoBP=M[30];
           strGoCC=M[31];
           strGoMF=M[32];
           pathway=M[33];
           
           if chromosomalLocation.startswith(chrid):
               probesets.append(probesetId)

   return probesets 

def parseexpressions(fich,probesets):
   f = open(fich, 'r') 
   firstline=True
   for line in f:
       if firstline:
           print line[:-1]
           firstline=False


       M=re.split(';',line)
       probesetId=M[0]
       if probesetId in probesets:
           print line[:-1]
       

if __name__ == '__main__':
    if len(sys.argv)<2:
        print "Usage:\n  extract_probeset_chromosome.py descriptionfile expressionfile chromosomeid\n"
    else:
        descfile=sys.argv[1]
        expressionfile=sys.argv[2]
        chromosomeid="chr"+sys.argv[3]
        probesets=parsedescription(descfile, chromosomeid)
        #print probesets
        parseexpressions(expressionfile,probesets)
