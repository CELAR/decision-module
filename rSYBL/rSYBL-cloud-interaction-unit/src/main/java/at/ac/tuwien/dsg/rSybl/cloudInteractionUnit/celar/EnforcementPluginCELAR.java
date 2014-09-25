package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.csdg.relationships.ElasticityRelationship;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.dbalancer.ElasticityAction;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin.MELA_API3;
import gr.ntua.cslab.orchestrator.beans.ExecutedResizingAction;
import gr.ntua.cslab.orchestrator.beans.Parameter;
import gr.ntua.cslab.orchestrator.beans.Parameters;
import gr.ntua.cslab.orchestrator.beans.ResizingAction;
import gr.ntua.cslab.orchestrator.beans.ResizingActionList;
import gr.ntua.cslab.orchestrator.beans.ResizingActionType;
import gr.ntua.cslab.orchestrator.beans.ResizingExecutionStatus;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import org.codehaus.jackson.map.ObjectMapper;

public class EnforcementPluginCELAR implements EnforcementInterface {

    private MonitoringAPIInterface monitoringAPI;
    private Node cloudService;
    boolean cleanupGoingOn = false;
    boolean cleanupNecessary = true;
    public static String API_URL = "https://83.212.107.38:8443/resizing/";
    public HashMap<Integer, ResizingAction> actionsAvailable = new HashMap<Integer, ResizingAction>();

    public EnforcementPluginCELAR(Node cloudService) {
        this.cloudService = cloudService;
        API_URL = Configuration.getEnforcementServiceURL();

    }

