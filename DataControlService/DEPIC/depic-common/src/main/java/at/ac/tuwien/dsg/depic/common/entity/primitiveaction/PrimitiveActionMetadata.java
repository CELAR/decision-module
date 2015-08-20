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



@XmlRootElement(name = "PrimitiveActionMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class PrimitiveActionMetadata {
    
    @XmlElement(name = "listOfAdjustmentActions", required = true)
    List<AdjustmentAction> listOfAdjustmentActions;
    
    @XmlElement(name = "listOfMonitoringActions", required = true)
    List<MonitoringAction> listOfMonitoringActions;
    
    @XmlElement(name = "listOfResourceControls", required = true)
    List<ResourceControlAction> listOfResourceControls;
     

    public PrimitiveActionMetadata() {
    }

    public PrimitiveActionMetadata(List<AdjustmentAction> listOfAdjustmentActions, List<MonitoringAction> listOfMonitoringActions, List<ResourceControlAction> listOfResourceControls) {
        this.listOfAdjustmentActions = listOfAdjustmentActions;
        this.listOfMonitoringActions = listOfMonitoringActions;
        this.listOfResourceControls = listOfResourceControls;
    }

    public List<AdjustmentAction> getListOfAdjustmentActions() {
        return listOfAdjustmentActions;
    }

    public void setListOfAdjustmentActions(List<AdjustmentAction> listOfAdjustmentActions) {
        this.listOfAdjustmentActions = listOfAdjustmentActions;
    }

    public List<MonitoringAction> getListOfMonitoringActions() {
        return listOfMonitoringActions;
    }

    public void setListOfMonitoringActions(List<MonitoringAction> listOfMonitoringActions) {
        this.listOfMonitoringActions = listOfMonitoringActions;
    }

    public List<ResourceControlAction> getListOfResourceControls() {
        return listOfResourceControls;
    }

    public void setListOfResourceControls(List<ResourceControlAction> listOfResourceControls) {
        this.listOfResourceControls = listOfResourceControls;
    }

    

    
    
}
