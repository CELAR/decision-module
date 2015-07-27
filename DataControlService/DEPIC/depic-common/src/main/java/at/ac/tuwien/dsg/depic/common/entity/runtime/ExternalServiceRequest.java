/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Parameter;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "ExternalServiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalServiceRequest {
    
    
    @XmlElement(name = "edaas", required = true)
    String edaas;
    
    @XmlElement(name = "customerID", required = true)
    String customerID;
    
    @XmlElement(name = "dataAssetID", required = true)
    String dataAssetID;
    
    @XmlElement(name = "listOfParameters", required = true)
    List<Parameter> listOfParameters;

    public ExternalServiceRequest() {
    }

    public ExternalServiceRequest(String edaas, String customerID, String dataAssetID, List<Parameter> listOfParameters) {
        this.edaas = edaas;
        this.customerID = customerID;
        this.dataAssetID = dataAssetID;
        this.listOfParameters = listOfParameters;
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

    public List<Parameter> getListOfParameters() {
        return listOfParameters;
    }

    public void setListOfParameters(List<Parameter> listOfParameters) {
        this.listOfParameters = listOfParameters;
    }
    
    
    
    
    
}