    public void refreshElasticityActionsList(String actionName) {
        String ip = "";
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(API_URL);


            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            JAXBContext jAXBContext = JAXBContext.newInstance(ResizingActionList.class);
            ResizingActionList retrievedData = (ResizingActionList) jAXBContext.createUnmarshaller().unmarshal(inputStream);
            for (ResizingAction action : retrievedData.getResizingActions()) {
                this.actionsAvailable.put(action.getId(), action);
            }

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
            RuntimeLogger.logger.error("Failing to connect to Orchestrator");

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public boolean scaleIn(Node node) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.SCALE_IN && toBeScaled.getId() == action.getModuleId() + "") {
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId());
                ResizingExecutionStatus status = checkForAction(executedResizingAction.getUniqueId());
                while (status == ResizingExecutionStatus.ONGOING) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    status = checkForAction(executedResizingAction.getUniqueId());
                }
                if (status == ResizingExecutionStatus.SUCCESS) {
                    //TODO : Assume we have value IP returned
                    Parameters par = executedResizingAction.getParameters();
                    Parameter p = (Parameter) par.getParameters().get(0);
                    if (p.getKey().equalsIgnoreCase("ip")) {
                        String ip = p.getValue();
                        toBeScaled.removeNode(ip);
                        monitoringAPI.refreshServiceStructure(cloudService);

                    }
                }

                if (status == ResizingExecutionStatus.FAILED) {
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
            if (action.getType() == ResizingActionType.SCALE_IN && toBeScaled.getId() == action.getModuleId() + "") {
                Parameters pars = new Parameters();
                Parameter par = new Parameter();
                par.setKey("IP");
                par.setValue(ip);
                pars.addParameter(par);
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId(), pars);
                ResizingExecutionStatus status = checkForAction(executedResizingAction.getUniqueId());
                while (status == ResizingExecutionStatus.ONGOING) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    status = checkForAction(executedResizingAction.getUniqueId());
                }
                if (status == ResizingExecutionStatus.SUCCESS) {
                    //TODO : Assume we have value IP returned
                    //Parameters par = executedResizingAction.getParameters();
                    //Parameter p = (Parameter) par.getParameters().get(0);
                    //  String ip = p.getValue();
                    toBeScaled.removeNode(ip);
                    monitoringAPI.refreshServiceStructure(cloudService);


                }

                if (status == ResizingExecutionStatus.FAILED) {
                }
                break;
            }
        }
        return ok;
    }

    public static ResizingExecutionStatus checkForAction(String uniqueID) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(API_URL + "/status");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            JAXBContext jAXBContext = JAXBContext.newInstance(ResizingExecutionStatus.class);
            ResizingExecutionStatus retrievedData = (ResizingExecutionStatus) jAXBContext.createUnmarshaller().unmarshal(inputStream);
            return retrievedData;

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
            RuntimeLogger.logger.error("Failing to connect to Orchestrator");

        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
        return null;
    }

    @Override
    public boolean scaleOut(Node node) {
        boolean ok = true;
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        Node toBeScaled = dependencyGraph.getNodeWithID(node.getId());
        for (Entry<Integer, ResizingAction> actionE : actionsAvailable.entrySet()) {
            ResizingAction action = actionE.getValue();
            if (action.getType() == ResizingActionType.SCALE_OUT && toBeScaled.getId() == action.getModuleId() + "") {
                ExecutedResizingAction executedResizingAction = executeResizingCommand(action.getId());
                ResizingExecutionStatus status = checkForAction(executedResizingAction.getUniqueId());
                while (status == ResizingExecutionStatus.ONGOING) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    status = checkForAction(executedResizingAction.getUniqueId());
                }
                if (status == ResizingExecutionStatus.SUCCESS) {
                    //TODO : Assume we have value IP returned
                    Parameters par = executedResizingAction.getParameters();
                    Parameter p = (Parameter) par.getParameters().get(0);
                    if (p.getKey().equalsIgnoreCase("ip")) {
                        String ip = p.getValue();
                        if (!ip.equalsIgnoreCase("err") && !ip.equalsIgnoreCase("")) {
                            Node artifact = null;
                            Node container = null;
                            Node newNode = new Node();
                            if (node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {
                                artifact = node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);
                                if (artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER) != null && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).size() > 0) {

                                    container = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).get(0);
                                }
                            }
                            newNode.getStaticInformation().put("IP", ip);
                            newNode.setId(ip);
                            newNode.setNodeType(NodeType.VIRTUAL_MACHINE);

                            SimpleRelationship rel = new SimpleRelationship();
                            rel.setTargetElement(newNode.getId());
                            rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                            RuntimeLogger.logger.info("Adding to " + node.getId() + " vm with ip " + ip);



                            if (artifact == null && container == null) {
                                rel.setSourceElement(node.getId());
                                node.addNode(newNode, rel);
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
                        RuntimeLogger.logger.info("The controlled service is now " + cloudService.toString());

                        monitoringAPI.refreshServiceStructure(cloudService);
                        return ok;

                    }
                }

                if (status == ResizingExecutionStatus.FAILED) {
                }
                break;
            }
        }
        return ok;
    }

    public static ExecutedResizingAction executeResizingCommand(Integer actionID, Parameters pars) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(API_URL + "/?query=" + actionID + "/");



            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");
            OutputStream os = connection.getOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(Parameters.class);
            jaxbContext.createMarshaller().marshal(pars, os);
            StringWriter stringWriter = new StringWriter();
            jaxbContext.createMarshaller().marshal(pars, stringWriter);

            RuntimeLogger.logger.info(stringWriter.toString());
            os.flush();
            os.close();
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            JAXBContext jAXBContext = JAXBContext.newInstance(ExecutedResizingAction.class);
            ExecutedResizingAction retrievedData = (ExecutedResizingAction) jAXBContext.createUnmarshaller().unmarshal(inputStream);
            return retrievedData;

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
            RuntimeLogger.logger.error("Failing to connect to Orchestrator");

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public static ExecutedResizingAction executeResizingCommand(Integer actionID) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(API_URL + "/?query=" + actionID + "/");



            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.SEVERE, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            JAXBContext jAXBContext = JAXBContext.newInstance(ExecutedResizingAction.class);
            ExecutedResizingAction retrievedData = (ExecutedResizingAction) jAXBContext.createUnmarshaller().unmarshal(inputStream);
            return retrievedData;

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
            RuntimeLogger.logger.error("Failing to connect to Orchestrator");

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
//		try{
//		Process p = Runtime.getRuntime().exec(command);                                                                                                                                                     
//		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//		String s ="";
//		while ((s = stdInput.readLine()) != null) {
//	        if (s.contains("Adding")){
//	        	String[] x=s.split("[ :]");
//	        	if (x.length>=2)
//	        	if (x[1].charAt(0)>='0'&&x[1].charAt(0)<='9'){
//	        		ip=x[1];
//	        	}
//	        }
//	        RuntimeLogger.logger.info("From scaling command " +s);
//		}
//		
//		if (ip.length()>0 && ip.charAt(0)>='0'&&ip.charAt(0)<='9'){
//			return ip;}
//		else
//		{
//			RuntimeLogger.logger.info("Answer from scale command "+ip+" ");
//			return "";
//		}
//		}catch(Exception e ){
//			RuntimeLogger.logger.info("Answer from scale command "+ip+" "+e.getMessage());
//			return "";
//		}

    }

    public static void main(String[] args) {
    }

    public void cleanup() {
        URL url = null;
        try {
            url = new URL(API_URL + "resize/?action=cleanup");
            InputStream is = url.openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();


            String cp = new String();
            String s = "";
            while ((cp = rd.readLine()) != null) {

                sb.append(cp);
                s += cp;
            }
            RuntimeLogger.logger.info("Cleanup returning " + s);
            while (!checkStatus("", "cleanup")) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            RuntimeLogger.logger.error("Error at cleanup" + e.getMessage() + " " + e.getCause());
        }
    }

    public boolean checkStatus(String ip, String action) {
        URL url = null;
        HttpURLConnection connection = null;
        try {

            if (!ip.equalsIgnoreCase("")) {
                url = new URL(API_URL + "resizestatus/?action=" + action + "&ip=" + ip);
            } else {
                url = new URL(API_URL + "resizestatus/?action=" + action);
                RuntimeLogger.logger.info("Check cleanup status " + url);

            }

            InputStream is = url.openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();


            String cp = new String();
            String s = "";
            while ((cp = rd.readLine()) != null) {

                sb.append(cp);
                s += cp;
            }
            RuntimeLogger.logger.info("STATUS:" + sb);

            if (s.contains("true")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            RuntimeLogger.logger.error("Error when checking for status of " + ip + " with action " + action);
            return false;
        }


    }

    @Override
    public List<String> getElasticityCapabilities() {
        // TODO Auto-generated method stub
        List<String> avActions = new ArrayList<String>();
        for (ResizingAction action : this.actionsAvailable.values()) {
            avActions.add(action.getName());
        }
        return avActions;
    }

    public boolean enforceAction(String actionName, Node entity) {
        if (actionName.equalsIgnoreCase("cleanup")) {
            cleanupGoingOn = true;
            cleanup();

            cleanupGoingOn = false;
            return true;
        }
        return false;
    }

    @Override
    public void setControlledService(Node controlledService) {
        cloudService = controlledService;

    }

    @Override
    public Node getControlledService() {
        return cloudService;
    }

    @Override
    public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
        monitoringAPI = monitoring;
    }

    @Override
    public boolean containsElasticityCapability(Node entity, String capability) {
        switch (capability.toLowerCase()) {
            case "scalein":
                return true;
            case "scaleout":
                return true;
            default:
                return false;
        }
    }
}
