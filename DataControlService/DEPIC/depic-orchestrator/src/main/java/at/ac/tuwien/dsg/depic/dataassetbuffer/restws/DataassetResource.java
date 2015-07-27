/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.restws;

import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.eda.dataasset.DataAsset;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;


import at.ac.tuwien.dsg.depic.dataassetbuffer.datasource.GenericDataLoader;

import at.ac.tuwien.dsg.depic.dataassetbuffer.store.DataAssetFunctionStore;
import at.ac.tuwien.dsg.depic.dataassetbuffer.util.ThroughputMonitor;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.YamlUtils;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

/**
 * REST Web Service
 *
 * @author Jun
 */
@Path("/dataasset")
public class DataassetResource {

 
    @PUT
    @Path("/repo/getpartition")
    @Consumes("application/xml")
    @Produces("application/xml")
    public DataAsset getDataPartition(String dataAssetRequestXML) {

       String log ="RECEIVED: " + dataAssetRequestXML;
       
        Logger.getLogger(DataassetResource.class.getName()).log(Level.INFO, log);
        
        DataPartitionRequest request=null;
        try {
            request = JAXBUtils.unmarshal(dataAssetRequestXML, DataPartitionRequest.class);
        } catch (JAXBException ex) {
            Logger.getLogger(DataassetResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DataAssetFunctionStore dafStore = new DataAssetFunctionStore();
        DBType eDaaSType = dafStore.gEDaaSTypeFromEDaaSName(request.getEdaas());
        
        GenericDataLoader dataLoader = new GenericDataLoader(eDaaSType);
        String daXML =dataLoader.getDataPartitionRepo(request);
          DataAsset da = null;
        try {
            da = JAXBUtils.unmarshal(daXML, DataAsset.class);
        } catch (JAXBException ex) {
            Logger.getLogger(DataassetResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       // ThroughputMonitor.trackingLoad(request);
        
        return da;
    }
    
    @PUT
    @Path("/repo/noofpartition")
    @Consumes("application/xml")
    @Produces("application/xml")
    public String getNoOfPartition(String dataAssetRequestXML) {

       String log ="RECEIVED: " + dataAssetRequestXML;
        Logger.getLogger(DataassetResource.class.getName()).log(Level.INFO, log);
        
        DataPartitionRequest request=null;
        try {
            request = JAXBUtils.unmarshal(dataAssetRequestXML, DataPartitionRequest.class);
        } catch (JAXBException ex) {
            Logger.getLogger(DataassetResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        DataAssetFunctionStore dafStore = new DataAssetFunctionStore();
        DBType eDaaSType = dafStore.gEDaaSTypeFromEDaaSName(request.getEdaas());
        
        GenericDataLoader dataLoader = new GenericDataLoader(eDaaSType);
        String noOfDataPartitionRepo =dataLoader.getNoOfParitionRepo(request);
        
        return noOfDataPartitionRepo;
        
    }
    
    @PUT
    @Path("/repo/savepartition")
    @Consumes("application/xml")
    @Produces("application/xml")
    public String saveDataPartition(String dataAssetXML) {
        
        DataAsset dataAsset=null;
        try {
            dataAsset = JAXBUtils.unmarshal(dataAssetXML, DataAsset.class);
        } catch (JAXBException ex) {
            Logger.getLogger(DataassetResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String[] strs = dataAsset.getDataAssetID().split(";");
        String edaasName = strs[0];
        
        DataAssetFunctionStore dafStore = new DataAssetFunctionStore();
        DBType eDaaSType = dafStore.gEDaaSTypeFromEDaaSName(edaasName);
        GenericDataLoader dataLoader = new GenericDataLoader(eDaaSType);
        dataLoader.saveDataPartitionRepo(dataAsset);
        
        
        return "";
    }
  
  
    

}
