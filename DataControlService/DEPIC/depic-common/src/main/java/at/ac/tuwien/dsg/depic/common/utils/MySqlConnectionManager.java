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

import java.sql.*;
import java.util.*;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MySqlConnectionManager {

    private String ip;
    private String port;
    private String database;
    private String userName;
    private String password;
    private String connectionString;

    public MySqlConnectionManager() {
    }

    public MySqlConnectionManager(String ip, String port, String database, String userName, String password) {
        this.ip = ip;
        this.port = port;
        this.database = database;
        this.userName = userName;
        this.password = password;

        connectionString = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useFastDateParsing=false";

    }

    public MySqlConnectionManager(String userName, String password, String connectionString) {
        this.userName = userName;
        this.password = password;
        this.connectionString = connectionString;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public ResultSet ExecuteQuery(String sql) {
        ResultSet result = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionURL = connectionString;
            Connection connection = DriverManager.getConnection(connectionURL, userName, password);
            Statement stmt = connection.createStatement();
            String mysql = sql;

            result = stmt.executeQuery(mysql);
            
         
        } catch (Exception ex) {
            Logger.getLogger(MySqlConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public int ExecuteUpdate(String sql) {
        int result = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionURL = connectionString;
            Connection connection = DriverManager.getConnection(connectionURL, userName, password);
            Statement stmt = connection.createStatement();

            String mysql = sql;

            result = stmt.executeUpdate(mysql);
         
            connection.close();
        } catch (Exception ex) {
            Logger.getLogger(MySqlConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public int ExecuteUpdateBlob(String sql, List<InputStream> listOfInputStreams) {

        int result = 0;
        try {

            Class.forName("com.mysql.jdbc.Driver");
            String connectionURL = connectionString;
            Connection connection = DriverManager.getConnection(connectionURL, userName, password);
                PreparedStatement statement = connection.prepareStatement(sql);
                
                for (int i=0;i<listOfInputStreams.size();i++) {
                    statement.setBlob(i+1, listOfInputStreams.get(i));
                }
                
                result = statement.executeUpdate();
                connection.close();
        } catch (Exception ex) {
            Logger.getLogger(MySqlConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
