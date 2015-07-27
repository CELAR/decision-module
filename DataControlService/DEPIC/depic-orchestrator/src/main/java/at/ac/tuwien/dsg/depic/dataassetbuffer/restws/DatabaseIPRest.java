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
package at.ac.tuwien.dsg.depic.dataassetbuffer.restws;

import at.ac.tuwien.dsg.depic.common.utils.Configuration;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import javax.ws.rs.core.MediaType;



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
