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

@XmlRootElement(name = "MonitoringAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitoringAction implements PrimitiveAction{
    
    @XmlElement(name = "monitorActionID", required = true)
    String monitorActionID;
    
    @XmlElement(name = "monitoringActionName", required = true)
    String monitoringActionName;
    
    @XmlElement(name = "artifact", required = true)
    Artifact artifact;
    
    @XmlElement(name = "associatedQoRMetric", required = true)
    String associatedQoRMetric; 

    @XmlElement(name = "listOfParameters", required = true)
    List<Parameter> listOfParameters;
    
    public MonitoringAction() {
    }

    public MonitoringAction(String monitorActionID, String monitoringActionName, Artifact artifact, String associatedQoRMetric, List<Parameter> listOfParameters) {
        this.monitorActionID = monitorActionID;
        this.monitoringActionName = monitoringActionName;
        this.artifact = artifact;
        this.associatedQoRMetric = associatedQoRMetric;
        this.listOfParameters = listOfParameters;
    }

    public String getMonitorActionID() {
        return monitorActionID;
    }

    public void setMonitorActionID(String monitorActionID) {
        this.monitorActionID = monitorActionID;
    }

    public String getMonitoringActionName() {
        return monitoringActionName;
    }

    public void setMonitoringActionName(String monitoringActionName) {
        this.monitoringActionName = monitoringActionName;
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

    public List<Parameter> getListOfParameters() {
        return listOfParameters;
    }

    public void setListOfParameters(List<Parameter> listOfParameters) {
        this.listOfParameters = listOfParameters;
    }

    
    
}
