#!/bin/bash

MELA_ANALYSIS_DIR=/opt/mela-analysis-service

mkdir -p $MELA_ANALYSIS_DIR
cp ./*jar $MELA_ANALYSIS_DIR
cp -R ./config $MELA_ANALYSIS_DIR

cp ./mela-analysis-service /etc/init.d/
chmod +x /etc/init.d/mela-analysis-service
update-rc.d -f mela-analysis-service defaults
