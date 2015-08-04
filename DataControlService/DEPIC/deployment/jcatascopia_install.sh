#!/bin/bash



wget https://raw.githubusercontent.com/CELAR/celar-deployment/master/orchestrator/jcatascopia-server.sh

wget https://raw.githubusercontent.com/CELAR/celar-deployment/master/vm/jcatascopia-agent.sh

wget http://128.130.172.215/salsa/upload/files/jun/depic_setup/PostgresProbe.zip

sudo yum install -y unzip
unzip PostgresProbe.zip


wget http://128.130.172.215/salsa/upload/files/jun/depic_setup/celar.repo
sudo cp celar.repo /etc/yum.repos.d/

sudo yum --disablerepo=* --enablerepo=CELAR-snapshots list available

sudo bash jcatascopia-server.sh
sudo bash jcatascopia-agent.sh

wget http://128.130.172.215/salsa/upload/files/jun/depic_setup/agent.properties
sudo cp agent.properties /usr/local/bin/JCatascopiaAgentDir/resources/

sudo /usr/local/bin/JCatascopiaAgentDir/JCatascopia-Agent-stop.sh