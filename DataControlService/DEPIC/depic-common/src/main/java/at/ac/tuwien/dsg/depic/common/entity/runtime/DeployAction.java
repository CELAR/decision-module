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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "DeployAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeployAction {
 
    @XmlElement(name = "actionID", required = true)
    String actionID;
    
    @XmlElement(name = "actionName", required = true)
    String actionName;
    
    @XmlElement(name = "artifact", required = false)
    String artifact;
    
    @XmlElement(name = "artifactType", required = false)
    String artifactType;
    
    @XmlElement(name = "apiEndpoint", required = false)
    String apiEndpoint;	
    
    @XmlElement(name = "deploymentEndpoint", required = false)
    String deploymentEndpoint;

    public DeployAction() {
    }
    
    public DeployAction(String actionID, String actionName, String artifactType, String artifactRef) {
        this.actionID = actionID;
        this.actionName = actionName;
        this.artifactType = artifactType;
        this.artifact = artifactRef;
    }

    public DeployAction(String actionID, String actionName, String artifactType, String artifact, String apiEndpoint) {
        this.actionID = actionID;
        this.actionName = actionName;
        this.artifact = artifact;
        this.artifactType = artifactType;
        this.apiEndpoint = apiEndpoint;
    }

    public String getActionID() {
        return actionID;
    }

    public void setActionID(String actionID) {
        this.actionID = actionID;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getDeploymentEndpoint() {
        return deploymentEndpoint;
    }

    public void setDeploymentEndpoint(String deploymentEndpoint) {
        this.deploymentEndpoint = deploymentEndpoint;
    }
    
    

    
    
    
    
}
