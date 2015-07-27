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

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Parameter;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "ExternalServiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalServiceRequest {
    
    
    @XmlElement(name = "edaas", required = true)
    String edaas;
    
    @XmlElement(name = "customerID", required = true)
    String customerID;
    
    @XmlElement(name = "dataAssetID", required = true)
    String dataAssetID;
    
    @XmlElement(name = "listOfParameters", required = true)
    List<Parameter> listOfParameters;

    public ExternalServiceRequest() {
    }

    public ExternalServiceRequest(String edaas, String customerID, String dataAssetID, List<Parameter> listOfParameters) {
        this.edaas = edaas;
        this.customerID = customerID;
        this.dataAssetID = dataAssetID;
        this.listOfParameters = listOfParameters;
    }
    
    

    public String getEdaas() {
        return edaas;
    }

    public void setEdaas(String edaas) {
        this.edaas = edaas;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getDataAssetID() {
        return dataAssetID;
    }

    public void setDataAssetID(String dataAssetID) {
        this.dataAssetID = dataAssetID;
    }

    public List<Parameter> getListOfParameters() {
        return listOfParameters;
    }

    public void setListOfParameters(List<Parameter> listOfParameters) {
        this.listOfParameters = listOfParameters;
    }
    
    
    
    
    
}
