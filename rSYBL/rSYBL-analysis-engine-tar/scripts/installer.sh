#!/bin/bash
 
DEC_MODULE_DIR=/opt/decision-module

mkdir -p $DEC_MODULE_DIR
cp ./*jar $DEC_MODULE_DIR
cp -R ./config $DEC_MODULE_DIR
cp ./config.properties $DEC_MODULE_DIR

cp ./decision-module /etc/init.d/
chmod +x /etc/init.d/decision-module
update-rc.d -f decision-module defaults


