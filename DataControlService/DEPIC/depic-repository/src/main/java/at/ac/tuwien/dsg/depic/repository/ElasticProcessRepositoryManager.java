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
package at.ac.tuwien.dsg.depic.repository;

import at.ac.tuwien.dsg.depic.common.entity.runtime.DeployAction;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ElasticService;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.MySqlConnectionManager;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class ElasticProcessRepositoryManager {

    MySqlConnectionManager connectionManager;
    String classPath;


    public ElasticProcessRepositoryManager(String classPath) {
        this.classPath = classPath;
        Configuration config = new Configuration(classPath);
        String ip = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.IP");
        String port = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.PORT");
        String database = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.DATABASE");
        String username = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.USERNAME");
        String password = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.PASSWORD");
        
        connectionManager = new MySqlConnectionManager(ip, port, database, username, password);

    }
    
    
    
    public void storeQoRAndPrimitiveActionMeatadata(String edaas, String qor, String primitiveActionMetadata, DBType type){
        InputStream qorStream = new ByteArrayInputStream(qor.getBytes(StandardCharsets.UTF_8));
        InputStream elasticityProcessesStream = new ByteArrayInputStream(primitiveActionMetadata.getBytes(StandardCharsets.UTF_8));
        
        
        List<InputStream> listOfInputStreams = new ArrayList<InputStream>();
        listOfInputStreams.add(qorStream);
        listOfInputStreams.add(elasticityProcessesStream);
        
        String sql = "INSERT INTO InputSpecification (name, qor, elasticity_process_config, type) VALUES ('"+edaas+"',?,?,'"+type.getDBType()+"')";
        connectionManager.ExecuteUpdateBlob(sql, listOfInputStreams);

        
    }
    

    
    
    
    public  void storeElasticityProcesses(String name, String elasticStateSetXML, String elasticityProcesses, String deploymentDesciption, String type){
       
        InputStream elasticStateSetStream = new ByteArrayInputStream(elasticStateSetXML.getBytes(StandardCharsets.UTF_8));
        InputStream elasticityProcessesStream = new ByteArrayInputStream(elasticityProcesses.getBytes(StandardCharsets.UTF_8));
        InputStream deploymentDesciptionStream = new ByteArrayInputStream(deploymentDesciption.getBytes(StandardCharsets.UTF_8));
    
        
        List<InputStream> listOfInputStreams = new ArrayList<InputStream>();
        listOfInputStreams.add(elasticStateSetStream);
        listOfInputStreams.add(elasticityProcessesStream);
        listOfInputStreams.add(deploymentDesciptionStream);
        
        
        String sql = "INSERT INTO ElasticDaaS (name, elasticStateSet, elasticity_processes, deployment_descriptions, type) VALUES ('"+name+"',?,?,?,'"+type+"')";
    
        connectionManager.ExecuteUpdateBlob(sql, listOfInputStreams);
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
    
    
    public  void storeDataAssetFunction(String edaasName, String dataAssetID, String dataAssetFunction, String noOfPartition){
       
       
        InputStream dataAssetFunctionStream = new ByteArrayInputStream(dataAssetFunction.getBytes(StandardCharsets.UTF_8));
        
        List<InputStream> listOfInputStreams = new ArrayList<InputStream>();
        listOfInputStreams.add(dataAssetFunctionStream);

        String sql = "INSERT INTO DataAssetFunction (edaas, dataAssetID, noOfPartition, dataAssetFunction) "
                + "VALUES ('"+edaasName+"','"+dataAssetID+"',"+noOfPartition+",?)";
    
        connectionManager.ExecuteUpdateBlob(sql, listOfInputStreams);
    }
    
    public void updateDataAssetFunction(String edaasName, String dataAssetID, String dataAssetFunction, String noOfPartition){
        String sql = "UPDATE DataAssetFunction SET noOfPartition="+noOfPartition+" WHERE dataAssetFunction='"+dataAssetFunction+"'";
        connectionManager.ExecuteUpdate(sql);
    }
    
  
    public DeployAction getPrimitiveAction(String actionName){
        String sql = "select * from PrimitiveAction where actionName='" + actionName+"'";
        DeployAction deployAction=null;
  
        
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {
            
            
                String artifact = rs.getString("artifact"); 
                String type = rs.getString("type"); 
                String restapi = rs.getString("restapi"); 
                deployAction = new DeployAction(actionName,actionName, type, artifact, restapi);          
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return deployAction;
    }
  
    public List<String> getElasticDaasNames(){
        String sql = "select name from InputSpecification";
        
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        List<String> listOfEDaases = new ArrayList<String>();
        try {
            while (rs.next()) {
            
                String name = rs.getString("name");    
                listOfEDaases.add(name);
       
            }
            
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return listOfEDaases;
    }
    
    
    public ResultSet getDataAssetFunction(String edaasName){
        
        String sql = "select edaas, dataAssetID  from DataAssetFunction WHERE edaas='"+edaasName+"'";
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        return rs;
        
    }
    
    public void storeElasticServices(List<ElasticService> listOfElasticServices){
        
        String sql_es ="TRUNCATE TABLE ElasticService";
        connectionManager.ExecuteUpdate(sql_es);
        
        for (ElasticService es : listOfElasticServices){
            
            if (es.getRequest()!=-1) {
            String sql = "INSERT INTO ElasticService (actionID, serviceID, uri) VALUES ('"+es.getActionID()+"','"+es.getServiceID()+"','"+es.getUri()+"')";
            connectionManager.ExecuteUpdate(sql);
            }
        }
    }
    
    public void cleanElasticityProcess(){

        String sql_daf ="TRUNCATE TABLE DataAssetFunction";
        String sql_es ="TRUNCATE TABLE ElasticService";
        String sql_ed ="TRUNCATE TABLE ElasticDaaS";
        String sql_in ="TRUNCATE TABLE InputSpecification";
      
        
        connectionManager.ExecuteUpdate(sql_daf);
        connectionManager.ExecuteUpdate(sql_es);
        connectionManager.ExecuteUpdate(sql_ed);
        connectionManager.ExecuteUpdate(sql_in);
        

    }
}
