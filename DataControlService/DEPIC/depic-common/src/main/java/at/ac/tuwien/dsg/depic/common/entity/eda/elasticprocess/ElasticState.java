/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "ElasticState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElasticState {
    
    @XmlElement(name = "eStateID", required = true)
    String eStateID;
    
    @XmlElement(name = "listOfConditions", required = true)
    List<MetricCondition> listOfConditions;

    public ElasticState() {
    }

    public ElasticState(String eStateID, List<MetricCondition> listOfConditions) {
        this.eStateID = eStateID;
        this.listOfConditions = listOfConditions;
    }

    public String geteStateID() {
        return eStateID;
    }

    public void seteStateID(String eStateID) {
        this.eStateID = eStateID;
    }

    public List<MetricCondition> getListOfConditions() {
        return listOfConditions;
    }

    public void setListOfConditions(List<MetricCondition> listOfConditions) {
        this.listOfConditions = listOfConditions;
    }


    
}
