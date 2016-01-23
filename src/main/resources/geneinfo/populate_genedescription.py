#!/usr/bin/python
'''
Created on Mar 29, 2013

@author: jose
'''

import re
import sys
from RNASeqTools import DrosophilaMelanogaster
from RNASeqTools import HomoSapiens
from RNASeqTools import EColi
from RNASeqTools import ParseGO


def parsedb():
   f = open('../META-INF/spring/database.properties', 'r') 
   for line in f:
       prop=re.split('=',line)
       key=prop[0].strip()
       if key=='database.username':
           user=prop[1].strip()
       elif key=='database.password':
           passwd=prop[1].strip()
       elif key=='database.url':
           database=re.split('/',prop[1])[-1].strip()
   return (user,passwd,database)

def lockTables():
    print "SET autocommit=0;"
    print "SET unique_checks=0;"
    print "SET foreign_key_checks=0;"
    print "LOCK TABLES gene_description WRITE, go_term WRITE;"

def unlockTables():
    print "UNLOCK TABLES;"

def createdb():
    print '/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;'
    print '/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;'
    print '/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;'
    print '/*!40101 SET NAMES utf8 */;'
    print '/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;'
    print "/*!40103 SET TIME_ZONE='+00:00' */;"
    print '/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;'
    print '/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;'
    print "/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;"
    print "/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;"
    print ""
    print "--"
    print "-- Table structure for table `gene_description`"
    print "--"
    print ""
    print "DROP TABLE IF EXISTS `gene_description`;"
    print "/*!40101 SET @saved_cs_client     = @@character_set_client */;"
    print "/*!40101 SET character_set_client = utf8 */;"
    print "CREATE TABLE `gene_description` ("
    print "  `id` bigint(20) NOT NULL AUTO_INCREMENT,"
    print "  `ec` varchar(255) DEFAULT NULL,"
    print "  `mginame` varchar(255) DEFAULT NULL,"
    print "  `omim` varchar(255) DEFAULT NULL,"
    print "/*  `qtl` varchar(255) DEFAULT NULL,*/"
    print "  `rgdname` varchar(255) DEFAULT NULL,"
    print "  `sgdaccesion_number` varchar(255) DEFAULT NULL,"
    print "  `alignments` varchar(1024) DEFAULT NULL,"
    print "  `annotation_date` varchar(255) DEFAULT NULL,"
    print "  `archival_unigene_cluster` varchar(255) DEFAULT NULL,"
    print "  `chromosomal_location` varchar(255) DEFAULT NULL,"
    print "  `description` longblob,"
    print "  `ensembl` varchar(1024) DEFAULT NULL,"
    print "  `entrez_gene` varchar(255) DEFAULT NULL,"
    print "  `flybase` varchar(255) DEFAULT NULL,"
    print "/*  `gene_chip_array` varchar(255) DEFAULT NULL,*/"
    print "  `gene_symbol` varchar(700) DEFAULT NULL,"
    print "  `gene_title` varchar(4096) DEFAULT NULL,"
    print "  `genome_version` varchar(255) DEFAULT NULL,"
    print "/*  `inter_pro` varchar(255) DEFAULT NULL,*/"
    print "  `micro_array_type` int(11) DEFAULT NULL,"
    print "  `pathway` longblob,"
    print "  `probe_setid` varchar(255) DEFAULT NULL,"
    print "  `representative_publicid` varchar(255) DEFAULT NULL,"
    print "  `sequence_source` varchar(255) DEFAULT NULL,"
    print "  `sequence_type` varchar(255) DEFAULT NULL,"
    print "/*  `species_scientific_name` varchar(255) DEFAULT NULL,*/"
    print "  `str_agi_name` varchar(255) DEFAULT NULL,"
    print "  `str_gobp` longblob,"
    print "  `str_gocc` longblob,"
    print "  `str_gomf` longblob,"
    print "  `swiss_prot` varchar(255) DEFAULT NULL,"
    print "/*  `trans_membrane` varchar(255) DEFAULT NULL,*/"
    print "  `transcriptid` varchar(255) DEFAULT NULL,"
    print "  `uni_gene_cluster_type` varchar(255) DEFAULT NULL,"
    print "  `uni_geneid` varchar(255) DEFAULT NULL,"
    print "  `version` int(11) DEFAULT NULL,"
    print "/*  `worm_base` varchar(255) DEFAULT NULL,*/"
    print "  UNIQUE idx_probename (micro_array_type,probe_setid),"
    #print "  UNIQUE idx_genename(micro_array_type,gene_symbol),"
    #print "  UNIQUE idx_stragi(micro_array_type,str_agi_name),"
    print "  PRIMARY KEY (`id`)"
    print ") ENGINE=InnoDB AUTO_INCREMENT=99772 DEFAULT CHARSET=latin1;"
    print "/*!40101 SET character_set_client = @saved_cs_client */;"
    print "--"
    print "-- Dumping data for table `gene_description`"
    print "--"
    print "/*!40000 ALTER TABLE `gene_description` DISABLE KEYS */;"
    print ""

