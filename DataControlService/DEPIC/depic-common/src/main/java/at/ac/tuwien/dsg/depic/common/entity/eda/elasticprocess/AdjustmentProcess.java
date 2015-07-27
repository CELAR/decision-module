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

@XmlRootElement(name = "AdjustmentProcess")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdjustmentProcess {

    @XmlElement(name = "finalEState", required = true)
    ElasticState finalEState;
    
    @XmlElement(name = "listOfAdjustmentActions", required = true)
    List<AdjustmentAction> listOfAdjustmentActions;
    
    @XmlElement(name = "directedAcyclicalGraph", required = true)
    DirectedAcyclicalGraph directedAcyclicalGraph;
    
    

    public AdjustmentProcess() {
    }

    public AdjustmentProcess(ElasticState finalEState, List<AdjustmentAction> listOfAdjustmentActions, DirectedAcyclicalGraph directedAcyclicalGraph) {
        this.finalEState = finalEState;
        this.listOfAdjustmentActions = listOfAdjustmentActions;
        this.directedAcyclicalGraph = directedAcyclicalGraph;
    }

    public ElasticState getFinalEState() {
        return finalEState;
    }

    public void setFinalEState(ElasticState finalEState) {
        this.finalEState = finalEState;
    }

    public List<AdjustmentAction> getListOfAdjustmentActions() {
        return listOfAdjustmentActions;
    }

    public void setListOfAdjustmentActions(List<AdjustmentAction> listOfAdjustmentActions) {
        this.listOfAdjustmentActions = listOfAdjustmentActions;
    }

    public DirectedAcyclicalGraph getDirectedAcyclicalGraph() {
        return directedAcyclicalGraph;
    }

    public void setDirectedAcyclicalGraph(DirectedAcyclicalGraph directedAcyclicalGraph) {
        this.directedAcyclicalGraph = directedAcyclicalGraph;
    }

    
    
}
