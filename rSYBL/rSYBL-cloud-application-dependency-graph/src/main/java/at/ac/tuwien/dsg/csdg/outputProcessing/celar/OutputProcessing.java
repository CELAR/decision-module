/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing.celar;

import at.ac.tuwien.csdg.utils.celar.Configuration;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.outputProcessing.OutputProcessingInterface;

import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class OutputProcessing implements OutputProcessingInterface {
    
    public static String API_URL="http://localhost:8080/celar-orchestrator/deployment/";
	
    public OutputProcessing(){
     	  API_URL=Configuration.getCELARManagerURL();
		
		 
    }
    @Override
    public void saveActionPlan(HashMap<Node,ElasticityCapability> actionPlan) {
     //TODO: save action plan in celar data base through celar manager
        
    }
    
}
