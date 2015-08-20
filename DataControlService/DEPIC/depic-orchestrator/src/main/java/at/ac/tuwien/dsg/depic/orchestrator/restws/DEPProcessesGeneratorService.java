/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
  E184.  This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.depic.orchestrator.restws;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DataElasticityManagementProcess;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRModel;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ElasticService;
import at.ac.tuwien.dsg.depic.common.entity.runtime.MonitoringSession;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;
import at.ac.tuwien.dsg.depic.common.utils.ToscaParser;
import at.ac.tuwien.dsg.depic.common.utils.YamlUtils;
import at.ac.tuwien.dsg.depic.process.generator.DataElasticityManagementProcessesGenerator;
import at.ac.tuwien.dsg.depic.orchestrator.execution.DataElasticityMonitor;
import at.ac.tuwien.dsg.depic.orchestrator.registry.ElasticServiceRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;



@Path("/dep")
public class DEPProcessesGeneratorService {
    
  
    @PUT
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String requestToGenerateDEPProcess(String toscaString) {

   
        ToscaParser toscaParser = new ToscaParser();
       
        QoRModel qor = toscaParser.parseQoRModel(toscaString);

        
        Configuration cfg = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
      
        
        DataElasticityManagementProcessesGenerator dempg = 
                new DataElasticityManagementProcessesGenerator(null, qor, 
                        cfg.getConfigPath(),
                        getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        
        DataElasticityManagementProcess demp = dempg.generateElasticProcesses();
       
      
        return demp.getsYBLSpecification().toString();

    }
    
    
    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String startDepicService() {
        
        MonitoringSession monitoringSession = new MonitoringSession();
        

        DataElasticityMonitor dataElasticityMonitor = new DataElasticityMonitor(monitoringSession);
        dataElasticityMonitor.startMonitoringService();

        return "";
    }


}