def enddb():
    print "alter table gene_description add index (micro_array_type, probe_setid);"
    print "alter table gene_description add index (micro_array_type, gene_symbol);"
    print "alter table gene_description add index (micro_array_type, str_agi_name);"
    print "    /*!40000 ALTER TABLE `gene_description` ENABLE KEYS */;"
    print "COMMIT;"
    print "SET unique_checks=1;"
    print "SET foreign_key_checks=1;"
    print "--"
    print "/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;"
    print "/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;"
    print "/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;"
    print "/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;"
    print "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;"
    print "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;"
    print "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;"
    print "/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;"

def createGoTermdb():
    print "DROP TABLE IF EXISTS `go_term`;"
    print "CREATE TABLE `go_term` ("
    print "   `id` bigint(20) NOT NULL AUTO_INCREMENT,"
    print "   `goid` varchar(255) NOT NULL,"
    print "   `description` varchar(255) NOT NULL,"
    print "   `micro_array_type` int(11) DEFAULT NULL,"
    print "   `num_probesets` int(11) NOT NULL,"
    print "   `num_genes` int(11) NOT NULL,"
    print "   `str_probesets` longtext,"
    print "   `str_gene_symbols` longtext,"
    print "   `ontology` varchar(2),"
    print "   `version` int(11) DEFAULT NULL,"
    print "PRIMARY KEY (`id`)"
    print ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;"
    print "/*!40000 ALTER TABLE `go_term` DISABLE KEYS */;"   
    print "SET autocommit=0;"
    print "SET unique_checks=0;"
    print "SET foreign_key_checks=0;"

def endGoTermdb():
    print "alter table go_term add index (micro_array_type, goid);"
    print "alter table go_term add index (micro_array_type, goid, ontology);"
    print "/*!40000 ALTER TABLE `go_term` ENABLE KEYS */;"
    print "COMMIT;"
    
    

def parsefile(fich,microarrayType):
   f = open(fich, 'r') 
   skipline=True
   for line in f:
       if not line.startswith('#'):
           if skipline:
               skipline=False
               continue
           
           linec=re.sub("\'","\\\'",line)

           M=re.split('\",\"',linec)
           microArrayType=microarrayType
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
           print "INSERT INTO `gene_description` (ec,mginame,omim,rgdname,sgdaccesion_number,alignments,annotation_date,archival_unigene_cluster,chromosomal_location,description,ensembl,entrez_gene,flybase,gene_symbol,gene_title,genome_version,micro_array_type,pathway,probe_setid,representative_publicid,sequence_source,sequence_type,str_agi_name,str_gobp,str_gocc,str_gomf,transcriptid,uni_gene_cluster_type,uni_geneid,version) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%d);"%(EC,MGIName,OMIM,RGDName,SGDAccesionNumber,alignments,annotationDate,archivalUnigeneCluster,chromosomalLocation,description,ensembl,entrezGene,flybase,geneSymbol,geneTitle,genomeVersion,microArrayType,pathway,probesetId,representativePublicID,sequenceSource,sequenceType,strAgiName,strGoBP,strGoCC,strGoMF,transcriptID,uniGeneClusterType,uniGeneID,0)


