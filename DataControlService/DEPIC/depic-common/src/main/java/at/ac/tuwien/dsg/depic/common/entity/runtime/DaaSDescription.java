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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "DaaSDescription")
@XmlAccessorType(XmlAccessType.FIELD)
public class DaaSDescription {
    
    @XmlElement(name = "DaasName")
    String daasName;
    
    @XmlElement(name = "DBType")
    DBType dBType;
    
    @XmlElement(name = "DatabasePort")
    String databasePort;
    
    @XmlElement(name = "DatabaseName")
    String databaseName;
    
    @XmlElement(name = "DatabaseUser")
    String databaseUser;
    
    @XmlElement(name = "DatabasePassword")
    String databasePassword;
    
    @XmlElement(name = "listOfElasticServices")
    List<ElasticService> listOfElasticServices;

    public DaaSDescription() {
    }

    public DaaSDescription(String daasName, DBType dBType, String databasePort, String databaseName, String databaseUser, String databasePassword, List<ElasticService> listOfElasticServices) {
        this.daasName = daasName;
        this.dBType = dBType;
        this.databasePort = databasePort;
        this.databaseName = databaseName;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.listOfElasticServices = listOfElasticServices;
    }

    public String getDaasName() {
        return daasName;
    }

    public void setDaasName(String daasName) {
        this.daasName = daasName;
    }

    public DBType getdBType() {
        return dBType;
    }

    public void setdBType(DBType dBType) {
        this.dBType = dBType;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(String databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public List<ElasticService> getListOfElasticServices() {
        return listOfElasticServices;
    }

    public void setListOfElasticServices(List<ElasticService> listOfElasticServices) {
        this.listOfElasticServices = listOfElasticServices;
    }

    
}
