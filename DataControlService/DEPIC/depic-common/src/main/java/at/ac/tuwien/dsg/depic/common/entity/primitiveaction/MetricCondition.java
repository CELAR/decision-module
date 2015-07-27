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

@XmlRootElement(name = "MetricCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetricCondition {
    
    @XmlElement(name = "metricName", required = true)
    String metricName;
    
    @XmlElement(name = "conditionID", required = true)
    String conditionID;
    
    @XmlElement(name = "lowerBound", required = true)
    double lowerBound;
    
    @XmlElement(name = "upperBound", required = true)
    double upperBound;

    public MetricCondition() {
    }

    public MetricCondition(String metricName, String conditionID, double lowerBound, double upperBound) {
        this.metricName = metricName;
        this.conditionID = conditionID;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getConditionID() {
        return conditionID;
    }

    public void setConditionID(String conditionID) {
        this.conditionID = conditionID;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    
    
    
}
