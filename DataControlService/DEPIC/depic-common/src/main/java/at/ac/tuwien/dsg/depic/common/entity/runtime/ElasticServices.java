/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "ElasticServices")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElasticServices {
    
    @XmlElement(name = "listOfElasticServices", required = true)
    List<ElasticService> listOfElasticServices;

    public ElasticServices() {
    }

    public ElasticServices(List<ElasticService> listOfElasticServices) {
        this.listOfElasticServices = listOfElasticServices;
    }

    public List<ElasticService> getListOfElasticServices() {
        return listOfElasticServices;
    }

    public void setListOfElasticServices(List<ElasticService> listOfElasticServices) {
        this.listOfElasticServices = listOfElasticServices;
    }
    
    
}
