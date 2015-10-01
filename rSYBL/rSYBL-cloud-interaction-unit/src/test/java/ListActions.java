/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */


import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.utils.Configuration;
import gr.ntua.cslab.orchestrator.beans.ResizingAction;
import gr.ntua.cslab.orchestrator.beans.ResizingActionList;
import gr.ntua.cslab.orchestrator.client.ResizingActionsClient;
import gr.ntua.cslab.orchestrator.client.conf.ClientConfiguration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Georgiana
 */
public class ListActions {
    
    public ListActions() {
    }
    
   private static ResizingActionsClient resizingActionsClient;
    
    @Before
    public void setUp() {
       ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setHost(Configuration.getOrchestratorHost());
        clientConfiguration.setPort(Integer.parseInt(Configuration.getOrchestratorPort()));
        resizingActionsClient = new ResizingActionsClient();
        resizingActionsClient.setConfiguration(clientConfiguration);

    }
    @Test
    public void listActions(){
        try {
            ResizingActionList actionList = resizingActionsClient.listResizingActions();
            for (ResizingAction action:actionList.getResizingActions()){
                System.err.println("Available action: "+action.getName()+" type "+action.getType());
                System.out.println("with parameters...");
                for (String param:action.getApplicablePatameters()){
                    System.out.println(param);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ListActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
