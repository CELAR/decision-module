/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.PrimitiveActionMetadata;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "DepicDescription")
@XmlAccessorType(XmlAccessType.FIELD)
public class DepicDescription {
    
    @XmlElement(name = "DaaSDescription")
    DaaSDescription daaSDescription;
    
    @XmlElement(name = "PrimitiveActionMetadata")
    PrimitiveActionMetadata primitiveActionMetadata;

    public DepicDescription() {
    }

    public DepicDescription(DaaSDescription daaSDescription, PrimitiveActionMetadata primitiveActionMetadata) {
        this.daaSDescription = daaSDescription;
        this.primitiveActionMetadata = primitiveActionMetadata;
    }

    public DaaSDescription getDaaSDescription() {
        return daaSDescription;
    }

    public void setDaaSDescription(DaaSDescription daaSDescription) {
        this.daaSDescription = daaSDescription;
    }

    public PrimitiveActionMetadata getPrimitiveActionMetadata() {
        return primitiveActionMetadata;
    }

    public void setPrimitiveActionMetadata(PrimitiveActionMetadata primitiveActionMetadata) {
        this.primitiveActionMetadata = primitiveActionMetadata;
    }

}
