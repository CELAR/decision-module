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

@XmlRootElement(name = "QElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class QElement {
    
    @XmlElement(name = "qElementID")
    String qElementID;
    
    @XmlElement(name = "listOfRanges")
    List<String> listOfRanges;
    
    @XmlElement(name = "price")
    double price;


    public QElement() {
    }

    public QElement(String qElementID, List<String> listOfRanges, double price) {
        this.qElementID = qElementID;
        this.listOfRanges = listOfRanges;
        this.price = price;
    }
    
    

    public String getqElementID() {
        return qElementID;
    }

    public void setqElementID(String qElementID) {
        this.qElementID = qElementID;
    }

    public List<String> getListOfRanges() {
        return listOfRanges;
    }

    public void setListOfRanges(List<String> listOfRanges) {
        this.listOfRanges = listOfRanges;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    
    
   
    
    
}
