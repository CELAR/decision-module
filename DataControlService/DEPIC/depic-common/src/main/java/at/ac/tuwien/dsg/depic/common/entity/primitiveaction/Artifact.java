/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.primitiveaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
@XmlRootElement(name = "Artifact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Artifact {
    
    @XmlElement(name = "name", required = false)
    String name;
    
    @XmlElement(name = "description", required = false)
    String description;
    
    @XmlElement(name = "location", required = false)
    String location;
    
    @XmlElement(name = "type", required = false)
    String type;
    
    @XmlElement(name = "restfulAPI", required = false)
    String restfulAPI;
    
    @XmlElement(name = "httpMethod", required = false)
    String httpMethod;

    public Artifact() {
    }

    public Artifact(String name, String description, String location, String type, String restfulAPI) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.type = type;
        this.restfulAPI = restfulAPI;
    }

    public Artifact(String name, String description, String location, String type, String restfulAPI, String httpMethod) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.type = type;
        this.restfulAPI = restfulAPI;
        this.httpMethod = httpMethod;
    }
    
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRestfulAPI() {
        return restfulAPI;
    }

    public void setRestfulAPI(String restfulAPI) {
        this.restfulAPI = restfulAPI;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    
    
}
