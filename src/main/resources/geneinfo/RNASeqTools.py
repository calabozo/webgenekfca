# -*- coding: utf-8 -*-
"""

"""

from sets import Set
MF = 'molecular_function'
BP = 'biological_process'
CC = 'cellular_component'


class GO:
    def __init__(self,go_id,description,domain):
        self.go_id=go_id
        self.description=description
        self.domain=domain
    def __str__(self):
        return self.go_id+" "+self.domain+" "+self.description 
    def __repr__(self):
        return self.__str__()
    def __eq__(self,other):
        return (self.go_id==other.go_id)
    def __hash__(self):
        return self.go_id.__hash__()
    def getShortDomain(self):
        if (self.domain==BP):
            return 'BP'
        elif (self.domain==MF):
            return 'MF'
        elif (self.domain==CC):
            return 'CC'

class Gene(object):
    def __init__(self,gene_id,symbol,name,gos):
        self.gene_id=gene_id #DB Object ID:  P12345
        self.symbol=symbol   #DB Object Symbol:  PHO3
        self.name=name       #DB Object Name: Toll-like receptor 4
        if gos is not None:
            self.go_bp=Set([x for x in gos if x.domain == BP])
            self.go_mf=Set([x for x in gos if x.domain == MF])
            self.go_cc=Set([x for x in gos if x.domain == CC])
        else:            
            self.go_bp=Set()
            self.go_mf=Set()
            self.go_cc=Set()

    def addGO(self,go):
        if (go.domain==BP):
            self.go_bp.add(go)
        elif (go.domain==MF):
            self.go_mf.add(go)
        elif (go.domain==CC):
            self.go_cc.add(go)

    def getStrGo(self,domain):
        goresult=None
        if (domain==BP):
            goresult=self.go_bp
        elif (domain==MF):
            goresult=self.go_mf
        elif (domain==CC):
            goresult=self.go_cc
        strGOs=[]
        for go_term in goresult:
            strGOs.append(go_term.go_id[3:]+" // "+go_term.description.replace('\'', '')+" // --- ")
        return " /// ".join(strGOs)
        
        
    def __str__(self):
        return self.gene_id+" "+self.symbol+" "+self.name+" "+str(self.go_bp)+","+str(self.go_mf)+","+str(self.go_cc) 
    def __repr__(self):
        return self.__str__()
    def toSQL(self):
        return "INSERT into gene_description %s VALUES (%d,\'%s\',\'%s\',\'%s\',\'%s\',\'%s\',\'%s\');"%(self.values,self.datatype,self.gene_id,self.symbol.replace('\'', ''),self.name.replace('\'', ''),self.getStrGo(BP),self.getStrGo(CC),self.getStrGo(MF))

class DMelanogasterGene(Gene):
    datatype=131
    values="(micro_array_type,flybase,gene_symbol,gene_title,str_gobp,str_gocc,str_gomf)"
    
class UniProtGene(Gene):
    datatype=132
    values="(micro_array_type,swiss_prot,gene_symbol,gene_title,str_gobp,str_gocc,str_gomf)"

#This class parses the OBO file obtained from
#http://purl.obolibrary.org/obo/go/go-basic.obo
#Accesible from: http://geneontology.org/page/download-ontology
#The format is explained in:
#http://oboformat.googlecode.com/svn/trunk/doc/GO.format.obo-1_2.html
class ParseGO:
    TERM="[Term]"
    ID="id:"
    NAME="name:"
    DOMAIN="namespace:"
    ALT_ID="alt_id:"
    def __init__(self,obofile):
        self.file=obofile
        self.godb={}
                       
    def parse(self):
        self.f = open(self.file)
        line = self.f.readline()
        while line:
            if line.startswith(self.TERM):
                gos=self.parseTerm()
                for go in gos:
                    self.godb[go.go_id]=go
            line = self.f.readline()
        self.f.close()
        return self.godb
    
    def parseTerm(self):
        go_id=None
        go_name=None
        go_domain=None
        go_alt_id=[]
        inTerm=True
        while inTerm:
            line = self.f.readline()
            if (line.startswith(self.ID)):
                go_id=line[len(self.ID):].strip()
                continue
            if (line.startswith(self.NAME)):
                go_name=line[len(self.NAME):].strip()
                continue
            if (line.startswith(self.DOMAIN)):
                go_domain=line[len(self.DOMAIN):].strip()
                continue
            if (line.startswith(self.ALT_ID)):
                go_alt_id.append(line[len(self.ALT_ID):].strip())
                continue
            inTerm=False
        retGO=[GO(go_id,go_name,go_domain)]
        for go_id in go_alt_id:
            retGO.append(GO(go_id,go_name,go_domain))
        return retGO


