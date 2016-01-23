# mysql < createdb.sql

create database geneanalyzer;
create user 'gene'@'localhost' identified by 'genep';
grant all on geneanalyzer.* to gene;
flush privileges;

