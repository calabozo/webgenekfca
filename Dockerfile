FROM openjdk:7
MAINTAINER melopsitaco@gmail.com

#Install packages and mysql
RUN apt-get -y update && DEBIAN_FRONTEND=noninteractive apt-get install -yq mysql-server maven2 tomcat7

#Builds webgenekfca
RUN git clone https://github.com/calabozo/webgenekfca.git /opt/webgenekfca
RUN cd /opt/webgenekfca && mvn package -Dmaven.test.skip=true
RUN mv /opt/webgenekfca/target/WebGeneKFCA-1.0.war /var/lib/tomcat7/webapps/webgenekfca.war

#Configure database (You should use another container...)
RUN /etc/init.d/mysql start; mysql < /opt/webgenekfca/src/main/resources/META-INF/spring/createdb.sql
RUN gunzip /opt/webgenekfca/src/main/resources/geneinfo/*.gz
RUN cd /opt/webgenekfca/src/main/resources/geneinfo/;python populate_genedescription.py . > /tmp/db_dump.sql
RUN /etc/init.d/mysql start; mysql -ugene -pgenep webgenekfca < /tmp/db_dump.sql
RUN rm /tmp/db_dump.sql
RUN sed -i "s/max_allowed_packet.*=.*/max_allowed_packet = 100M/" /etc/mysql/my.cnf
RUN killall java; exit 0


#Downloads Affymetrix tools from the web
RUN wget -P /opt/affymetrix http://media.affymetrix.com/Download/updates/APT_1.19.0_Linux_64_bit_x86_binaries.zip; unzip /opt/affymetrix/APT_1.19.0_Linux_64_bit_x86_binaries.zip; mv apt-1.19.0-x86_64-intel-linux/* /opt/affymetrix; rm /opt/affymetrix/APT_1.19.0_Linux_64_bit_x86_binaries.zip
RUN chmod +x /opt/affymetrix/bin/*

#Gives more memory to Tomcat
RUN sed -i "s/-Xmx128m/-Xmx2048m/" /etc/default/tomcat7

#Start daemons
EXPOSE 8080
CMD /etc/init.d/mysql start; /etc/init.d/tomcat7 start; sleep infinity; 

