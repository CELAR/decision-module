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

package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;

import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import gr.ntua.cslab.orchestrator.beans.ExecutedResizingAction;
import gr.ntua.cslab.orchestrator.beans.Parameter;
import gr.ntua.cslab.orchestrator.beans.Parameters;
import gr.ntua.cslab.orchestrator.beans.ResizingAction;
import gr.ntua.cslab.orchestrator.beans.ResizingActionList;
import gr.ntua.cslab.orchestrator.beans.ResizingActionType;
import gr.ntua.cslab.orchestrator.client.ResizingActionsClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import com.sixsq.slipstream.statemachine.States;
import gr.ntua.cslab.orchestrator.beans.ResourceInfo;
import gr.ntua.cslab.orchestrator.client.ProvidedResourcesClient;
import gr.ntua.cslab.orchestrator.client.conf.ClientConfiguration;
import java.net.ConnectException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.bind.JAXBException;

public class EnforcementPluginCELAR implements EnforcementInterface {

    private MonitoringAPIInterface monitoringAPI;
    private Node cloudService;
    boolean cleanupGoingOn = false;
    boolean cleanupNecessary = true;
    private HashMap<Integer, ResizingAction> actionsAvailable = new HashMap<Integer, ResizingAction>();
    private static ResizingActionsClient resizingActionsClient = new ResizingActionsClient();
    private HashMap<String, ArrayList<String>> disks = new HashMap<String, ArrayList<String>>();
    private ClientConfiguration clientConfiguration;
    private HashMap<String, ResourceInfo> flavors = new HashMap<String, ResourceInfo>();
    private DependencyGraph dependencyGraph;
    public EnforcementPluginCELAR(Node cloudService) {

        this.cloudService = cloudService;
        clientConfiguration = new ClientConfiguration();
        clientConfiguration.setHost(Configuration.getOrchestratorHost());
        clientConfiguration.setPort(Integer.parseInt(Configuration.getOrchestratorPort()));
        resizingActionsClient.setConfiguration(clientConfiguration);
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        refreshElasticityActionsList();
        startDiskManagement();
        findAllAvailableFlavors();
    }

    private void findAllAvailableFlavors() {
        ProvidedResourcesClient client = new ProvidedResourcesClient(clientConfiguration);
        List<ResourceInfo> flavorsList = null;
        try {
            //get the flavors from the client
            flavorsList = client.getFlavors();
            for (ResourceInfo rinfo : flavorsList) {
                flavors.put(rinfo.name, rinfo);
            }
        } catch (IOException ex) {
            RuntimeLogger.logger.error("IOException when trying to find FLAVORS.");
        } catch (JAXBException ex) {
            RuntimeLogger.logger.error("JAXBException when trying to find FLAVORS.");
        }

    }

