/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.common.entity.eda.dataasset;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "DataItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataItem {
    
    @XmlElement(name = "listOfAttributes", required = true)
    List<DataAttribute> listOfAttributes;

    public DataItem() {
    }

    public DataItem(List<DataAttribute> listOfAttributes) {
        this.listOfAttributes = listOfAttributes;
    }

    public List<DataAttribute> getListOfAttributes() {
        return listOfAttributes;
    }

    public void setListOfAttributes(List<DataAttribute> listOfAttributes) {
        this.listOfAttributes = listOfAttributes;
    }
    
    
}
