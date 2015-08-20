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



@XmlRootElement(name = "ResourceControlAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceControlAction implements PrimitiveAction{
    
    @XmlElement(name = "resourceControlActionName", required = false)
    String actionName;
    
    @XmlElement(name = "associatedQoRMetric", required = false)
    String associatedQoRMetric; 
    
    @XmlElement(name = "listOfResourceControlStrategies", required = false)
    List<ResourceControlCase> listOfResourceControlCases;

    public ResourceControlAction() {
    }

    public ResourceControlAction(String associatedQoRMetric, List<ResourceControlCase> listOfResourceControlCases) {
        this.associatedQoRMetric = associatedQoRMetric;
        this.listOfResourceControlCases = listOfResourceControlCases;
    }

    public ResourceControlAction(String actionName, String associatedQoRMetric, List<ResourceControlCase> listOfResourceControlCases) {
        this.actionName = actionName;
        this.associatedQoRMetric = associatedQoRMetric;
        this.listOfResourceControlCases = listOfResourceControlCases;
    }
    
    

    public String getAssociatedQoRMetric() {
        return associatedQoRMetric;
    }

    public void setAssociatedQoRMetric(String associatedQoRMetric) {
        this.associatedQoRMetric = associatedQoRMetric;
    }

    public List<ResourceControlCase> getListOfResourceControlCases() {
        return listOfResourceControlCases;
    }

    public void setListOfResourceControlCases(List<ResourceControlCase> listOfResourceControlStrategies) {
        this.listOfResourceControlCases = listOfResourceControlStrategies;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    
    
    
}
