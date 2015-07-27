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
package at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


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
