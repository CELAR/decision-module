/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.common.artifact;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Artifacts")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Artifacts")
public class Artifacts {
	@XmlElement(name="Artifact")
	List<Artifact> arts = new ArrayList<>();
	
	public void add(Artifact art){
		this.arts.add(art);
	}
	
	
	public void exportToXML(String fileName){
		System.out.println("Writing Tosca to file: "+fileName);
		try {
			File file = new File(fileName);
			JAXBContext jaxbContext = JAXBContext.newInstance(Artifacts.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			jaxbMarshaller.marshal(this, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Artifact searchArtifact(String name){
		for (Artifact art : this.arts) {
			if (art.name.equals(name)){
				return art;
			}
		}
		return null;		
	}
	
	public void importFromXML(String fileName){
		System.out.println("Import the Artifact repo from file: "+fileName);
		try {
			JAXBContext context = JAXBContext.newInstance(Artifacts.class);			
			Unmarshaller um = context.createUnmarshaller();
			Artifacts parsedArts = (Artifacts)um.unmarshal(new FileReader(fileName));
			this.arts = parsedArts.arts;			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
