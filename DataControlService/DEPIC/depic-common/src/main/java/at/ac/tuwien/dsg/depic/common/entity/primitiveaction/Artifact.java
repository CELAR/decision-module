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


package at.ac.tuwien.dsg.depic.common.entity.primitiveaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
