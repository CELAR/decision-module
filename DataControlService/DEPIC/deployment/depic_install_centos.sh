#!/bin/bash

TOMCAT_VERSION=7.0.55
TOMCAT_DIR=/usr/share

val "sed -i 's/127.0.0.1.*localhost.*/127.0.0.1 localhost $HOSTNAME/g' /etc/hosts"


sudo yum install -y tomcat-webapps tomcat-admin-webapps

sudo yum install -y git

sudo git clone https://github.com/CELAR/decision-module


sudo yum install -y maven

cd decision-module/DataControlService/DEPIC/
sudo mvn clean install
sudo cp depic-orchestrator/target/depic-orchestrator.war /usr/share/tomcat/webapps/
cd 

sudo bash /usr/share/tomcat/bin/startup.sh

wget http://128.130.172.215/salsa/upload/files/jun/artifact/LSR.war

sudo cp LSR.war /usr/share/tomcat/webapps/

sudo rpm -Uvh http://dev.mysql.com/get/mysql-community-release-el7-5.noarch.rpm

sudo yum install -y mysql-server

cd /home/centos

wget http://128.130.172.215/salsa/upload/files/jun/depic_setup/ElasticityProcess.sql

wget http://128.130.172.215/salsa/upload/files/jun/depic_setup/PrimitiveActionMetadata.sql



sudo /usr/bin/systemctl enable mysqld

sudo /usr/bin/systemctl start mysqld



MYSQL=`which mysql`

$MYSQL -uroot -e "use mysql; update user set password=PASSWORD('123') where User='root'; flush privileges;"

Q1="CREATE DATABASE IF NOT EXISTS ElasticityProcess;"
Q2="GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123' WITH GRANT OPTION;"
Q3="FLUSH PRIVILEGES;"
SQL="${Q1}${Q2}${Q3}"

$MYSQL -uroot -p123 -e "$SQL"


sudo mysql -u root -p123 ElasticityProcess < ElasticityProcess.sql


Q4="CREATE DATABASE IF NOT EXISTS PrimitiveActionMetadata;"
Q5="GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123' WITH GRANT OPTION;"
Q6="FLUSH PRIVILEGES;"
SQL2="${Q4}${Q5}${Q6}"

$MYSQL -uroot -p123 -e "$SQL2"
sudo mysql -u root -p123 PrimitiveActionMetadata < PrimitiveActionMetadata.sql

sudo service mysql restart