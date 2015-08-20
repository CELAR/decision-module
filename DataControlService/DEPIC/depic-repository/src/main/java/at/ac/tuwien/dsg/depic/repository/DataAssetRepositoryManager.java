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

import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;

import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.RestfulWSClient;

import at.ac.tuwien.dsg.depic.common.utils.Logger;
import at.ac.tuwien.dsg.depic.common.utils.MySqlConnectionManager;
import at.ac.tuwien.dsg.depic.common.utils.YamlUtils;



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
