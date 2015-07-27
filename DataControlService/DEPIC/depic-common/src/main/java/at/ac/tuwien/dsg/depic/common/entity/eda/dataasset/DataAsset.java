/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.common.entity.eda.dataasset;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "DataAsset")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataAsset {
    
    @XmlElement(name = "dataAssetID", required = true)
    String dataAssetID;
    
    @XmlElement(name = "partition", required = true)
    int partition;

    @XmlElement(name = "listOfDataItems", required = true)
    List<DataItem> listOfDataItems;

    public DataAsset() {
    }

    public DataAsset(String dataAssetID, int partition, List<DataItem> listOfDataItems) {
        this.dataAssetID = dataAssetID;
        this.partition = partition;
        this.listOfDataItems = listOfDataItems;
    }

    public String getDataAssetID() {
        return dataAssetID;
    }

    public void setDataAssetID(String dataAssetID) {
        this.dataAssetID = dataAssetID;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public List<DataItem> getListOfDataItems() {
        return listOfDataItems;
    }

    public void setListOfDataItems(List<DataItem> listOfDataItems) {
        this.listOfDataItems = listOfDataItems;
    }

    

    
}
