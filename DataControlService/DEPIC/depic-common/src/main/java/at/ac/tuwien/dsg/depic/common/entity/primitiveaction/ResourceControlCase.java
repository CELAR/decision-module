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
@XmlRootElement(name = "ResourceControlCase")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceControlCase {
    
    @XmlElement(name = "estimatedResult", required = true)
    MetricCondition estimatedResult;
    
    @XmlElement(name = "analyticTask")
    AnalyticTask analyticTask;
    
    @XmlElement(name = "listOfResourceControlStrategy", required = true)
    List<ResourceControlStrategy> listOfResourceControlStrategies;
    
    

    public ResourceControlCase() {
    }

    public ResourceControlCase(MetricCondition estimatedResult, AnalyticTask analyticTask, List<ResourceControlStrategy> listOfResourceControlStrategies) {
        this.estimatedResult = estimatedResult;
        this.analyticTask = analyticTask;
        this.listOfResourceControlStrategies = listOfResourceControlStrategies;
    }

    public MetricCondition getEstimatedResult() {
        return estimatedResult;
    }

    public void setEstimatedResult(MetricCondition estimatedResult) {
        this.estimatedResult = estimatedResult;
    }

    public AnalyticTask getAnalyticTask() {
        return analyticTask;
    }

    public void setAnalyticTask(AnalyticTask analyticTask) {
        this.analyticTask = analyticTask;
    }

    public List<ResourceControlStrategy> getListOfResourceControlStrategies() {
        return listOfResourceControlStrategies;
    }

    public void setListOfResourceControlStrategies(List<ResourceControlStrategy> listOfResourceControlStrategies) {
        this.listOfResourceControlStrategies = listOfResourceControlStrategies;
    }

    

    
    
}
