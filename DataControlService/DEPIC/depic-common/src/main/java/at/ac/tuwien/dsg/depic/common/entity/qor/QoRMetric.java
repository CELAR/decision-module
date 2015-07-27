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

package at.ac.tuwien.dsg.depic.common.entity.qor;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "QoRMetric")
@XmlAccessorType(XmlAccessType.FIELD)
public class QoRMetric {
    
    @XmlElement(name = "name", required = true)
    String name;
    
    @XmlElement(name = "unit", required = true)
    String unit;
    
    @XmlElement(name = "listOfRanges", required = true)
    List<Range> listOfRanges;

    public QoRMetric() {
        
    }

    public QoRMetric(String name, String unit, List<Range> listOfRanges) {
        this.name = name;
        this.unit = unit;
        this.listOfRanges = listOfRanges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<Range> getListOfRanges() {
        return listOfRanges;
    }

    public void setListOfRanges(List<Range> listOfRanges) {
        this.listOfRanges = listOfRanges;
    }

    
    
    
    
}
