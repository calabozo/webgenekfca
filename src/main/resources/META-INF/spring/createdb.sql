# mysql < createdb.sql

create database webgenekfca;
create user 'gene'@'localhost' identified by 'genep';
grant all on webgenekfca.* to gene;
flush privileges;

