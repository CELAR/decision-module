/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlStrategy;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "ResourceControlPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceControlPlan {
    
    @XmlElement(name = "finalEState", required = true)
    ElasticState finalEState;
    
    @XmlElement(name = "listOfResourceControlStrategy", required = true)
    List<ResourceControlStrategy> listOfResourceControlStrategies;

    public ResourceControlPlan() {
    }

    public ResourceControlPlan(ElasticState finalEState, List<ResourceControlStrategy> listOfResourceControlStrategies) {
        this.finalEState = finalEState;
        this.listOfResourceControlStrategies = listOfResourceControlStrategies;
    }

    public ElasticState getFinalEState() {
        return finalEState;
    }

    public void setFinalEState(ElasticState finalEState) {
        this.finalEState = finalEState;
    }

    public List<ResourceControlStrategy> getListOfResourceControlStrategies() {
        return listOfResourceControlStrategies;
    }

    public void setListOfResourceControlStrategies(List<ResourceControlStrategy> listOfResourceControlStrategies) {
        this.listOfResourceControlStrategies = listOfResourceControlStrategies;
    }
    
    
    
}
