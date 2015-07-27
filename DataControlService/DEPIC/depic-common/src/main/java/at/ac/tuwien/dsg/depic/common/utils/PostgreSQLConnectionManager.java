/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.utils;

/**
 *
 * @author Jun
 */

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
