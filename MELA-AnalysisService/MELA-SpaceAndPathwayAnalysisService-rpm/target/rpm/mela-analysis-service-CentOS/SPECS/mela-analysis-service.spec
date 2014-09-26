Name: mela-analysis-service
Version: 2.0
Release: 1
Summary: mela-analysis-service
License: Apache License, Version 2.0
Vendor: TUWien
URL: http://infosys.tuwien.ac.at
Group: Applications/Engineering
Packager: TUWien
autoprov: yes
autoreq: yes
BuildRoot: /home/daniel-tuwien/Documents/CELAR_GIT/decision-module/MELA-AnalysisService/MELA-SpaceAndPathwayAnalysisService-rpm/target/rpm/mela-analysis-service-CentOS/buildroot

%description
Multilevel Metrics Evaluation module

%files

%attr(664,root,root) /opt/mela-analysis-service/config/Config.properties
%attr(664,root,root) /opt/mela-analysis-service/config/Log4j.properties
%attr(664,root,root) /opt/mela-analysis-service/config/mela-analysis-service.properties
%attr(664,root,root) /opt/mela-analysis-service/config/default/compositionRules.xml
%attr(664,root,root) /opt/mela-analysis-service/config/default/structure.xml
%attr(664,root,root) /opt/mela-analysis-service/config/default/requirements.xml
%attr(664,root,root) /opt/mela-analysis-service/mela-analysis-service
%attr(664,root,root) /opt/mela-analysis-service/MELA-SpaceAndPathwayAnalysisService-2.0-SNAPSHOT-war-exec.jar
%attr(774,root,root) /etc/init.d/mela-analysis-service

%post
chkconfig mela-analysis-service on
                            service mela-analysis-service start
