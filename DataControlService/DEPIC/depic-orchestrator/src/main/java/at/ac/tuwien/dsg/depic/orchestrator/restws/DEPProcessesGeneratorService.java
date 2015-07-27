/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.orchestrator.restws;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DataElasticityManagementProcess;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRModel;
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


/**
 *
 * @author Jun
 */
@Path("/dep")
public class DEPProcessesGeneratorService {
    
  
    @PUT
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String requestToGenerateDEPProcess(String toscaString) {
// reveice tosca - extract tosca to get daf and qor
   
        ToscaParser toscaParser = new ToscaParser();
       
        QoRModel qor = toscaParser.parseQoRModel(toscaString);
       // YamlUtils.setFilePath("/Volumes/DATA/Temp");
        //YamlUtils.toYaml(qor, "qor.yaml");
        
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
