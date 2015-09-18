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

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


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
