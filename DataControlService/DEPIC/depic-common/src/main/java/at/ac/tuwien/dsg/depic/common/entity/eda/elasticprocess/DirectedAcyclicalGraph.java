/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "DirectedAcyclicalGraph")
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectedAcyclicalGraph {
    
    @XmlElement(name = "listOfActions", required = true)
    List<Action> listOfActions;
    
    @XmlElement(name = "listOfParallelGateways", required = true)
    List<ParallelGateway> listOfParallelGateways;

    public DirectedAcyclicalGraph() {
    }

    public List<Action> getListOfActions() {
        return listOfActions;
    }

    public void setListOfActions(List<Action> listOfActions) {
        this.listOfActions = listOfActions;
    }

    public List<ParallelGateway> getListOfParallelGateways() {
        return listOfParallelGateways;
    }

    public void setListOfParallelGateways(List<ParallelGateway> listOfParallelGateways) {
        this.listOfParallelGateways = listOfParallelGateways;
    }
    
    
    
    
}
