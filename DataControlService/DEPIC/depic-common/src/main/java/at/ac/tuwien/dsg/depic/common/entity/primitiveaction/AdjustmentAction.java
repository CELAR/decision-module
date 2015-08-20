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


@XmlRootElement(name = "AdjustmentAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdjustmentAction implements PrimitiveAction{
    
    @XmlElement(name = "adjustmentActionID", required = true)
    String actionID;
    
    @XmlElement(name = "adjustmentActionName", required = true)
    String actionName;
    
    @XmlElement(name = "artifact", required = true)
    Artifact artifact;
    
    @XmlElement(name = "associatedQoRMetric", required = true)
    String associatedQoRMetric; 
    
    @XmlElement(name = "listOfPrerequisiteActionIDs", required = true)
    List<String> listOfPrerequisiteActionIDs;
    
    @XmlElement(name = "listOfAdjustmentCases", required = true)
    List<AdjustmentCase> listOfAdjustmentCases;
    
    
    public AdjustmentAction() {
    }

    public AdjustmentAction(String actionID, String actionName, Artifact artifact, String associatedQoRMetric, List<String> listOfPrerequisiteActionIDs, List<AdjustmentCase> listOfAdjustmentCases) {
        this.actionID = actionID;
        this.actionName = actionName;
        this.artifact = artifact;
        this.associatedQoRMetric = associatedQoRMetric;
        this.listOfPrerequisiteActionIDs = listOfPrerequisiteActionIDs;
        this.listOfAdjustmentCases = listOfAdjustmentCases;
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

    public List<String> getListOfPrerequisiteActionIDs() {
        return listOfPrerequisiteActionIDs;
    }

    public void setListOfPrerequisiteActionIDs(List<String> listOfPrerequisiteActionIDs) {
        this.listOfPrerequisiteActionIDs = listOfPrerequisiteActionIDs;
    }

    public List<AdjustmentCase> getListOfAdjustmentCases() {
        return listOfAdjustmentCases;
    }

    public void setListOfAdjustmentCases(List<AdjustmentCase> listOfAdjustmentCases) {
        this.listOfAdjustmentCases = listOfAdjustmentCases;
    }
    
}
