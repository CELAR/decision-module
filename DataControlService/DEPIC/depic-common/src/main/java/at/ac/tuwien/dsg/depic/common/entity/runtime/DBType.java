/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.runtime;

/**
 *
 * @author Jun
 */
public enum DBType {

    MYSQL("MYSQL"), CASSANDRA("CASSANDRA"), MONGODB("MONGODB"), POSTGRESQL("POSTGRESQL"), MONGODB_NEAR_REAL_TIME("MONGODB_NEAR_REAL_TIME"), CASSANDRA_NEAR_REAL_TIME("CASSANDRA_NEAR_REAL_TIME");

    private String dbType;

    private DBType(String dbType) {
        this.dbType = dbType;
    }

    public String getDBType() {
        return dbType;
    }

}
