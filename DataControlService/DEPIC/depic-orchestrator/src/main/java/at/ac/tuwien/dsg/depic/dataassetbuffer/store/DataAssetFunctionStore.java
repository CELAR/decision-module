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
package at.ac.tuwien.dsg.depic.dataassetbuffer.store;

import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.MySqlConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;


public class DataAssetFunctionStore {

    MySqlConnectionManager connectionManager;
    
    public DataAssetFunctionStore() {
        Configuration config = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        String ip = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.IP");
        String port = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.PORT");
        String database = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.DATABASE");
        String username = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.USERNAME");
        String password = config.getConfig("DB.ELASTICITY.PROCESSES.REPO.PASSWORD");
        connectionManager = new MySqlConnectionManager(ip, port, database, username, password);
    }
    
    public String getDataAssetFunction(String dataAssetID){

        String dafXML = "";
        try {
            InputStream inputStream = null;

            String sql = "select * from DataAssetFunction WHERE dataAssetID='" + dataAssetID + "'";
            ResultSet rs = connectionManager.ExecuteQuery(sql);

            try {
                while (rs.next()) {
                    inputStream = rs.getBinaryStream("dataAssetFunction");
                }
                
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataAssetFunctionStore.class.getName()).log(Level.SEVERE, null, ex);
            }

            StringWriter writer = new StringWriter();
            String encoding = StandardCharsets.UTF_8.name();

            IOUtils.copy(inputStream, writer, encoding);
            dafXML = writer.toString();
            Logger.getLogger(DataAssetFunctionStore.class.getName()).log(Level.INFO, null, "DAF XML: " + dafXML);
                

        } catch (IOException ex) {
            Logger.getLogger(DataAssetFunctionStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return dafXML;
    }
    
    
    public int getNoOfPartitions(String dataAssetID){

        int noOfPartition=0;
      

            String sql = "select * from DataAssetFunction WHERE dataAssetID='" + dataAssetID + "'";
            ResultSet rs = connectionManager.ExecuteQuery(sql);

            try {
                while (rs.next()) {
                    noOfPartition = rs.getInt("noOfPartition");
                }
                
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataAssetFunctionStore.class.getName()).log(Level.SEVERE, null, ex);
            }

     
        
        
        return noOfPartition;
    }
    
    
    public DBType gEDaaSTypeFromEDaaSName(String eDaaSName){
        
        String sql = "SELECT * FROM ElasticDaaS WHERE name='" + eDaaSName + "'";
        DBType eDaaSType = null;
       

        ResultSet rs = connectionManager.ExecuteQuery(sql);

        try {
            while (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("type");
                StringWriter writer = new StringWriter();
                String encoding = StandardCharsets.UTF_8.name();
                IOUtils.copy(inputStream, writer, encoding);
                String type = writer.toString();          
                eDaaSType = DBType.valueOf(type);
            }

            rs.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }

        return eDaaSType;

        
    }
    
    
    public DBType gEDaaSTypeFromDataAssetID(String dataAssetID){
        
       String sql = "SELECT * FROM ElasticDaaS, DataAssetFunction "
                    + "WHERE ElasticDaaS.name = DataAssetFunction.edaas "
                    + "AND DataAssetFunction.dataAssetID='"+dataAssetID+"'";
        DBType eDaaSType = null;
       

        ResultSet rs = connectionManager.ExecuteQuery(sql);

        System.out.println("Get DB Type ");
        try {
            while (rs.next()) {

                String type = rs.getString("type");
                 
                eDaaSType = DBType.valueOf(type);
            }

            rs.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        
      

        return eDaaSType;
    }
    
    
    
}
