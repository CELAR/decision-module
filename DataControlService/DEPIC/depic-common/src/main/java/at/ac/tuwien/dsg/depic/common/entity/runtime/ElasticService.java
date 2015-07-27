/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "ElasticService")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElasticService {
    
    @XmlElement(name = "actionID", required = true)
    private String actionID;
    
    @XmlElement(name = "serviceID", required = true)
    private String serviceID;
    
    @XmlElement(name = "uri", required = true)
    private String uri;
    
    @XmlElement(name = "updatedTimeStamp")
    private long updatedTimeStamp;
    
    @XmlElement(name = "request")
    private int request;
    

    public ElasticService() {
    }

    public ElasticService(String actionID, String serviceID, String uri) {
        this.actionID = actionID;
        this.serviceID = serviceID;
        this.uri = uri;
        request=0;
    }

    public String getActionID() {
        return actionID;
    }

    public void setActionID(String actionID) {
        this.actionID = actionID;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    public long getUpdatedTimeStamp() {
        return updatedTimeStamp;
    }

    public void setUpdatedTimeStamp(long updatedTimeStamp) {
        this.updatedTimeStamp = updatedTimeStamp;
    }

   
    
    
    
}
