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

@XmlRootElement(name = "AnalyticTask")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnalyticTask {
    
    @XmlElement(name = "taskName", required = true)
    String taskName;
    
    @XmlElement(name = "parameters", required = true)
    List<Parameter> parameters;

    public AnalyticTask() {
    }

    
    public AnalyticTask(String taskName, List<Parameter> parameters) {
        this.taskName = taskName;
        this.parameters = parameters;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
    
    
    
    
    
    
    
    
}
