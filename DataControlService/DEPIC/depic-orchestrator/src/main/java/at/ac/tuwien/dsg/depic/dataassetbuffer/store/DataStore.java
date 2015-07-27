/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.store;

import at.ac.tuwien.dsg.depic.common.entity.eda.dataasset.DataAsset;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;

/**
 *
 * @author Jun
 */
public interface DataStore {

    public  void saveDataAsset(String daXML, String dafName, String partitionID);

    public void removeDataAsset(String dafName);

    public String getDataAssetXML(String dafName, String partitionID);

    public String copyDataAssetRepo(DataPartitionRequest request);

    public String getDataPartitionRepo(DataPartitionRequest request);

    public String getNoOfPartitionRepo(DataPartitionRequest request);

    public void saveDataPartitionRepo(DataAsset dataAssetPartition);

    public void insertDataPartitionRepo(DataAsset dataAssetPartition);
}
