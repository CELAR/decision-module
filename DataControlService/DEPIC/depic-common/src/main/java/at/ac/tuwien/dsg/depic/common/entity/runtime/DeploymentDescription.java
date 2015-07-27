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

package at.ac.tuwien.dsg.depic.common.entity.runtime;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement (name = "DeploymentDescription")
public class DeploymentDescription {
    List<DeployAction> listOfDeployActions;

    public DeploymentDescription() {
    }

    public DeploymentDescription(List<DeployAction> listOfDeployActions) {
        this.listOfDeployActions = listOfDeployActions;
    }

    public List<DeployAction> getListOfDeployActions() {
        return listOfDeployActions;
    }

    @XmlElement (name ="ListOfDeployActions")
    public void setListOfDeployActions(List<DeployAction> listOfDeployActions) {
        this.listOfDeployActions = listOfDeployActions;
    }
    
    
    
    public String toXMLString() {
        String xmlString = "";

        try {

            StringWriter objWriter = new StringWriter();

            JAXBContext jaxbContext = JAXBContext.newInstance(DeploymentDescription.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(this, objWriter);
 
            //jaxbMarshaller.marshal(eState, System.out);
           xmlString = objWriter.toString();

        } catch (JAXBException e) {
            System.err.println("Ex: " + e.toString());
        }

        return xmlString;
    }
    
    public static DeploymentDescription fromXMLString(String xmlStr) {

        DeploymentDescription javaObj = null;
         try {
            
                StringReader reader = new StringReader(xmlStr);   
		JAXBContext jaxbContext = JAXBContext.newInstance(DeploymentDescription.class);
 		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		javaObj = (DeploymentDescription) jaxbUnmarshaller.unmarshal(reader);	
 
	  } catch (JAXBException e) {
              System.err.println(e);
	  } 
         return javaObj;
    }
    
    public boolean loadXMLString(String xmlStr){
    	DeploymentDescription javaObj = fromXMLString(xmlStr);
    	if (javaObj != null){
    		this.listOfDeployActions = javaObj.listOfDeployActions;
    		return true;
    	}
    	return false;
    }
      
    
    
}
