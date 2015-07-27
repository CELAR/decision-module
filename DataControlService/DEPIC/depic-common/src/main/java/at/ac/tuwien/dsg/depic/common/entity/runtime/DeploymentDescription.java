/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author Jun
 */
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
