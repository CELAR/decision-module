/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
  E184.  This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package at.ac.tuwien.dsg.depic.common.entity.runtime;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



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
