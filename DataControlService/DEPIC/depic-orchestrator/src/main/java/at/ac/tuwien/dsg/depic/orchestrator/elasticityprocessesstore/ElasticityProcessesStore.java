/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.orchestrator.elasticityprocessesstore;

import at.ac.tuwien.dsg.depic.common.entity.runtime.ElasticService;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.eda.ElasticDataAsset;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticStateSet;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DataElasticityManagementProcess;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.PrimitiveActionMetadata;

import at.ac.tuwien.dsg.depic.common.entity.qor.QoRModel;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;
import at.ac.tuwien.dsg.depic.common.utils.MySqlConnectionManager;
import at.ac.tuwien.dsg.depic.common.utils.YamlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Jun
 */
public class ElasticityProcessesStore {
    
    MySqlConnectionManager connectionManager;
    
    public ElasticityProcessesStore() {
        Configuration config = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        String ip = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.IP");
        String port = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.PORT");
        String database = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.DATABASE");
        String username = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.USERNAME");
        String password = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.PASSWORD");
        

        connectionManager = new MySqlConnectionManager(ip, port, database, username, password);

    }
    
    
    
    public String getDeployementDescription(String edaas) {

        String deploymentDescription = "";
        try {
            InputStream inputStream = null;

            String sql = "Select * from DeploymentDescription where edaas='" + edaas + "'";

            ResultSet rs = connectionManager.ExecuteQuery(sql);

            try {
                while (rs.next()) {
                    inputStream = rs.getBinaryStream("specs");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
            }

            StringWriter writer = new StringWriter();
            String encoding = StandardCharsets.UTF_8.name();

            IOUtils.copy(inputStream, writer, encoding);
            deploymentDescription = writer.toString();
          

        } catch (IOException ex) {
            Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        return deploymentDescription;
    }
    
    
    public ElasticDataAsset getElasticDataAsset(String edaasName){
        
                 
            String elasticityProcessesXML="";
            String elasticStateSetXML="";

        try {

            InputStream elProcessStream = null;
            InputStream eStateSetStream = null;
            InputStream typeStream = null;
                    
            String sql = "SELECT * FROM ElasticDaaS "
                    + "WHERE ElasticDaaS.name='"+edaasName+"' ";
            
            ResultSet rs = connectionManager.ExecuteQuery(sql);
            
            
            
            
            try {
                while (rs.next()) {
                    elProcessStream = rs.getBinaryStream("elasticity_processes");
                    eStateSetStream = rs.getBinaryStream("elasticStateSet");
                    typeStream = rs.getBinaryStream("type");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            StringWriter writer1 = new StringWriter();
            String encoding = StandardCharsets.UTF_8.name();
            IOUtils.copy(elProcessStream, writer1, encoding);
            elasticityProcessesXML = writer1.toString();
            
            StringWriter writer2 = new StringWriter();
            IOUtils.copy(eStateSetStream, writer2, encoding);
            elasticStateSetXML = writer2.toString();
            
            StringWriter writer3 = new StringWriter();
            IOUtils.copy(typeStream, writer3, encoding);
            String typeStr = writer3.toString();
           
            
            
            
            
        } catch (IOException ex) {
                Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
        Configuration cfg = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        YamlUtils.setFilePath(cfg.getConfigPath());
        
        DataElasticityManagementProcess elasticityProcess = 
                YamlUtils.unmarshallYaml(DataElasticityManagementProcess.class, elasticityProcessesXML);
        
        
        
        ElasticStateSet elasticStateSet = null;
        
        try {
            elasticStateSet = JAXBUtils.unmarshal(elasticStateSetXML, ElasticStateSet.class);
        } catch (JAXBException ex) {
            Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ElasticDataAsset eda = new ElasticDataAsset(edaasName, elasticityProcess, elasticStateSet.getFinalElasticStateSet());
        
        
        return  eda;
        
    }
    
    
    
    
    public QoRModel getQoRModel(String dataAssetID) {
        
            
                    
            String qorXML="";
        
        try {

            InputStream inputStream = null;
            
            String sql = "SELECT * FROM InputSpecification, DataAssetFunction "
                    + "WHERE InputSpecification.name = DataAssetFunction.edaas "
                    + "AND DataAssetFunction.dataAssetID='"+dataAssetID+"'";
            
            ResultSet rs = connectionManager.ExecuteQuery(sql);
            
            try {
                while (rs.next()) {
                    inputStream = rs.getBinaryStream("elasticity_processes");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            StringWriter writer = new StringWriter();
            String encoding = StandardCharsets.UTF_8.name();

            IOUtils.copy(inputStream, writer, encoding);
            qorXML = writer.toString();
            
        } catch (IOException ex) {
                Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        QoRModel qoRModel=null;
        try {
            qoRModel = JAXBUtils.unmarshal(qorXML, QoRModel.class);
        } catch (JAXBException ex) {
            Logger.getLogger(ElasticityProcessesStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return qoRModel;
        
    }
    
    public List<ElasticService> getElasticServices() {
        String sql = "SELECT * from ElasticService";

        List<ElasticService> listOfElasticServices = new ArrayList<ElasticService>();

        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {
                String actionID = rs.getString("actionID");
                String serviceID = rs.getString("serviceID");
                String uri = rs.getString("uri");

                ElasticService elasticService = new ElasticService(actionID, serviceID, uri);
                elasticService.setRequest(0);
                listOfElasticServices.add(elasticService);
            }

        } catch (Exception ex) {
            System.err.println(ex);
        }

        
        
        return listOfElasticServices;
    }
    
       public PrimitiveActionMetadata getPrimitiveActionMetadata(String eDaaSName) {
           
        
        String sql = "SELECT * FROM InputSpecification WHERE name ='" + eDaaSName + "'";
        InputStream inputStream = null;
        String primitiveXML = "";
        ResultSet rs = connectionManager.ExecuteQuery(sql);

        try {
            while (rs.next()) {
                inputStream = rs.getBinaryStream("elasticity_process_config");
            }

            StringWriter writer = new StringWriter();
            String encoding = StandardCharsets.UTF_8.name();

            IOUtils.copy(inputStream, writer, encoding);
            primitiveXML = writer.toString();
        } catch (Exception ex) {
            System.err.println(ex);
        }

        Configuration cfg = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        YamlUtils.setFilePath(cfg.getConfigPath());
        PrimitiveActionMetadata primitiveActionMetadata = YamlUtils.unmarshallYaml(PrimitiveActionMetadata.class, primitiveXML);

        return primitiveActionMetadata;
    }
       
       public String getElasticityProcesses(String eDaaSName){
     
        String sql = "SELECT * FROM ElasticDaaS WHERE name='" + eDaaSName + "'";
        String elProcessXML = "";
        

        ResultSet rs = connectionManager.ExecuteQuery(sql);

        try {
            while (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("elasticity_processes");
                StringWriter writer = new StringWriter();
                String encoding = StandardCharsets.UTF_8.name();
                IOUtils.copy(inputStream, writer, encoding);
                elProcessXML = writer.toString();          
                
            }

            rs.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }

        return elProcessXML;

    }

    public void storeElasticServices(List<ElasticService> listOfElasticServices) {

        for (ElasticService es : listOfElasticServices) {

            String sql = "INSERT INTO ElasticService (actionID, serviceID, uri) VALUES ('" + es.getActionID() + "','" + es.getServiceID() + "','" + es.getUri() + "')";
            connectionManager.ExecuteUpdate(sql);

        }
    }
    
  

    public void cleanElasticServices() {

        String sql = "TRUNCATE TABLE ElasticService";

        connectionManager.ExecuteUpdate(sql);

    }

}
