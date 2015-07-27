/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.common.entity.qor;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "QoRMetric")
@XmlAccessorType(XmlAccessType.FIELD)
public class QoRMetric {
    
    @XmlElement(name = "name", required = true)
    String name;
    
    @XmlElement(name = "unit", required = true)
    String unit;
    
    @XmlElement(name = "listOfRanges", required = true)
    List<Range> listOfRanges;

    public QoRMetric() {
        
    }

    public QoRMetric(String name, String unit, List<Range> listOfRanges) {
        this.name = name;
        this.unit = unit;
        this.listOfRanges = listOfRanges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<Range> getListOfRanges() {
        return listOfRanges;
    }

    public void setListOfRanges(List<Range> listOfRanges) {
        this.listOfRanges = listOfRanges;
    }

    
    
    
    
}
