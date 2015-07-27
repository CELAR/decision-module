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


package at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "ParallelGateway")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParallelGateway {
    
    @XmlElement(name = "gatewayID", required = true)
    String gatewayID; 
    
    @XmlElement(name = "incomming", required = true)
    List<String> incomming;
    
    @XmlElement(name = "outgoing", required = true)
    List<String> outgoing;

    public ParallelGateway() {
    }

    public ParallelGateway(String gatewayID, List<String> incomming, List<String> outgoing) {
        this.gatewayID = gatewayID;
        this.incomming = incomming;
        this.outgoing = outgoing;
    }

    public String getGatewayID() {
        return gatewayID;
    }

    public void setGatewayID(String gatewayID) {
        this.gatewayID = gatewayID;
    }

    public List<String> getIncomming() {
        return incomming;
    }

    public void setIncomming(List<String> incomming) {
        this.incomming = incomming;
    }

    public List<String> getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(List<String> outgoing) {
        this.outgoing = outgoing;
    }

    
  
}
