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
package at.ac.tuwien.dsg.depic.common.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLConnectionManager {
    
    private String ip;
    private String port;
    private String database;
    private String userName;
    private String password;
    private String connectionString;

    public PostgreSQLConnectionManager(String ip, String port, String database, String userName, String password) {
        this.ip = ip;
        this.port = port;
        this.database = database;
        this.userName = userName;
        this.password = password;
        
        connectionString = "jdbc:postgresql://"+ip+"/" +database;
    }
    
    
    public ResultSet ExecuteQuery(String sql) {
    
        
        System.out.println("Starting connection .....");
        ResultSet rs = null;

        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(connectionString, userName, password);
            Statement st = con.createStatement();
            rs = st.executeQuery(sql);

        } catch (SQLException ex) {
            System.err.println(ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PostgreSQLConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }
    
    
}
