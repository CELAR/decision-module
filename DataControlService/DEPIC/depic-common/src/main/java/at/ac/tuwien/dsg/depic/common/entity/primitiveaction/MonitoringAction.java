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

package at.ac.tuwien.dsg.depic.common.entity.primitiveaction;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "MonitoringAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitoringAction implements PrimitiveAction{
    
    @XmlElement(name = "monitorActionID", required = true)
    String monitorActionID;
    
    @XmlElement(name = "monitoringActionName", required = true)
    String monitoringActionName;
    
    @XmlElement(name = "artifact", required = true)
    Artifact artifact;
    
    @XmlElement(name = "associatedQoRMetric", required = true)
    String associatedQoRMetric; 

    @XmlElement(name = "listOfParameters", required = true)
    List<Parameter> listOfParameters;
    
    public MonitoringAction() {
    }

    public MonitoringAction(String monitorActionID, String monitoringActionName, Artifact artifact, String associatedQoRMetric, List<Parameter> listOfParameters) {
        this.monitorActionID = monitorActionID;
        this.monitoringActionName = monitoringActionName;
        this.artifact = artifact;
        this.associatedQoRMetric = associatedQoRMetric;
        this.listOfParameters = listOfParameters;
    }

    public String getMonitorActionID() {
        return monitorActionID;
    }

    public void setMonitorActionID(String monitorActionID) {
        this.monitorActionID = monitorActionID;
    }

    public String getMonitoringActionName() {
        return monitoringActionName;
    }

    public void setMonitoringActionName(String monitoringActionName) {
        this.monitoringActionName = monitoringActionName;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public String getAssociatedQoRMetric() {
        return associatedQoRMetric;
    }

    public void setAssociatedQoRMetric(String associatedQoRMetric) {
        this.associatedQoRMetric = associatedQoRMetric;
    }

    public List<Parameter> getListOfParameters() {
        return listOfParameters;
    }

    public void setListOfParameters(List<Parameter> listOfParameters) {
        this.listOfParameters = listOfParameters;
    }

    
    
}
