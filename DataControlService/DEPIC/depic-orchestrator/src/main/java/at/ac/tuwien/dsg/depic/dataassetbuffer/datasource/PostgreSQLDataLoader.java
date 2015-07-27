/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.datasource;

import at.ac.tuwien.dsg.depic.dataassetbuffer.store.PostgreSQLDataAssetStore;
import at.ac.tuwien.dsg.depic.common.entity.eda.dataasset.DataAsset;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;

/**
 *
 * @author Jun
 */
public class PostgreSQLDataLoader implements DataLoader{

    @Override
    public String getNoOfParitionRepo(DataPartitionRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDataPartitionRepo(DataPartitionRequest request) {
        
        PostgreSQLDataAssetStore postgreSQLDataAssetStore = new PostgreSQLDataAssetStore();
        String dataAssetXML = postgreSQLDataAssetStore.getDataPartitionRepo(request);
        return  dataAssetXML;
 
    }
    
    @Override
    public void saveDataPartitionRepo(DataAsset dataAsset){
        PostgreSQLDataAssetStore postgreSQLDataAssetStore = new PostgreSQLDataAssetStore();
        postgreSQLDataAssetStore.saveDataPartitionRepo(dataAsset);
        
    }
    
}
