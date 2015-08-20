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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "MonitoringSession")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitoringSession {
    
    @XmlElement(name = "sessionID", required = true)
    String sessionID;
    
    @XmlElement(name = "edaasName", required = true)
    String edaasName;
    
    @XmlElement(name = "dataAssetID", required = true)
    String dataAssetID;
    
    @XmlElement(name = "eDaaSType", required = true)
    private DBType eDaaSType;
    
    @XmlElement(name = "listOfExpectedElasticStates", required = true)
    List<String> listOfExpectedElasticStates;

    public MonitoringSession() {
    }

    public MonitoringSession(String sessionID, String edaasName, String dataAssetID, DBType eDaaSType, List<String> listOfExpectedElasticStates) {
        this.sessionID = sessionID;
        this.edaasName = edaasName;
        this.dataAssetID = dataAssetID;
        this.eDaaSType = eDaaSType;
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

    public DBType geteDaaSType() {
        return eDaaSType;
    }

    public void seteDaaSType(DBType eDaaSType) {
        this.eDaaSType = eDaaSType;
    }

    public List<String> getListOfExpectedElasticStates() {
        return listOfExpectedElasticStates;
    }

    public void setListOfExpectedElasticStates(List<String> listOfExpectedElasticStates) {
        this.listOfExpectedElasticStates = listOfExpectedElasticStates;
    }

    
    
    
}
