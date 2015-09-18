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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "ResourceControlCase")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceControlCase {
    
    @XmlElement(name = "estimatedResult", required = true)
    MetricCondition estimatedResult;
    
    @XmlElement(name = "analyticTask")
    AnalyticTask analyticTask;
    
    @XmlElement(name = "listOfResourceControlStrategy", required = true)
    List<ResourceControlStrategy> listOfResourceControlStrategies;
    
    

    public ResourceControlCase() {
    }

    public ResourceControlCase(MetricCondition estimatedResult, AnalyticTask analyticTask, List<ResourceControlStrategy> listOfResourceControlStrategies) {
        this.estimatedResult = estimatedResult;
        this.analyticTask = analyticTask;
        this.listOfResourceControlStrategies = listOfResourceControlStrategies;
    }

    public MetricCondition getEstimatedResult() {
        return estimatedResult;
    }

    public void setEstimatedResult(MetricCondition estimatedResult) {
        this.estimatedResult = estimatedResult;
    }

    public AnalyticTask getAnalyticTask() {
        return analyticTask;
    }

    public void setAnalyticTask(AnalyticTask analyticTask) {
        this.analyticTask = analyticTask;
    }

    public List<ResourceControlStrategy> getListOfResourceControlStrategies() {
        return listOfResourceControlStrategies;
    }

    public void setListOfResourceControlStrategies(List<ResourceControlStrategy> listOfResourceControlStrategies) {
        this.listOfResourceControlStrategies = listOfResourceControlStrategies;
    }

    

    
    
}
