/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.primitiveaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "ResourceControlStrategy")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceControlStrategy {
    
    
    @XmlElement(name = "scaleInCondition", required = true)
    MetricCondition scaleInCondition;
    
    @XmlElement(name = "scaleOutCondition", required = true)
    MetricCondition scaleOutCondition;
    
    @XmlElement(name = "controlMetric", required = true)
    String controlMetric;
    
    @XmlElement(name = "primitiveAction", required = true)
    String primitiveAction;

    public ResourceControlStrategy() {
    }

    public ResourceControlStrategy(MetricCondition scaleInCondition, MetricCondition scaleOutCondition, String controlMetric, String primitiveAction) {
        this.scaleInCondition = scaleInCondition;
        this.scaleOutCondition = scaleOutCondition;
        this.controlMetric = controlMetric;
        this.primitiveAction = primitiveAction;
    }

    public MetricCondition getScaleInCondition() {
        return scaleInCondition;
    }

    public void setScaleInCondition(MetricCondition scaleInCondition) {
        this.scaleInCondition = scaleInCondition;
    }

    public MetricCondition getScaleOutCondition() {
        return scaleOutCondition;
    }

    public void setScaleOutCondition(MetricCondition scaleOutCondition) {
        this.scaleOutCondition = scaleOutCondition;
    }

    public String getControlMetric() {
        return controlMetric;
    }

    public void setControlMetric(String controlMetric) {
        this.controlMetric = controlMetric;
    }

    public String getPrimitiveAction() {
        return primitiveAction;
    }

    public void setPrimitiveAction(String primitiveAction) {
        this.primitiveAction = primitiveAction;
    }

    
    
    
    
}
