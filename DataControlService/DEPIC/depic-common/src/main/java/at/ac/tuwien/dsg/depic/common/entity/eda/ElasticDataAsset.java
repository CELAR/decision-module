/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.common.entity.eda;


import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DataElasticityManagementProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DataElasticityManagementProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticStateSet;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */

@XmlRootElement(name = "ElasticDataAsset")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElasticDataAsset {

    @XmlElement(name = "dataAssetID", required = true)
    String dataAssetID;
  
    @XmlElement(name = "elasticProcess", required = true)
    DataElasticityManagementProcess elasticProcess;
    
    @XmlElement(name = "listOfFinalElasticState", required = true)
    List<ElasticState> listOfFinalElasticState;
    
    public ElasticDataAsset() {
    }

    public ElasticDataAsset(String dataAssetID, DataElasticityManagementProcess elasticProcess, List<ElasticState> listOfFinalElasticState) {
        this.dataAssetID = dataAssetID;
        this.elasticProcess = elasticProcess;
        this.listOfFinalElasticState = listOfFinalElasticState;
    }

    public String getDataAssetID() {
        return dataAssetID;
    }

    public void setDataAssetID(String dataAssetID) {
        this.dataAssetID = dataAssetID;
    }

    public DataElasticityManagementProcess getElasticProcess() {
        return elasticProcess;
    }

    public void setElasticProcess(DataElasticityManagementProcess elasticProcess) {
        this.elasticProcess = elasticProcess;
    }

    public List<ElasticState> getListOfFinalElasticState() {
        return listOfFinalElasticState;
    }

    public void setListOfFinalElasticState(List<ElasticState> listOfFinalElasticState) {
        this.listOfFinalElasticState = listOfFinalElasticState;
    }

    

    
    
}
