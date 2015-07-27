/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.datasource;

import at.ac.tuwien.dsg.depic.common.entity.eda.dataasset.DataAsset;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;

/**
 *
 * @author Jun
 */
public interface DataLoader {

   
    public String getNoOfParitionRepo(DataPartitionRequest request);

    public String getDataPartitionRepo(DataPartitionRequest request);
    
    public void saveDataPartitionRepo(DataAsset dataAsset);

}