#The file format is explained in:
# http://geneontology.org/page/go-annotation-file-format-20  
class GOAnnotationParser(object):
    def __init__(self,annotationfile,godb,datatype):
        self.file=annotationfile
        self.godb=godb
        self.datatype=datatype;
        self.genedb={}
        
    def parse(self):
        self.f = open(self.file)
        line='.'
        while line:
            line = self.f.readline()
            if line.startswith('!gaf-version: 2.0'):
                break
        
        if not line.startswith('!gaf-version: 2.0'):
            raise ValueError('Wrong gaf-version, expected 2.0, received:',line)
            
        while line:
            if line.startswith('!'):
                line = self.f.readline()
                continue
            fields=line.split('\t')
            gene_id=fields[1].strip()
            go_id=fields[4].strip()
            go_term=self.godb[go_id]
            if gene_id in self.genedb:           
                self.genedb[gene_id].addGO(go_term)
            else:
                symbol=fields[2].strip()
                name=fields[9].strip()
                self.genedb[gene_id]=self.createGene(gene_id,symbol,name,[go_term])
            line = self.f.readline()
        self.f.close()
        return self.genedb
    
    def calcGO_count(self):
        go_count={}
        for gene in self.genedb:
            for go in (self.genedb[gene].go_bp|self.genedb[gene].go_mf|self.genedb[gene].go_cc):
                go_id=go.go_id
                if go_id not in go_count:
                    go_count[go_id]=Set()
                go_count[go_id].add(self.genedb[gene].symbol.replace('\'', ''))
        return go_count
        
             
#Parses gene annotation file donloaded from:
#http://flybase.org/static_pages/downloads/FB2015_04/go/gene_association.fb.gz
#Accesible from
#http://flybase.org/static_pages/downloads/bulkdata7.html 
# Genes->Genes GO data->gene_association.fb.gz           
class DrosophilaMelanogaster(GOAnnotationParser):
    def __init__(self,flybasefile,godb):
        datatype=131; #Defined in DataTypeEnum
        super(self.__class__, self).__init__(flybasefile,godb,datatype)
    def createGene(self,gene_id,symbol,name,go_term):
        return DMelanogasterGene(gene_id,symbol,name,go_term)
    

#Parses gene association file donloaded from:
#http://geneontology.org/gene-associations/gene_association.goa_human.gz
#Accesible from
#http://geneontology.org/page/download-annotations        
class HomoSapiens(GOAnnotationParser):
    def __init__(self,gocfile,godb):
        datatype=132; #Defined in DataTypeEnum
        super(self.__class__, self).__init__(gocfile,godb,datatype)
    def createGene(self,gene_id,symbol,name,go_term):
        return UniProtGene(gene_id,symbol,name,go_term)

#Parses gene association file donloaded from:
#http://geneontology.org/gene-associations/gene_association.ecocyc.gz
#Accesible from
#http://geneontology.org/page/download-annotations        
class EColi(GOAnnotationParser):
    def __init__(self,gocfile,godb):
        datatype=129; #Defined in DataTypeEnum
        super(self.__class__, self).__init__(gocfile,godb,datatype)
    def createGene(self,gene_id,symbol,name,go_term):
        return UniProtGene(gene_id,symbol,name,go_term)
        
        

        
if __name__ == "__main__":
    parseGO=ParseGO('go-basic.obo')
    godb=parseGO.parse()
    print godb['GO:0000015']
    print godb['GO:2001317']
    d=DrosophilaMelanogaster('flybase.fb',godb)
    drosophilagenes=d.parse()
    print drosophilagenes['FBgn0043467']
    print drosophilagenes['FBgn0043467'].toSQL()
    print d.calcGO_count()
        
    
    