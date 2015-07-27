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

import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAssetForm;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "QoRModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class QoRModel {
    
    @XmlElement(name = "listOfMetrics", required = true)
    List<QoRMetric> listOfMetrics;
    
    @XmlElement(name = "listOfQElements", required = true)
    List<QElement> listOfQElements;
    
    @XmlElement(name = "dataAssetForm", required = true)
    DataAssetForm dataAssetForm;

    public QoRModel() {
    }

    public QoRModel(List<QoRMetric> listOfMetrics, List<QElement> listOfQElements, DataAssetForm dataAssetForm) {
        this.listOfMetrics = listOfMetrics;
        this.listOfQElements = listOfQElements;
        this.dataAssetForm = dataAssetForm;
    }

    public List<QoRMetric> getListOfMetrics() {
        return listOfMetrics;
    }

    public void setListOfMetrics(List<QoRMetric> listOfMetrics) {
        this.listOfMetrics = listOfMetrics;
    }

    public List<QElement> getListOfQElements() {
        return listOfQElements;
    }

    public void setListOfQElements(List<QElement> listOfQElements) {
        this.listOfQElements = listOfQElements;
    }

    public DataAssetForm getDataAssetForm() {
        return dataAssetForm;
    }

    public void setDataAssetForm(DataAssetForm dataAssetForm) {
        this.dataAssetForm = dataAssetForm;
    }

    
}
