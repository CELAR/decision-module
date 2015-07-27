/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.common.entity.qor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "QoRModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class Range {
    
    @XmlElement(name = "rangeID", required = true)
    String rangeID;
    
    @XmlElement(name = "fromValue", required = true)
    double fromValue;
    
    @XmlElement(name = "toValue", required = true)
    double toValue;

    public Range() {
    }

    public Range(String rangeID, double fromValue, double toValue) {
        this.rangeID = rangeID;
        this.fromValue = fromValue;
        this.toValue = toValue;
    }

    public String getRangeID() {
        return rangeID;
    }

    public void setRangeID(String rangeID) {
        this.rangeID = rangeID;
    }

    public double getFromValue() {
        return fromValue;
    }

    public void setFromValue(double fromValue) {
        this.fromValue = fromValue;
    }

    public double getToValue() {
        return toValue;
    }

    public void setToValue(double toValue) {
        this.toValue = toValue;
    }
    
    
    
    
}
