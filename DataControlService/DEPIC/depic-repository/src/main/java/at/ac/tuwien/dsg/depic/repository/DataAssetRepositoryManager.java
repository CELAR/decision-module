/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.repository;

import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;

import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.RestfulWSClient;

import at.ac.tuwien.dsg.depic.common.utils.Logger;
import at.ac.tuwien.dsg.depic.common.utils.MySqlConnectionManager;
import at.ac.tuwien.dsg.depic.common.utils.YamlUtils;


/**
 *
 * @author Jun
 */
public class DataAssetRepositoryManager {
    
    MySqlConnectionManager connectionManager;
    String classPath;

    public DataAssetRepositoryManager(String classPath) {
        this.classPath = classPath;
    }

    
    
    
    public String requestToGetDataAsset(DataAnalyticsFunction daf){
        
        Configuration configuration  = new Configuration(classPath);
        String ip = configuration.getConfig("DATA.ASSET.LOADER.IP");
        String port = configuration.getConfig("DATA.ASSET.LOADER.PORT");
        String resource = configuration.getConfig("DATA.ASSET.LOADER.RESOURCE.REQUEST");
      
        
        Logger.logInfo("IP: " + ip);
        Logger.logInfo("PORT: " + port);
        Logger.logInfo("RESOURSE: " + resource);
 
        YamlUtils.setFilePath(configuration.getConfigPath());
        String dafYaml = YamlUtils.marshallYaml(DataAnalyticsFunction.class, daf);
        
        RestfulWSClient ws = new RestfulWSClient(ip, port, resource);
        String noOfDataPartitions = ws.callPutMethod(dafYaml);
   
        return noOfDataPartitions;
    }
    
    
    
    
}
