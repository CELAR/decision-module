/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "MonitoringSession")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitoringSession {
    
    @XmlElement(name = "sessionID", required = true)
    String sessionID;
    
    @XmlElement(name = "edaasName", required = true)
    String edaasName;
    
    @XmlElement(name = "dataAssetID", required = true)
    String dataAssetID;
    
    @XmlElement(name = "listOfExpectedElasticStates", required = true)
    List<String> listOfExpectedElasticStates;

    public MonitoringSession() {
    }

    public MonitoringSession(String sessionID, String edaasName, String dataAssetID, List<String> listOfExpectedElasticStates) {
        this.sessionID = sessionID;
        this.edaasName = edaasName;
        this.dataAssetID = dataAssetID;
        this.listOfExpectedElasticStates = listOfExpectedElasticStates;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getEdaasName() {
        return edaasName;
    }

    public void setEdaasName(String edaasName) {
        this.edaasName = edaasName;
    }

    public String getDataAssetID() {
        return dataAssetID;
    }

    public void setDataAssetID(String dataAssetID) {
        this.dataAssetID = dataAssetID;
    }

    public List<String> getListOfExpectedElasticStates() {
        return listOfExpectedElasticStates;
    }

    public void setListOfExpectedElasticStates(List<String> listOfExpectedElasticStates) {
        this.listOfExpectedElasticStates = listOfExpectedElasticStates;
    }

    
    
    
}