    public static void main(String[] args) {

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setHost(Configuration.getOrchestratorHost());
        clientConfiguration.setPort(Integer.parseInt(Configuration.getOrchestratorPort()));
        ResizingActionsClient r = new ResizingActionsClient();
        r.setConfiguration(clientConfiguration);
        try {
            for (ResizingAction action : r.listResizingActions().getResizingActions()) {
                System.out.println(action.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
 private void startDiskManagement(){
        TimerTask timerTask = new TimerTask(){

            @Override
            public void run() {
                List<Node> vms = dependencyGraph.getAllVMs();
                for (Node vm : vms){
                    try {
                        double val=monitoringAPI.getMetricValue("diskUsage", vm);
                        if (val>92){
                            attachDisk(vm);
                        }else{
                            if (val<13){
                                attachDisk(vm);
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
            
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 20000, 120000);
    }

    public boolean scaleVertically(Node node, Object[] parameters) {
        boolean ok = true;
        double cores = 0;
        double memory = 0;
        double disk = 0;
        double remainingCost = 0;
        remainingCost = (double) parameters[parameters.length - 1];
        int i = 0;
        String oldFlavor= (String) node.getStaticInformation().get("flavor");
        while (i < parameters.length) {
            switch ((String) parameters[i]) {
                case "cores":
                    cores = Double.parseDouble(flavors.get(oldFlavor).getFieldMap().get("cores"))+(double) parameters[i + 1];
                    break;
                case "memory":
                    memory = Double.parseDouble(flavors.get(oldFlavor).getFieldMap().get("ram"))+(double) parameters[i + 1];
                    break;
                case "disk":
                    disk = Double.parseDouble(flavors.get(oldFlavor).getFieldMap().get("disk"))+(double) parameters[i + 1];
                    break;
            }
        }
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        String flavorId = mapRequirementsNewUUID(node, cores, memory, disk, remainingCost);
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.SCALE_UP && node.getId().equalsIgnoreCase(action.getModuleName())) {
                Parameters params = new Parameters();
                List<Parameter> param = new ArrayList<>();
                Parameter p1 = new Parameter();
                p1.setKey("vm_id");
                p1.setValue(node.getId());
                Parameter p2 = new Parameter();
                p2.setKey("flavor");
                p2.setValue(flavorId);
            }
        }
        return ok;
    }

    public String mapRequirementsNewUUID(Node vm, double cores, double memory, double disk, double remainingCost) {
        String oldFlavor = (String) vm.getStaticInformation().get("flavor");
        ResourceInfo resourceInfoOld = flavors.get(oldFlavor);
        double smallestDistance = 999990;
        String newFlavor = "";
        for (ResourceInfo ri : flavors.values()) {
            if (!ri.name.equalsIgnoreCase(oldFlavor)) {
                double dist = Math.sqrt(Math.pow(disk - Double.parseDouble(ri.getFieldMap().get("disk")), 2)
                        + Math.pow(memory - Double.parseDouble(ri.getFieldMap().get("ram")), 2)
                        + Math.pow(cores - Double.parseDouble(ri.getFieldMap().get("cores")), 2));
                if (dist < smallestDistance) {
                    smallestDistance = dist;
                    newFlavor = ri.name;
                }
            }
        }
        return newFlavor;
    }

    public void refreshElasticityActionsList() {
        actionsAvailable = new HashMap<Integer, ResizingAction>();
        try {
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.INFO, "Connecting to "
                    + resizingActionsClient.getConfiguration().getHost() + ":"
                    + resizingActionsClient.getConfiguration().getPort());
            ResizingActionList actionList = resizingActionsClient.listResizingActions();
            for (ResizingAction action : actionList.getResizingActions()) {
                this.actionsAvailable.put(action.getId(), action);
            }
        } catch (ConnectException ex) {
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, "Encountered error connecting to orchestrator " + ex.getMessage());
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, "Retrying connection to orchestrator");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex1);
            }
            refreshElasticityActionsList();
        } catch (IOException ex) {
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean balance(Node node) {
        boolean ok = false;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.BALANCE && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId());
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {

                    String ip = getEffect(executedResizingAction.getUniqueId(), node.getId());
                    if (!ip.equalsIgnoreCase("")) {
                        RuntimeLogger.logger.debug("Balanced nodes " + node.getId());

                        monitoringAPI.refreshServiceStructure(cloudService);
                        ok = true;
                    } else {
                        RuntimeLogger.logger.error("No IP was remove dafter scaling in node  " + node.getId());
                    }
                }

                break;
            }
        }
        return ok;

    }

    public boolean attachDisk(Node node) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());

        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.ATTACH_DISK && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {

                Parameters parameters = new Parameters();
                List<Parameter> param = new ArrayList<>();
                Parameter p1 = new Parameter();
                p1.setKey("vm_id");
                Double maxUsage = -20.0;
                Node maxUsageNode = null;

                for (Node n : node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE)) {
                    List<String> avMetrics = monitoringAPI.getAvailableMetrics(n);
                    if (avMetrics == null || avMetrics.size() == 0) {
                        Double current = 0.0;
                        try {
                            current = monitoringAPI.getMetricValue("diskUsage", n);
                        } catch (Exception ex) {
                            RuntimeLogger.logger.error("Could not get usage information for " + n.getId() + " and metric diskUsage " + " when trying to attach disk.");
                        }
                        if (current > maxUsage) {
                            maxUsage = current;
                            maxUsageNode = n;
                        }
                    }
                    for (String metric : avMetrics) {
                        if (metric.contains("diskUsage")) {
                            try {
                                Double current = monitoringAPI.getMetricValue(metric, n);
                                if (current > maxUsage) {
                                    maxUsage = current;
                                    maxUsageNode = n;
                                }
                            } catch (Exception ex) {
                                RuntimeLogger.logger.error("Could not get usage information for " + n.getId() + " and metric " + metric + " when trying to attach disk.");
                            }

                        }
                    }
                }
                p1.setValue(maxUsageNode.getId());
                Parameter p2 = new Parameter();
                p2.setKey("disk_size");
                p2.setValue("50");

