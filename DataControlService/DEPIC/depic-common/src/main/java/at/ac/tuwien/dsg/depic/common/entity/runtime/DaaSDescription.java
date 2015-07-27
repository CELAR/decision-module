/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */
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
