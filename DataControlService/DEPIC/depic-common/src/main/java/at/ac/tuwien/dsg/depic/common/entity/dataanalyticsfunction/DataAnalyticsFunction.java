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

package at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction;

import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "DataAssetFunction")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataAnalyticsFunction {
    
    @XmlElement(name = "name", required = true)
    String name;
    
    @XmlElement(name = "dataAssetForm", required = true)
    DataAssetForm dataAssetForm;
    
    @XmlElement(name = "dbType", required = true)
    DBType dbType;
    
    @XmlElement(name = "daw", required = true)
    String daw;

    public DataAnalyticsFunction() {
    }

    public DataAnalyticsFunction(String name, DataAssetForm dataAssetForm, DBType dbType, String daw) {
        this.name = name;
        this.dataAssetForm = dataAssetForm;
        this.dbType = dbType;
        this.daw = daw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataAssetForm getDataAssetForm() {
        return dataAssetForm;
    }

    public void setDataAssetForm(DataAssetForm dataAssetForm) {
        this.dataAssetForm = dataAssetForm;
    }

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(DBType dbType) {
        this.dbType = dbType;
    }

    public String getDaw() {
        return daw;
    }

    public void setDaw(String daw) {
        this.daw = daw;
    }

    

    
    
  
}
