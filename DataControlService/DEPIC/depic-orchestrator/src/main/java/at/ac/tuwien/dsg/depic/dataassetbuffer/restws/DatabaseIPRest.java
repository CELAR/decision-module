/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.restws;

import at.ac.tuwien.dsg.depic.common.utils.Configuration;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import javax.ws.rs.core.MediaType;


/**
 *
 * @author Jun
 */
@Path("/databaseip")
public class DatabaseIPRest {
    
    
    @PUT
    @Path("/import")
    @Consumes(MediaType.APPLICATION_XML)
    public void getElasticDataAsset(String databaseIP) {
        
        Configuration cfg = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        cfg.setConfig("DATABASE.IP", databaseIP);
          
    }
    
    
}
