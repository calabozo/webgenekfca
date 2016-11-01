#Webgenekfca

This is a Beta web based tool which helps the researcher to group similar genes based on their expression profiles for different experiments.
The genes are grouped based on the concepts obtained from a K-Formal Concept Analysis.

#Installation

To install you only need docker
```
docker run --name webgenekfca -d -p 8080:8080 calabozo/webgenekfca
```
The web will be available at: http://localhost:8080/webgenekfca

To upload data you must login. The default web user/password is admin/admin1.

The file format can be:
* Several Affymetrix CEL files.  
* A gene expression file in the format given by the Affymetrix apt-probeset-summarize.
Several count read files obttained with htseq-count or a equivalent program.
* A gene expression file in the format given by the Affymetrix apt-probeset-summarize.
Several count read files obttained with htseq-count or a equivalent program.

You can start by downloading the CEL files from:
https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=GSE9311

For development read the document README.txt

#Basic monitoring

To stop the container execute:
```
docker stop webgenekfca
```

To start again the container execute:
```
docker start webgenekfca
```

To see the logs:
```
docker exec -t webgenekfca tail -F /var/log/tomcat7/webgenekfca.log
```

#Advanced uses

If you want to deploy a new .war you just have to mount the remote dir */var/lib/tomcat7/webapps* into the folder where you would leave the .war file:
```
docker run --name webgenekfca -v <host_directory>:/var/lib/tomcat7/webapps -d -p 8080:8080 calabozo/webgenekfca
```
If you want to enter into the shell of the container execute:
```
docker exec -t -i webgenekfca /bin/bash
```
