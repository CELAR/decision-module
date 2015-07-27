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
