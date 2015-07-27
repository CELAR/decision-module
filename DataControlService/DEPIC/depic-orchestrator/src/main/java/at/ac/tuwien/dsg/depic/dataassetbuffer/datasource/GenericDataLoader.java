/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.datasource;

import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.eda.dataasset.DataAsset;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;

/**
 *
 * @author Jun
 */
public class GenericDataLoader implements DataLoader{

    private DBType eDaaSType;
    
    public GenericDataLoader() {
    }

    public GenericDataLoader(DBType eDaaSType) {
        this.eDaaSType = eDaaSType;
    }



    @Override
    public String getDataPartitionRepo(DataPartitionRequest request) {
        String xml="";
        
        if (eDaaSType.equals(DBType.MYSQL)) {
            MySQLDataLoader msqldl = new MySQLDataLoader();
            xml = msqldl.getDataPartitionRepo(request);
        } else if (eDaaSType.equals(DBType.POSTGRESQL)) {
            PostgreSQLDataLoader psqldl = new PostgreSQLDataLoader();
            xml = psqldl.getDataPartitionRepo(request);
             
        }
        
        return xml;
    }

    public void saveDataPartitionRepo(DataAsset dataAsset) {
        
        if (eDaaSType.equals(DBType.MYSQL)) {
            MySQLDataLoader msqldl = new MySQLDataLoader();
            msqldl.saveDataPartitionRepo(dataAsset);
        } else if (eDaaSType.equals(DBType.POSTGRESQL)) {
            PostgreSQLDataLoader psqldl = new PostgreSQLDataLoader();
            psqldl.saveDataPartitionRepo(dataAsset);
        } 
    }

    @Override
    public String getNoOfParitionRepo(DataPartitionRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
}
