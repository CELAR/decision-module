/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184. * This work was partially supported by the European Commission in terms
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
/**
 * Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
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
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapabilityInformation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
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
import gr.ntua.cslab.orchestrator.beans.ResourceInfo.ResourceSpec;
import gr.ntua.cslab.orchestrator.client.ProvidedResourcesClient;
import gr.ntua.cslab.orchestrator.client.conf.ClientConfiguration;
import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.bind.JAXBException;

public class EnforcementPluginCELAR implements EnforcementInterface {

    private MonitoringAPIInterface monitoringAPI;
    private Node cloudService;
    private double MAKES_SENSE_TO_SCALE_VERTICALLY = 1;
    boolean cleanupGoingOn = false;
    boolean cleanupNecessary = true;
    private HashMap<Integer, ResizingAction> actionsAvailable = new HashMap<Integer, ResizingAction>();
    private static ResizingActionsClient resizingActionsClient = new ResizingActionsClient();
    private HashMap<String, ArrayList<String>> disks = new HashMap<String, ArrayList<String>>();
    private ClientConfiguration clientConfiguration;
    private HashMap<String, ResourceInfo> flavors = new HashMap<String, ResourceInfo>();
    private DependencyGraph dependencyGraph;
    private boolean executingAction = false;
    private HashMap<String, Double> maxResourceValues = new HashMap<String, Double>();

    public EnforcementPluginCELAR(Node cloudService) {

        this.cloudService = cloudService;
        clientConfiguration = new ClientConfiguration();
        clientConfiguration.setHost(Configuration.getOrchestratorHost());
        clientConfiguration.setPort(Integer.parseInt(Configuration.getOrchestratorPort()));
        resizingActionsClient.setConfiguration(clientConfiguration);
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
         findAllAvailableFlavors();
        initializeFlavors();
        refreshElasticityActionsList();
        startDiskManagement();
       
    }
    private void initializeFlavors(){
        List<Node> components = dependencyGraph.getAllServiceUnits();
        for (Node n:components){
            String flavor = "";
            for (Node vm:n.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE)){
                if (vm.getStaticInformation().get("flavor")!=null && !vm.getStaticInformation().get("flavor").equals("")){
                    flavor = getFlavorID((String) vm.getStaticInformation().get("flavor"));
                    vm.getStaticInformation().put("flavor", flavor);
                }
            }
            if (!flavor.equalsIgnoreCase("") && (n.getStaticInformation().get("DefaultFlavor")==null || n.getStaticInformation().get("DefaultFlavor")=="")){
                n.getStaticInformation().put("DefaultFlavor", flavor);
            }else{
                if (!flavor.equalsIgnoreCase("") && (n.getStaticInformation().get("DefaultFlavor")==null || n.getStaticInformation().get("DefaultFlavor")=="") ){
                    n.getStaticInformation().put("DefaultFlavor", flavors.keySet().iterator().next());
                }
            }
        }
    }
    private void findAllAvailableFlavors() {
        ProvidedResourcesClient client = new ProvidedResourcesClient(clientConfiguration);
        List<ResourceInfo> flavorsList = null;
        try {
            //get the flavors from the client
            flavorsList = client.getFlavors();
            for (ResourceInfo rinfo : flavorsList) {
                flavors.put(rinfo.name, rinfo);
                for (ResourceSpec resourceSpec : rinfo.specs) {
                    if ((maxResourceValues.containsKey(resourceSpec.property) && maxResourceValues.get(resourceSpec.property) < Double.parseDouble(resourceSpec.value))
                            || !maxResourceValues.containsKey(resourceSpec.property)) {
                        maxResourceValues.put(resourceSpec.property, Double.parseDouble(resourceSpec.value));
                    }
                }
            }
        } catch (IOException ex) {
            RuntimeLogger.logger.error("IOException when trying to find FLAVORS.");
        } catch (JAXBException ex) {
            RuntimeLogger.logger.error("JAXBException when trying to find FLAVORS.");
        }
        List<Node> nodes = dependencyGraph.getAllVMs();
        for (Node n : nodes) {
            if (n.getStaticInformation().get("flavor") == null || ((String) n.getStaticInformation().get("flavor")).equalsIgnoreCase("")) {
                n.getStaticInformation().put("flavor", flavors.keySet().iterator().next());
            }
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

    private void startDiskManagement() {
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (!executingAction) {
                    List<Node> vms = dependencyGraph.getAllVMs();
                    for (Node vm : vms) {
                        try {
                            double val = monitoringAPI.getMetricValue("diskUsage", vm);
                            if (val > 92) {
                                attachDisk(vm);
                            } else {
                                if (val < 13) {
                                    attachDisk(vm);
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }

        };
        if (Configuration.resourceLevelControlEnabled()) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(timerTask, 20000, 120000);
        }
    }

    public boolean scaleVertically(Node node, Object[] parameters) {
        executingAction = true;
        boolean ok = true;
        double cores = 0;
        double memory = 0;
        double disk = 0;
        double remainingCost = 0;
        remainingCost = (double) parameters[parameters.length - 1];
        int i = 0;
        String oldFlavor = (String) node.getStaticInformation().get("flavor");
        while (i < parameters.length) {
            switch ((String) parameters[i]) {
                case "cores":
                    cores = Double.parseDouble(flavors.get(oldFlavor).getFieldMap().get("cores")) + (double) parameters[i + 1];
                    break;
                case "memory":
                    memory = Double.parseDouble(flavors.get(oldFlavor).getFieldMap().get("ram")) + (double) parameters[i + 1];
                    break;
                case "disk":
                    disk = Double.parseDouble(flavors.get(oldFlavor).getFieldMap().get("disk")) + (double) parameters[i + 1];
                    break;
            }
        }
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        String flavorId = mapRequirementsNewUUID(node, cores, memory, disk, remainingCost);
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();

            if (action.getType() == ResizingActionType.SCALE_UP && node.getId().equalsIgnoreCase(action.getModuleName())) {
                Parameters params = new Parameters();
                List<Parameter> param = new ArrayList<>();
                Parameter p1 = new Parameter();
                p1.setKey("vm_ip");
                p1.setValue(node.getId());
                Parameter p2 = new Parameter();
                p2.setKey("flavor");
                p2.setValue(flavorId);
            }
        }
        executingAction = false;
        return ok;
    }

    public String mapRequirementsNewUUID(Node vm, double cores, double memory, double disk, double remainingCost) {
        String oldFlavor = (String) vm.getStaticInformation().get("flavor");
        ResourceInfo resourceInfoOld = flavors.get(oldFlavor);
        double smallestDistance = 999990;
        String newFlavor = "";
        for (ResourceInfo ri : flavors.values()) {
            if (!ri.name.equalsIgnoreCase(oldFlavor)) {
                double cdisk =0.0;
                double cram=0.0;
                double ccores=0.0;
                 List<ResourceSpec> specs = ri.specs;
                        for (ResourceSpec rs : specs) {
                            if (rs.property.equalsIgnoreCase("disk")){
                                cdisk=Double.parseDouble(rs.value);
                            }
                            if (rs.property.equalsIgnoreCase("ram")){
                                cram=Double.parseDouble(rs.value);
                            }
                            if (rs.property.equalsIgnoreCase("cores")){
                                ccores = Double.parseDouble(rs.value);
                            }
                        }
                double dist = Math.sqrt(Math.pow(disk - cdisk, 2)
                        + Math.pow(memory - cram, 2)
                        + Math.pow(cores - ccores, 2));
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
                ElasticityCapabilityInformation capability = new ElasticityCapabilityInformation();
                capability.setName(action.getName());
                capability.setPrimitiveOperations(action.getName());
                dependencyGraph.getNodeWithID(action.getModuleName()).addElasticityCapability(capability);
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
        executingAction = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.BALANCE && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId());
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {

                    String ip;
                    try {
                        ip = resizingActionsClient.getActionStatus(executedResizingAction.getUniqueId()).getIpAddressesAdded().get(0);
                        if (!ip.equalsIgnoreCase("")) {
                            RuntimeLogger.logger.debug("Balanced nodes " + node.getId());

                            monitoringAPI.refreshServiceStructure(cloudService);
                            ok = true;
                        } else {
                            RuntimeLogger.logger.error("No IP was remove dafter scaling in node  " + node.getId());
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                break;
            }
        }
        executingAction = false;
        return ok;

    }

    public boolean attachDisk(Node node) {
        boolean ok = true;
        executingAction = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());

        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.ATTACH_DISK && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {

                Parameters parameters = new Parameters();
                List<Parameter> param = new ArrayList<>();
                Parameter p1 = new Parameter();
                p1.setKey("vm_ip");
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
                if (maxUsageNode == null) {
                    String id = "";
                    for (Node n : node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE)) {
                        if (!disks.containsKey(n.getId()) || id.equalsIgnoreCase("")) {
                            id = n.getId();
                        }
                    }
                    //  maxUsageNode=node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size()-1);
                    maxUsageNode = dependencyGraph.getNodeWithID(id);

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
                    String id;
                    try {
                        id = resizingActionsClient.getActionStatus(executedResizingAction.getUniqueId()).getDiskIdAttached();
                        if (id != null && !id.equalsIgnoreCase("")) {
                            RuntimeLogger.logger.debug("Attaching to node disk for VM " + node.getId() + " ID " + id);

                            if (!disks.containsKey(node.getId())) {
                                disks.put(maxUsageNode.getId(), new ArrayList<String>());
                            }
                            disks.get(maxUsageNode.getId()).add(id);
                        } else {
                            RuntimeLogger.logger.error("No IP was remove dafter scaling in node  " + node.getId());
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                break;
            }
        }
        executingAction = false;
        return ok;
    }

    public boolean scaleVerticallyUp(Node node, double violationDegree) {
        boolean ok = true;
        scaleVerticallyUp(node);
        return ok;
    }

    public boolean scaleVerticallyUp(Node node) {
        boolean ok = true;
        executingAction = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.SCALE_VERTICALLY_UP && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {

                Parameters flavor = new Parameters();

                Node virtualMachine = this.pickVMToScaleUp(toBeScaled);
                if (virtualMachine != null) {
                    String newFlavor = this.chooseNewFlavorUp(virtualMachine, null);
                    if (newFlavor != null && !newFlavor.equalsIgnoreCase("")) {
                        ResourceInfo myFlavor = this.flavors.get(newFlavor);
                        List<ResourceSpec> specs = myFlavor.specs;
                        for (ResourceSpec rs : specs) {
                            if (!rs.property.equalsIgnoreCase("disk")) {
                                Parameter parameter = new Parameter();

                                parameter.setKey(rs.property);
                                parameter.setValue(rs.value);
                                flavor.addParameter(parameter);
                            }
                        }
                        Parameter vm_ip = new Parameter();
                        vm_ip.setKey("vm_ip");
                        vm_ip.setValue(virtualMachine.getId());
                        flavor.addParameter(vm_ip);
                        ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), flavor);
                        States status = checkForAction(executedResizingAction.getUniqueId());

                        if (status == States.Ready) {
                            System.out.println("The VM was scaled up from flavor " + virtualMachine.getStaticInformation().get("flavor") + " to " + myFlavor.name);
                            RuntimeLogger.logger.debug("Scaling up from node disk with " + node.getId());

                        }
                    }
                }
                break;
            }
        }
        executingAction = false;
        return ok;
    }

    public boolean scaleVerticallyDown(Node node, double violationDegree) {
        boolean ok = true;
        scaleVerticallyDown(node);
        return ok;
    }

    public boolean scaleVerticallyDown(Node node) {
        boolean ok = true;
        executingAction = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            //  System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.SCALE_VERTICALLY_DOWN && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {

                Parameters flavor = new Parameters();
                Node virtualMachine = this.pickVMToScaleDown(toBeScaled);
                if (virtualMachine != null) {
                    String newFlavor = this.chooseNewFlavorDown(virtualMachine, null);
                    if (newFlavor != null && !newFlavor.equalsIgnoreCase("")) {
                        ResourceInfo myFlavor = this.flavors.get(newFlavor);
                        List<ResourceSpec> specs = myFlavor.specs;
                        for (ResourceSpec rs : specs) {
                            if (!rs.property.equalsIgnoreCase("disk")) {
                                Parameter parameter = new Parameter();
                                parameter.setKey(rs.property);
                                parameter.setValue(rs.value);
                                flavor.addParameter(parameter);
                            }
                        }
                        Parameter vm_ip = new Parameter();
                        vm_ip.setKey("vm_ip");
                        vm_ip.setValue(virtualMachine.getId());
                        flavor.addParameter(vm_ip);
                        ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), flavor);
                        States status = checkForAction(executedResizingAction.getUniqueId());

                        if (status == States.Ready) {
                            System.out.println("The VM was scaled down from flavor " + virtualMachine.getStaticInformation().get("flavor") + " to " + myFlavor.name);

                            RuntimeLogger.logger.debug("Scaling down from node disk with " + node.getId());

                        }
                    }
                }
                break;
            }
        }
        executingAction = false;
        return ok;
    }

    public boolean detachDisk(Node node) {
        boolean ok = true;
        executingAction = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            RuntimeLogger.logger.info("Trying to detach ... " + action.getModuleName());
            if (action.getType() == ResizingActionType.DETTACH_DISK && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                Parameters parameters = new Parameters();
                List<Parameter> param = new ArrayList<>();
                Parameter p1 = new Parameter();
                p1.setKey("vm_ip");
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
                if (minUsageNode == null) {
                    if (disks.isEmpty()) {
                        executingAction = false;
                        return false;
                    } else {
                        minUsageNode = dependencyGraph.getNodeWithID(disks.keySet().iterator().next());
                    }

                }
                p1.setValue(minUsageNode.getId());
                Parameter p2 = new Parameter();
                p2.setKey("disk_id");
                String diskID = disks.get(minUsageNode.getId()).get(0);
                p2.setValue(diskID);

                parameters.addParameter(p1);
                parameters.addParameter(p2);

                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), parameters);
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {

                    RuntimeLogger.logger.debug("Dettaching from node disk with " + node.getId() + " ID " + diskID);

                    disks.get(minUsageNode.getId()).remove(diskID);
                }
                break;
            }
        }
        executingAction = false;
        return ok;
    }

    @Override
    public boolean scaleIn(Node node) {
        boolean ok = true;
        executingAction = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            //System.out.println(action.getModuleName());
            if (action.getType() == ResizingActionType.SCALE_IN && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                List<Node> nodes = node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE);
                Node found = null;
                for (Node n:nodes){
                    try {
                        List<String> metrics= monitoringAPI.getAvailableMetrics(n);
                        
                        if (metrics.contains("busyness") && monitoringAPI.getMetricValue("busyness", n)==0){
                            found=n;
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                        RuntimeLogger.logger.error("Error while trying to get the busy metric");
                    }
                }
                if (found!=null){
                  Parameters pars = new Parameters();
                Parameter par = new Parameter();
                par.setKey("vm_ip");
                par.setValue(found.getId());
                pars.addParameter(par);
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(),pars);
                States status = checkForAction(executedResizingAction.getUniqueId());

                if (status == States.Ready) {
                    //TODO : Assume we have value IP returned
                    String ip;
                    try {
                        ip = resizingActionsClient.getActionStatus(executedResizingAction.getUniqueId()).getIpAddressesRemoved().get(0);
                        if (!ip.equalsIgnoreCase("")) {
                            RuntimeLogger.logger.debug("Removing from node " + node.getId() + " IP " + ip);
                            toBeScaled.removeNode(ip);
                            monitoringAPI.refreshServiceStructure(cloudService);
                        } else {
                            RuntimeLogger.logger.error("No IP was remove dafter scaling in node  " + node.getId());
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                }
                break;
            }
        }
        executingAction = false;
        return ok;
    }

    public Node pickVMToScaleDown(Node toscale) {
        String metricCPU = "cpuUsedPercent";
        String metricMem = "memUsedPercent";
        double smallestCPU = 100;
        Node smallestCPUVM = null;
        double smallestMem = 100;
        Node smallestMemVM = null;
        for (Node vm : toscale.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE)) {
            double currentCPU;
            try {
                currentCPU = monitoringAPI.getMetricValue(metricCPU, vm);

                double currentMem = monitoringAPI.getMetricValue(metricMem, vm);
                if (currentCPU < smallestCPU) {
                    smallestCPU = currentCPU;
                    smallestCPUVM = vm;
                }
                if (currentMem < smallestMem) {
                    smallestMem = currentMem;
                    smallestMemVM = vm;
                }
            } catch (Exception ex) {
                Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (smallestCPUVM == null || smallestMemVM == null) {
            if (smallestCPUVM != null) {
                return smallestCPUVM;
            } else {
                if (smallestMemVM != null) {
                    return smallestMemVM;
                }
            }
        } else {
            if (smallestCPU < smallestMem) {
                return smallestCPUVM;
            } else {
                return smallestMemVM;
            }
        }
        return null;
    }

    public Node pickVMToScaleUp(Node toscale) {
        String metricCPU = "cpuUsedPercent";
        String metricMem = "memUsedPercent";
        double biggestCPU = 0;
        Node biggestCPUVM = null;
        double biggestMem = 0;
        Node biggestMemVM = null;
        for (Node vm : toscale.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE)) {
            double currentCPU;
            try {
                currentCPU = monitoringAPI.getMetricValue(metricCPU, vm);

                double currentMem = monitoringAPI.getMetricValue(metricMem, vm);
                if (currentCPU > biggestCPU) {
                    biggestCPU = currentCPU;
                    biggestCPUVM = vm;
                }
                if (currentMem > biggestMem) {
                    biggestMem = currentMem;
                    biggestMemVM = vm;
                }
            } catch (Exception ex) {
                Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (biggestCPUVM == null || biggestMemVM == null) {
            if (biggestCPUVM != null) {
                return biggestCPUVM;
            } else {
                if (biggestMemVM != null) {
                    return biggestMemVM;
                }
            }
        } else {
            if (biggestCPU < biggestMem) {
                return biggestCPUVM;
            } else {
                return biggestMemVM;
            }
        }
        return null;
    }

    public boolean scaleIn(Node node, String ip) {
        boolean ok = true;
        executingAction = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.SCALE_IN && toBeScaled.getId().equalsIgnoreCase(action.getModuleName())) {
                Parameters pars = new Parameters();
                Parameter par = new Parameter();
                par.setKey("vm_ip");
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
        executingAction = false;
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

    @Override
    public boolean scaleOut(Node node) {
        return scaleOut(node, (String) node.getStaticInformation().get("DefaultFlavor"));
    }

    public static ExecutedResizingAction executeResizingCommand(Integer actionID, Parameters pars) {
        try {

            ExecutedResizingAction retrievedData = resizingActionsClient.executeResizingAction(actionID, pars);
            States status = resizingActionsClient.getActionStatus(retrievedData.getUniqueId()).getExecutionStatus();
            while (status != States.Ready && status != States.Done && status != States.Aborted && status != States.Cancelled) {
                try {
                    Thread.sleep(10000);
                } catch (Exception ex) {
                    Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                }
                status = resizingActionsClient.getActionStatus(retrievedData.getUniqueId()).getExecutionStatus();
            }
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
    public String getFlavorID(String description){
        String[] descriptionSplit = description.split(" ");
        for (String flavor:this.flavors.keySet()){
            List<ResourceSpec> specs = flavors.get(flavor).specs;
            boolean ok = true;
                        for (ResourceSpec rs : specs) {
                            if (rs.property.equalsIgnoreCase("cores") && !descriptionSplit[0].split(":")[1].equalsIgnoreCase(rs.value)) {
                                ok=false;
                            }
                            if (rs.property.equalsIgnoreCase("ram") && !descriptionSplit[1].split(":")[1].equalsIgnoreCase(rs.value)) {
                                ok=false;
                            }
                            if (rs.property.equalsIgnoreCase("disk") && !descriptionSplit[2].split(":")[1].equalsIgnoreCase(rs.value)) {
                                ok=false;
                            }
                            
                        }
                        
            if (ok) return flavor;
        }
        return "";
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
                ExecutedResizingAction executedResizingAction = null;
                if (!Configuration.resourceLevelControlEnabled()) {
                    Parameters parameters = new Parameters();
                    ResourceInfo defaultFlavor = null;
                    if (flavorId.equalsIgnoreCase("")) {
                        if (node.getStaticInformation().get("DefaultFlavor") != null && !((String) node.getStaticInformation().get("DefaultFlavor")).equalsIgnoreCase("")) {
                            defaultFlavor = flavors.get((String) node.getStaticInformation().get("DefaultFlavor"));
                        } else {
                            defaultFlavor = flavors.values().iterator().next();
                        }
                    } else {
                        defaultFlavor = flavors.get(flavorId);
                    }
                    List<ResourceSpec> specs = defaultFlavor.specs;
                    for (ResourceSpec rs : specs) {
                        if (!rs.property.equalsIgnoreCase("disk")) {
                            Parameter parameter = new Parameter();
                            parameter.setKey(rs.property);
                            parameter.setValue(rs.value);
                            parameters.addParameter(parameter);
                        }
                    }

                    executedResizingAction = executeResizingCommand(action.getId(), parameters);

                } else {
                    executedResizingAction = executeResizingCommand(action.getId());
                }
                
                States status=null;
                        try{status = checkForAction(executedResizingAction.getUniqueId());

                executedResizingAction = refreshExecutedAction(executedResizingAction.getUniqueId());
                        }catch(Exception e){
                            System.err.println("Error when scaling: "+e.getMessage());
                            RuntimeLogger.logger.error(e.getMessage());
                        }
                if (status == States.Ready) {
                    //TODO : Assume we have value IP returned
                    Parameters par = executedResizingAction.getParameters();
                    // Parameter p = (Parameter) par.getParameters().get(0);

//                     String ip = "10.0.0." + new Random().nextInt(10);
                    String ip = executedResizingAction.getIpAddressesAdded().get(0);
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
                        newNode.getStaticInformation().put("UUID", ip);//
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
        String flavorID = (String) toBeScaled.getStaticInformation().get("DefaultFlavor");
        return scaleOut(toBeScaled, flavorID);
    }

    @Override
    public boolean scaleIn(Node toBeScaled, double violationDegree) {
        return scaleIn(toBeScaled);
    }

    @Override
    public boolean enforceAction(Node serviceID, String actionName) {
        for (Integer id : this.actionsAvailable.keySet()) {
            if (actionsAvailable.get(id).getName().equalsIgnoreCase(actionName)) {
                this.enforceAction(serviceID, actionName);
            }
        }
        return true;
    }

    public String chooseNewFlavorUp(Node serviceID, Strategy strategy) {
        String currentFlavor = "";
        if (serviceID.getNodeType() == NodeType.VIRTUAL_MACHINE) {
            currentFlavor = (String) serviceID.getStaticInformation().get("flavor");
        } else {
            currentFlavor = (String) serviceID.getStaticInformation().get("DefaultFlavor");
        }
        ResourceInfo resourceInfo = this.flavors.get(currentFlavor);
        HashMap<String, Double> currentSpecs = new HashMap<String, Double>();
        for (ResourceInfo.ResourceSpec resourceSpec : resourceInfo.specs) {
            currentSpecs.put(resourceSpec.property, Double.parseDouble(resourceSpec.value));
        }
        double minResources = 1000000.0;
        String foundFlavor = "";
        for (ResourceInfo resInfo : flavors.values()) {
            double diff = 0.0;
            boolean ok = true;
            for (String resource : currentSpecs.keySet()) {
                //TODO scale to more or less
                HashMap<String, Double> diffSpecs = new HashMap<String, Double>();
                for (ResourceInfo.ResourceSpec resourceSpec : resInfo.specs) {
                    diffSpecs.put(resourceSpec.property, Double.parseDouble(resourceSpec.value));
                }
                if (diffSpecs.get(resource) / maxResourceValues.get(resource) - currentSpecs.get(resource) / maxResourceValues.get(resource)<0){
                    ok=false;
                }
                diff += diffSpecs.get(resource) / maxResourceValues.get(resource) - currentSpecs.get(resource) / maxResourceValues.get(resource);

            }
            if (diff < minResources && diff != 0 && diff > 0 && diff > MAKES_SENSE_TO_SCALE_VERTICALLY) {
                minResources = diff;
                foundFlavor = resInfo.name;
            }
        }
        return foundFlavor;
    }

    public String chooseNewFlavorDown(Node serviceID, Strategy strategy) {
        String currentFlavor = "";
        if (serviceID.getNodeType() == NodeType.VIRTUAL_MACHINE) {
            currentFlavor = (String) serviceID.getStaticInformation().get("flavor");
        } else {
            currentFlavor = (String) serviceID.getStaticInformation().get("DefaultFlavor");
        }
        ResourceInfo resourceInfo = this.flavors.get(currentFlavor);
        HashMap<String, Double> currentSpecs = new HashMap<String, Double>();
        for (ResourceInfo.ResourceSpec resourceSpec : resourceInfo.specs) {
            currentSpecs.put(resourceSpec.property, Double.parseDouble(resourceSpec.value));
        }
        double minResources = -100000.0;
        String foundFlavor = "";
        for (ResourceInfo resInfo : flavors.values()) {
            double diff = 0.0;
            boolean ok = true;
            for (String resource : currentSpecs.keySet()) {
                HashMap<String, Double> diffSpecs = new HashMap<String, Double>();
                for (ResourceInfo.ResourceSpec resourceSpec : resInfo.specs) {
                    diffSpecs.put(resourceSpec.property, Double.parseDouble(resourceSpec.value));
                }
                if (diffSpecs.get(resource) / maxResourceValues.get(resource) - currentSpecs.get(resource) / maxResourceValues.get(resource)>0){
                    ok=false;
                }
                diff += diffSpecs.get(resource) / maxResourceValues.get(resource) - currentSpecs.get(resource) / maxResourceValues.get(resource);

            }
            if (ok&&diff != 0 && diff > minResources && diff < 0 && diff < (-1.0) * MAKES_SENSE_TO_SCALE_VERTICALLY) {
                minResources = diff;
                foundFlavor = resInfo.name;
            }
        }
        return foundFlavor;
    }

    public void diagonallyScale(Node serviceID, Strategy strategy) {
        serviceID.getStaticInformation().put("DefaultFlavor", chooseNewFlavorUp(serviceID, strategy));
    }

    public boolean scaleDiagonallyUp(Node serviceID, Strategy strategy) {
        try{
        double prevValue = MAKES_SENSE_TO_SCALE_VERTICALLY;
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = 0.0001;
        serviceID.getStaticInformation().put("DefaultFlavor", chooseNewFlavorUp(serviceID, strategy));
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = prevValue;
        }catch(Exception e ){
            RuntimeLogger.logger.error(e.getMessage());
        }
        return true;
    }

    public boolean scaleDiagonallyUp(Node serviceID) {
        try{
        double prevValue = MAKES_SENSE_TO_SCALE_VERTICALLY;
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = 0.0001;
        serviceID.getStaticInformation().put("DefaultFlavor", chooseNewFlavorUp(serviceID, null));
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = prevValue;
        }catch(Exception e){
            RuntimeLogger.logger.error(e.getMessage());
        }
        return true;
    }

    public boolean scaleDiagonallyDown(Node serviceID, Strategy strategy) {
        try{
        double prevValue = MAKES_SENSE_TO_SCALE_VERTICALLY;
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = 0.00001;
        serviceID.getStaticInformation().put("DefaultFlavor", chooseNewFlavorDown(serviceID, strategy));
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = prevValue;
         }catch(Exception e){
            RuntimeLogger.logger.error(e.getMessage());
        }
        return true;
    }

    public boolean scaleDiagonallyDown(Node serviceID) {
        try{
        double prevValue = MAKES_SENSE_TO_SCALE_VERTICALLY;
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = 0.00001;
        serviceID.getStaticInformation().put("DefaultFlavor", chooseNewFlavorDown(serviceID, null));
        this.MAKES_SENSE_TO_SCALE_VERTICALLY = prevValue;
         }catch(Exception e){
            RuntimeLogger.logger.error(e.getMessage());
        }
        return true;
    }
    

}