                parameters.addParameter(p1);
                parameters.addParameter(p2);

                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), parameters);
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {
                    String id = getEffect(executedResizingAction.getUniqueId(), node.getId());
                    if (!id.equalsIgnoreCase("")) {
                        RuntimeLogger.logger.debug("Attaching to node disk with " + node.getId() + " ID " + id);

                        if (!disks.containsKey(node.getId())) {
                            disks.put(node.getId(), new ArrayList<String>());
                        }
                        disks.get(node.getId()).add(id);
                    } else {
                        RuntimeLogger.logger.error("No IP was remove dafter scaling in node  " + node.getId());
                    }
                }

                break;
            }
        }
        return ok;
    }

    public boolean scaleUp(Node node, double violationDegree) {
        boolean ok = true;
        scaleUp(node);
        return ok;
    }

    public boolean scaleUp(Node node) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.SCALE_UP && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {

                Parameters flavors = null;
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), flavors);
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {

                    RuntimeLogger.logger.debug("Scaling up from node disk with " + node.getId());

                }

                break;
            }
        }

        return ok;
    }

    public boolean scaleDown(Node node, double violationDegree) {
        boolean ok = true;
        scaleDown(node);
        return ok;
    }

    public boolean scaleDown(Node node) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.SCALE_DOWN && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {

                Parameters flavors = null;
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), flavors);
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {

                    RuntimeLogger.logger.debug("Scaling down from node disk with " + node.getId());

                }

                break;
            }
        }

        return ok;
    }

    public boolean detachDisk(Node node) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            RuntimeLogger.logger.info("Trying to detach ... "+action.getModuleName());
            if (action.getType() == ResizingActionType.DETTACH_DISK && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                Parameters parameters = new Parameters();
                List<Parameter> param = new ArrayList<>();
                Parameter p1 = new Parameter();
                p1.setKey("vm_id");
                Double minUsage = 300000.0;
                Node minUsageNode = null;
                for (Node n : node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE)) {
                    List<String> avMetrics = monitoringAPI.getAvailableMetrics(n);
                    for (String metric : avMetrics) {
                        if (metric.contains("diskUsage")) {
                            try {
                                Double current = monitoringAPI.getMetricValue(metric, n);
                                if (current < minUsage) {
                                    minUsage = current;
                                    minUsageNode = n;
                                }
                            } catch (Exception ex) {
                                RuntimeLogger.logger.error("Could not get usage information for " + n.getId() + " and metric " + metric + " when trying to dettach disk.");
                            }

                        }
                    }
                }
                p1.setValue(minUsageNode.getId());
                Parameter p2 = new Parameter();
                p2.setKey("disk_id");
                String diskID = disks.get(minUsageNode.getId()).get(0);
                p2.setValue(diskID);

                parameters.addParameter(p1);
                parameters.addParameter(p2);

