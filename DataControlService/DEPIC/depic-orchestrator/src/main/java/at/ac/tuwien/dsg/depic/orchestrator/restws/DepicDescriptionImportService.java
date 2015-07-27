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
package at.ac.tuwien.dsg.depic.orchestrator.restws;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MonitoringAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.PrimitiveActionMetadata;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlAction;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DaaSDescription;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DepicDescription;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ElasticService;
import at.ac.tuwien.dsg.depic.common.entity.runtime.MonitoringSession;
import at.ac.tuwien.dsg.depic.common.utils.DepicDesciptionImporter;
import at.ac.tuwien.dsg.depic.repository.PrimitiveActionMetadataManager;
import at.ac.tuwien.dsg.depic.orchestrator.execution.DataElasticityMonitor;
import at.ac.tuwien.dsg.depic.orchestrator.registry.ElasticServiceRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/description")
public class DepicDescriptionImportService {
    
    
    @GET
    @Path("/import")
    @Produces(MediaType.TEXT_PLAIN)
    public String startDepicService() {
        
        DepicDesciptionImporter desciptionImporter = new DepicDesciptionImporter();
        DepicDescription depicDescription = desciptionImporter.importDescription(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
      
        PrimitiveActionMetadata primitiveActionMetadata = depicDescription.getPrimitiveActionMetadata();
        DaaSDescription daaSDescription = depicDescription.getDaaSDescription();
        

        PrimitiveActionMetadataManager pamm = new PrimitiveActionMetadataManager(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        
        List<MonitoringAction> listOfMonitoringActions = primitiveActionMetadata.getListOfMonitoringActions();
        for (MonitoringAction monitoringAction : listOfMonitoringActions){
            pamm.storeMonitoringAction(monitoringAction);
        }
        
        List<AdjustmentAction> listOfAdjustmentActions = primitiveActionMetadata.getListOfAdjustmentActions();
        for (AdjustmentAction adjustmentAction : listOfAdjustmentActions){
            pamm.storeAdjustmentAction(adjustmentAction);
        }
        
        List<ResourceControlAction> listOfResourceControlActions = primitiveActionMetadata.getListOfResourceControls();
        for (ResourceControlAction resourceControlAction : listOfResourceControlActions){
            pamm.storeResourceControlAction(resourceControlAction);
        }
        
        List<ElasticService> listOfElasticServices = daaSDescription.getListOfElasticServices();
        for (ElasticService elasticService : listOfElasticServices){             
              ElasticServiceRegistry.addElasticService(elasticService);
        }
        
        
        
        return "";
    }
    
    
   
}
