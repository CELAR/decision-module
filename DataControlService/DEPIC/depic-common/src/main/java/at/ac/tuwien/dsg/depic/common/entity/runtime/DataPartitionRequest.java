/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "DataPartitionRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPartitionRequest {
    
    @XmlElement(name = "edaas", required = true)
    String edaas;
    
    @XmlElement(name = "customerID", required = true)
    String customerID;
    
    @XmlElement(name = "dataAssetID", required = true)
    String dataAssetID;
    
    @XmlElement(name = "partitionID", required = true)
    String partitionID;

    public DataPartitionRequest() {
    }

    public DataPartitionRequest(String edaas, String customerID, String dataAssetID, String partitionID) {
        this.edaas = edaas;
        this.customerID = customerID;
        this.dataAssetID = dataAssetID;
        this.partitionID = partitionID;
    }

    public String getEdaas() {
        return edaas;
    }

    public void setEdaas(String edaas) {
        this.edaas = edaas;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getDataAssetID() {
        return dataAssetID;
    }

    public void setDataAssetID(String dataAssetID) {
        this.dataAssetID = dataAssetID;
    }

    public String getPartitionID() {
        return partitionID;
    }

    public void setPartitionID(String partitionID) {
        this.partitionID = partitionID;
    }

    
    
}
