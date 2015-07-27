/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.primitiveaction;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

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
