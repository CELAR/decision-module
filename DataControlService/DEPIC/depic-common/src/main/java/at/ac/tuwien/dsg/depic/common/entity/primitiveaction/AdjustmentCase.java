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

@XmlRootElement(name = "AdjustmentCase")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdjustmentCase {
    
    @XmlElement(name = "estimatedResult")
    MetricCondition estimatedResult;
    
    @XmlElement(name = "analyticTask")
    AnalyticTask analyticTask;
    
    @XmlElement(name = "listOfParameters")
    List<Parameter> listOfParameters;
    
    public AdjustmentCase() {
    }

    public AdjustmentCase(MetricCondition estimatedResult, AnalyticTask analyticTask, List<Parameter> listOfParameters) {
        this.estimatedResult = estimatedResult;
        this.analyticTask = analyticTask;
        this.listOfParameters = listOfParameters;
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

    public List<Parameter> getListOfParameters() {
        return listOfParameters;
    }

    public void setListOfParameters(List<Parameter> listOfParameters) {
        this.listOfParameters = listOfParameters;
    }

    
    
    
    
}