def parseGOFile(fich,microarrayType):
    goterm={};
    def parseGOTerm(strGO, probesetId, gene_array, ontology):
        if strGO=="---" or strGO=="":
            return;
        goTerms=strGO.split("///")
        for go in goTerms:            
            goelem=go.split("//")
            goid=goelem[0].strip()
            if goid not in goterm:
                goterm[goid]={'prob':set([probesetId]),'gen':set(gene_array),'desc':goelem[1].strip(),'ont':ontology}
            else:
                goterm[goid]['prob'].add(probesetId)
                goterm[goid]['gen'].update(gene_array)
                               
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
            geneSymbol=M[14]
            strAgiName=M[25]
            strGoBP=M[30]
            strGoCC=M[31]
            strGoMF=M[32]

            if (microarrayType==0):                                   
                geneNames=[x.strip() for x in strAgiName.split('///')]
            else:
                geneNames=[x.strip() for x in geneSymbol.split('///')]

            parseGOTerm(strGoBP,probesetId, geneNames,'BP')
            parseGOTerm(strGoCC,probesetId, geneNames,'CC')
            parseGOTerm(strGoMF,probesetId, geneNames,'MF')

    for goid in goterm:
        got=goterm[goid]        
        strProbesets=",".join(got['prob'])
        numProbesets=len(got['prob'])
        if ('---' in got['gen']):
            got['gen'].remove('---')

        strGenes=",".join(got['gen'])
        numGenes=len(got['gen'])
        ontology=got['ont']
        print "INSERT INTO `go_term` (goid,description,micro_array_type,num_probesets,num_genes,str_probesets,str_gene_symbols,ontology,version) VALUES ('%s','%s',%d,%d,%d,'%s','%s','%s',%d);"%(goid,got['desc'],microarrayType,numProbesets,numGenes,strProbesets,strGenes,ontology,0)
    
def insert_rna_genes(genes):
    for gen in genes:
        print genes[gen].toSQL()

def insert_rna_go_term(go_count,godb,genes):
    micro_array_type=genes.values()[1].datatype
    for go_id in go_count:
        strGenes=",".join(go_count[go_id])
        numGenes=len(go_count[go_id])
        ontology=godb[go_id].getShortDomain()
        desc=godb[go_id].description.replace('\'', '')
        print "INSERT INTO `go_term` (goid,description,micro_array_type,num_genes,str_gene_symbols,ontology,version) VALUES ('%s','%s',%d,%d,'%s','%s',%d);"%(go_id[3:],desc,micro_array_type,numGenes,strGenes,ontology,0)
    
        
    
#Defined in DataTypeEnum
#ATH1121501(0),HGU133PLUS2(1),HGU133A(2),PRIMEVIEW(3),TEST(99),GENERIC(-1),
#SEQUNKNOW(128),SEQECOLI(129),SEQSCEREVISAE(130),SEQDMELANOGASTER(131);

if __name__ == '__main__':
    try:
        (user,passwd,database)=parsedb()
    except:
        (user,passwd,database)=('user','password','database')

    if len(sys.argv)<2:
        print "Usage:\n    populate_genedescription.py  . >  db_dump.sql "
        print "    mysql -u%s -p%s %s < db_dump.sql "%(user,passwd,database)
    else:
        createdb()
        createGoTermdb()
        lockTables()
        #Microarray part
        parsefile('ATH1-121501.na32.annot.csv',0)
        parsefile('HG-U133_Plus_2.na33.annot.csv',1)
        parsefile('HG-U133A.na33.annot.csv',2)
        parsefile('PrimeView.na32.annot.csv',3)
        parsefile('TEST.annot.csv',99)
        
        parseGOFile('ATH1-121501.na32.annot.csv',0)
        parseGOFile('HG-U133_Plus_2.na33.annot.csv',1)
        parseGOFile('HG-U133A.na33.annot.csv',2)
        parseGOFile('PrimeView.na32.annot.csv',3)
        parseGOFile('TEST.annot.csv',99)
        
        #RNASeq part
        parseGO=ParseGO('go-basic.obo')
        godb=parseGO.parse()        
        dmel=DrosophilaMelanogaster('flybase.fb',godb)
        genes=dmel.parse()
        insert_rna_genes(genes)
        insert_rna_go_term(dmel.calcGO_count(),godb,genes)
        
        hsap=HomoSapiens('gene_association.goa_human',godb)
        genes=hsap.parse()
        insert_rna_genes(genes)
        insert_rna_go_term(hsap.calcGO_count(),godb,genes)
        
        ecoli=EColi('gene_association.goa_human',godb)
        genes=ecoli.parse()
        insert_rna_genes(genes)
        insert_rna_go_term(ecoli.calcGO_count(),godb,genes)
        
        endGoTermdb()
        enddb()
        lockTables()
