/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing.celar;

import at.ac.tuwien.csdg.utils.celar.Configuration;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.csdg.outputProcessing.OutputProcessingInterface;

import java.util.HashMap;

/**
 *
 * @author Georgiana
 */
public class OutputProcessing implements OutputProcessingInterface {

    public static String API_URL = "http://localhost:8080/celar-orchestrator/deployment/";

    static {
//        ensure that the cloud DeploymentDescription is injected in the generated jar
    }

    public OutputProcessing() {
        API_URL = Configuration.getCELARManagerURL();
        System.out.println("DeploymentDescription loaded as " + DeploymentDescription.class.getName());
    }

    @Override
    public void saveActionPlan(HashMap<Node, ElasticityCapability> actionPlan) {
     //TODO: save action plan in celar data base through celar manager

    }

}
