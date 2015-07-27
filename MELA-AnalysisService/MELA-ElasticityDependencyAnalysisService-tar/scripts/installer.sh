#!/bin/bash

MELA_DEP_ANALYSIS_DIR=/opt/mela-dependency-analysis-service

mkdir -p $MELA_DEP_ANALYSIS_DIR
cp ./*jar $MELA_DEP_ANALYSIS_DIR
cp -R ./config $MELA_DEP_ANALYSIS_DIR

cp ./mela-dependency-analysis-service /etc/init.d/
chmod +x /etc/init.d/mela-dependency-analysis-service
update-rc.d -f mela-dependency-analysis-service defaults
