/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "DataElasticityManagementProcess")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataElasticityManagementProcess {

    @XmlElement(name = "monitoringProcess")
    MonitoringProcess monitoringProcess;
    
    @XmlElement(name = "listOfAdjustmentProcesses")
    List<AdjustmentProcess> listOfAdjustmentProcesses;
    
    @XmlElement(name = "sYBLSpecification")
    SYBLSpecification sYBLSpecification;

    public DataElasticityManagementProcess() {
    }

    public DataElasticityManagementProcess(MonitoringProcess monitoringProcess, List<AdjustmentProcess> listOfAdjustmentProcesses, SYBLSpecification sYBLSpecification) {
        this.monitoringProcess = monitoringProcess;
        this.listOfAdjustmentProcesses = listOfAdjustmentProcesses;
        this.sYBLSpecification = sYBLSpecification;
    }

    public List<AdjustmentProcess> getListOfAdjustmentProcesses() {
        return listOfAdjustmentProcesses;
    }

    public void setListOfAdjustmentProcesses(List<AdjustmentProcess> listOfAdjustmentProcesses) {
        this.listOfAdjustmentProcesses = listOfAdjustmentProcesses;
    }

    public SYBLSpecification getsYBLSpecification() {
        return sYBLSpecification;
    }

    public void setsYBLSpecification(SYBLSpecification sYBLSpecification) {
        this.sYBLSpecification = sYBLSpecification;
    }

    public MonitoringProcess getMonitoringProcess() {
        return monitoringProcess;
    }

    public void setMonitoringProcess(MonitoringProcess monitoringProcess) {
        this.monitoringProcess = monitoringProcess;
    }

    
}
