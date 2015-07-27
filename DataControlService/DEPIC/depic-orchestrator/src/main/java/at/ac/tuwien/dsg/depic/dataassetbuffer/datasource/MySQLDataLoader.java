/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.dataassetbuffer.datasource;

import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.eda.dataasset.DataAsset;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;
import at.ac.tuwien.dsg.depic.common.utils.RestfulWSClient;

import at.ac.tuwien.dsg.depic.dataassetbuffer.store.MySqlDataAssetStore;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.YamlUtils;


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

/**
 *
 * @author Jun
 */
public class MySQLDataLoader implements DataLoader{
    
   
    private String requestToGetDataAsset(String daw){
        
            Configuration config = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            String ip = config.getConfig("DAF.MANAGEMENT.IP");
            String port = config.getConfig("DAF.MANAGEMENT.PORT");
            String resource = config.getConfig("DAF.MANAGEMENT.RESOURCE.DAW")+ "/" + DBType.MYSQL.getDBType();
            
            RestfulWSClient rs = new RestfulWSClient(ip, port, resource);
            String returnStr = rs.callPutMethod(daw);
            
        return returnStr;
    }

    private String getDataPartition(String dataAssetID, String dataPartitionID) {

        Configuration config = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        String ip = config.getConfig("DAF.MANAGEMENT.IP");
        String port = config.getConfig("DAF.MANAGEMENT.PORT");
        String resource = config.getConfig("DAF.MANAGEMENT.RESOURCE.DATAASSET")+ "/" + DBType.MYSQL.getDBType();

      
        
        DataPartitionRequest dataPartitionRequest = new DataPartitionRequest("", "", dataAssetID, dataPartitionID);
        
        String dataPartitionXML = "";
        try {
            dataPartitionXML = JAXBUtils.marshal(dataPartitionRequest, DataPartitionRequest.class);
        } catch (JAXBException ex) {
            Logger.getLogger(MySQLDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        RestfulWSClient rs = new RestfulWSClient(ip, port, resource);
        String dataParstitionXML = rs.callPutMethod(dataPartitionXML);
        
        return dataParstitionXML;
    }
    
    
    
  
    
    @Override
    public String getNoOfParitionRepo(DataPartitionRequest request){
        MySqlDataAssetStore das = new MySqlDataAssetStore();
        String noOfPartition =das.getNoOfPartitionRepo(request);
        return noOfPartition;
    }
    
    @Override
    public String getDataPartitionRepo(DataPartitionRequest request){
        MySqlDataAssetStore das = new MySqlDataAssetStore();
        String daXML =das.getDataPartitionRepo(request);
        return daXML;
    }
    
    @Override
    public void saveDataPartitionRepo(DataAsset dataAsset){
        MySqlDataAssetStore das = new MySqlDataAssetStore();
        das.saveDataPartitionRepo(dataAsset);
        
    }
 
}