//                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(),parameters);
//                States status = checkForAction(executedResizingAction.getUniqueId());
//                
//                if (status == States.Ready) {
//
//                        RuntimeLogger.logger.debug("Dettaching from node disk with " + node.getId() + " ID " + diskID);
//                        
//                        disks.get(node.getId()).remove(diskID);
//                }
                break;
            }
        }
        return ok;
    }

    @Override
    public boolean scaleIn(Node node) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.SCALE_IN && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId());
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {
                    //TODO : Assume we have value IP returned
                    String ip = getEffect(executedResizingAction.getUniqueId(), node.getId());
                    if (!ip.equalsIgnoreCase("")) {
                        RuntimeLogger.logger.debug("Removing from node " + node.getId() + " IP " + ip);
                        toBeScaled.removeNode(ip);
                        monitoringAPI.refreshServiceStructure(cloudService);
                    } else {
                        RuntimeLogger.logger.error("No IP was remove dafter scaling in node  " + node.getId());
                    }
                }

                break;
            }
        }
        return ok;
    }

    public boolean scaleIn(Node node, String ip) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.SCALE_IN && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                Parameters pars = new Parameters();
                Parameter par = new Parameter();
                par.setKey("IP");
                par.setValue(ip);
                pars.addParameter(par);
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), pars);
                States status = checkForAction(executedResizingAction.getUniqueId());
                while (status != States.Ready && status != States.Done && status != States.Aborted && status != States.Cancelled) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    status = checkForAction(executedResizingAction.getUniqueId());
                }
                if (status == States.Ready) {
                    //TODO : Assume we have value IP returned
                    //Parameters par = executedResizingAction.getParameters();
                    //Parameter p = (Parameter) par.getParameters().get(0);
                    //  String ip = p.getValue();
                    toBeScaled.removeNode(ip);
                    monitoringAPI.refreshServiceStructure(cloudService);

                }

                break;
            }
        }
        return ok;
    }

    public static ExecutedResizingAction refreshExecutedAction(String uniqueID) {

        try {

            return resizingActionsClient.getActionStatus(uniqueID);

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
            RuntimeLogger.logger.error("Failing to connect to Orchestrator");

        }
        return null;
    }

    public static States checkForAction(String uniqueID) {

        try {

            States retrievedData = resizingActionsClient.getActionStatus(uniqueID).getExecutionStatus();
            return retrievedData;

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
            RuntimeLogger.logger.error("Failing to connect to Orchestrator");

        }
        return null;
    }

    public static String getEffect(String uniqueActionID, String moduleName) {
        String effect = "";
        try {
            HashMap<String, String> effects = resizingActionsClient.getActionEffect(uniqueActionID);
            for (String domainID : effects.keySet()) {
                if (domainID.toLowerCase().contains(moduleName.toLowerCase())) {
                    if (!effects.get(domainID).equalsIgnoreCase("") && effects.get(domainID) != null) {
                        effect = effects.get(domainID);
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
        }

        return effect;
    }

    @Override
    public boolean scaleOut(Node node) {
        return scaleOut(node, (String) node.getStaticInformation().get("DefaultFlavor"));
    }

    public static ExecutedResizingAction executeResizingCommand(Integer actionID, Parameters pars) {
        try {

            ExecutedResizingAction retrievedData = resizingActionsClient.executeResizingAction(actionID, pars);
            return retrievedData;

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
            RuntimeLogger.logger.error("Failing to connect to Orchestrator");

        }
        return null;
    }

    public static ExecutedResizingAction executeResizingCommand(Integer actionID) {
        try {
            ExecutedResizingAction resizingAction = resizingActionsClient.executeResizingAction(actionID, null);
            States status = resizingActionsClient.getActionStatus(resizingAction.getUniqueId()).getExecutionStatus();
            while (status != States.Ready && status != States.Done && status != States.Aborted && status != States.Cancelled) {
                try {
                    Thread.sleep(10000);
                } catch (Exception ex) {
                    Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                }
                status = resizingActionsClient.getActionStatus(resizingAction.getUniqueId()).getExecutionStatus();
            }
            return resizingAction;
        } catch (IOException ex) {
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

//    public static void main(String[] args) {
//        Node cloudService = new Node();
//        cloudService.setId("myCloudServ");
//        cloudService.setNodeType(NodeType.CLOUD_SERVICE);
//        Node servTop = new Node();
//        servTop.setId("servTop");
//        servTop.setNodeType(NodeType.SERVICE_TOPOLOGY);
//        SimpleRelationship relationship = new SimpleRelationship();
//        relationship.setId("rel1");
//        relationship.setSourceElement(cloudService.getId());
//        relationship.setTargetElement(servTop.getId());
//        relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
//        cloudService.addNode(servTop, relationship);
//
//        Node node = new Node();
//
//        node.setId("name1=2");
//        node.setNodeType(NodeType.SERVICE_UNIT);
//        relationship = new SimpleRelationship();
//        relationship.setId("rel");
//        relationship.setSourceElement(servTop.getId());
//        relationship.setTargetElement(node.getId());
//        relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
//        servTop.addNode(node, relationship);
//        DependencyGraph dependencyGraph = new DependencyGraph();
//        dependencyGraph.setCloudService(cloudService);
//        System.out.println(dependencyGraph.graphToString());
//        EnforcementPluginCELAR enforcementPluginCELAR = new EnforcementPluginCELAR(cloudService);
//        enforcementPluginCELAR.refreshElasticityActionsList();
//        for (String action : enforcementPluginCELAR.getElasticityCapabilities()) {
//            System.out.println(action);
//
//        }
//        enforcementPluginCELAR.scaleIn(node);
//    }
    public List<String> getElasticityCapabilities() {
        // TODO Auto-generated method stub
        List<String> avActions = new ArrayList<String>();
        for (ResizingAction action : this.actionsAvailable.values()) {
            avActions.add(action.getName());
        }
        return avActions;
    }

    public void setControlledService(Node controlledService) {
        cloudService = controlledService;

    }

    @Override
    public Node getControlledService() {
        return cloudService;
    }

    public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
        monitoringAPI = monitoring;
    }

    public boolean containsElasticityCapability(Node entity, String capability) {
        boolean found = false;

        for (ResizingAction action : actionsAvailable.values()) {
            if (action.getName().toLowerCase().contains(capability.toLowerCase())) {
                return true;
            }
        }
        return found;
    }

    private boolean scaleOut(Node node, String flavorId) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.SCALE_OUT && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId());
                States status = checkForAction(executedResizingAction.getUniqueId());

                executedResizingAction = refreshExecutedAction(executedResizingAction.getUniqueId());
                if (status == States.Ready) {
                    //TODO : Assume we have value IP returned
                    Parameters par = executedResizingAction.getParameters();
                    // Parameter p = (Parameter) par.getParameters().get(0);

//                     String ip = "10.0.0." + new Random().nextInt(10);
                    String ip = getEffect(executedResizingAction.getUniqueId(), node.getId());
                    if (!ip.equalsIgnoreCase("err") && !ip.equalsIgnoreCase("")) {
                        Node artifact = null;
                        Node container = null;
                        Node newNode = new Node();
                        if (toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {
                            artifact = toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);
                            if (artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER) != null && toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).size() > 0) {

                                container = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).get(0);
                            }
                        }
                        newNode.getStaticInformation().put("IP", ip);
                        newNode.getStaticInformation().put("UUID",ip);//
                        newNode.setId(ip);
                        newNode.setNodeType(NodeType.VIRTUAL_MACHINE);

                        SimpleRelationship rel = new SimpleRelationship();
                        rel.setTargetElement(newNode.getId());
                        rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                        RuntimeLogger.logger.info("Adding to " + node.getId() + " vm with ip " + ip);

                        if (artifact == null && container == null) {
                            rel.setSourceElement(toBeScaled.getId());
                            toBeScaled.addNode(newNode, rel);
                            //node.addNode(newNode, rel);
                        } else {
                            if (container == null) {
                                rel.setSourceElement(artifact.getId());
                                artifact.addNode(newNode, rel);
                            } else {
                                rel.setSourceElement(container.getId());
                                container.addNode(newNode, rel);
                            }

                        }
                    } else {
                        ok = false;
                    }
                    ok = true;
                    RuntimeLogger.logger.debug("Adding to node " + node.getId() + " IP " + ip);

                    RuntimeLogger.logger.info("The controlled service is now " + cloudService.toString());

                    monitoringAPI.refreshServiceStructure(cloudService);
                    return ok;

                }

                break;
            }
        }
        return ok;
    }

    @Override
    public void undeployService(Node serviceID) {
        RuntimeLogger.logger.info("Undeploy service not implemented yet");
    }

    @Override
    public boolean scaleOut(Node toBeScaled, double violationDegree) {
        String flavorID = (String) toBeScaled.getStaticInformation().get("defaultFlavor");
        return scaleOut(toBeScaled, flavorID);
    }

    @Override
    public boolean scaleIn(Node toBeScaled, double violationDegree) {
        return scaleIn(toBeScaled);
    }

    @Override
    public boolean enforceAction(Node serviceID, String actionName) {
         for (Integer id:this.actionsAvailable.keySet()){
             if (actionsAvailable.get(id).getName().equalsIgnoreCase(actionName)){
                 this.enforceAction(serviceID, actionName);
             }
         }
         return true;
    }
    public String chooseNewFlavor(Node serviceID, String requirement){
        String currentFlavor = (String) serviceID.getStaticInformation().get("defaultFlavor");
        ResourceInfo resourceInfo=this.flavors.get(currentFlavor);
        HashMap<String,Double> currentSpecs = new HashMap<String,Double>();
        //resourceInfo.specs
        for (ResourceInfo.ResourceSpec resourceSpec:resourceInfo.specs){
            currentSpecs.put(resourceSpec.property, Double.parseDouble(resourceSpec.value));
        }
        double minResources=0.0;
        String foundFlavor="";
        for (ResourceInfo resInfo:flavors.values()){
            double diff=0.0;
            for (String resource:currentSpecs.keySet()){
               //TODO scale to more or less
                if  (((Double.parseDouble(resInfo.getFieldMap().get(resource))- currentSpecs.get(resource))>0) ){
                    diff+=Double.parseDouble(resInfo.getFieldMap().get(resource))- currentSpecs.get(resource);
                    
                }
                if (diff<minResources){
                    diff=minResources;
                    foundFlavor=resInfo.name;
                }
            }
        }
        return foundFlavor;
    }
    
    public void scaleDiagonally(Node serviceID,String requirement){
        serviceID.getStaticInformation().put("defaultFlavor",chooseNewFlavor(serviceID, requirement));
    }
    

}
