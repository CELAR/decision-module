/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */

package at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Metric">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ReferenceTo">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "metric",
    "referenceTo"
})
public class UnaryRestriction {

    @XmlElement(name = "Metric", required = false)
    protected String metric;
    @XmlElement(name = "ReferenceTo", required = false)
    protected ReferenceTo referenceTo;
    @XmlAttribute(name = "Type")
    protected String type;

    /**
     * Gets the value of the metric property.
     * 
     * @return
     *     possible object is
     *     {@link SYBLSpecification.Strategy.Condition.UnaryRestriction.Metric }
     *     
     */
    public String getMetric() {
        return metric;
    }

    /**
     * Sets the value of the metric property.
     * 
     * @param value
     *     allowed object is
     *     {@link SYBLSpecification.Strategy.Condition.UnaryRestriction.Metric }
     *     
     */
    public void setMetric(String value) {
        this.metric = value;
    }

    /**
     * Gets the value of the referenceTo property.
     * 
     * @return
     *     possible object is
     *     {@link SYBLSpecification.Strategy.Condition.UnaryRestriction.ReferenceTo }
     *     
     */
    public ReferenceTo getReferenceTo() {
        return referenceTo;
    }

    /**
     * Sets the value of the referenceTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SYBLSpecification.Strategy.Condition.UnaryRestriction.ReferenceTo }
     *     
     */
    public void setReferenceTo(ReferenceTo value) {
        this.referenceTo = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }


  

   

}
