package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.openstack.JCloudsOpenStackConnection;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;

public class EnforcementPluginCELAR implements EnforcementInterface {
	private MonitoringAPIInterface monitoringAPI;
	private Node cloudService;
	public EnforcementPluginCELAR(Node cloudService){
		this.cloudService=cloudService;
		
	}
	public String executeCommand(String command){
		String ip = "";
		try{
		Process p = Runtime.getRuntime().exec(command);                                                                                                                                                     
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String s ="";
		while ((s = stdInput.readLine()) != null) {
	        if (s.contains("Adding")){
	        	String[] x=s.split("[ :]");
	        	if (x.length>=2)
	        	if (x[1].charAt(0)>='0'&&x[1].charAt(0)<='9'){
	        		ip=x[1];
	        	}
	        }
	        RuntimeLogger.logger.info("From scaling command " +s);
		}
		
		if (ip.length()>0 && ip.charAt(0)>='0'&&ip.charAt(0)<='9'){
			return ip;}
		else
		{
			RuntimeLogger.logger.info("Answer from scale command "+ip+" ");
			return "";
		}
		}catch(Exception e ){
			RuntimeLogger.logger.info("Answer from scale command "+ip+" "+e.getMessage());
			return "";
		}
		
	}

	@Override
	public void scaleOut(Node toBeScaled) {
		
		String ip = executeCommand("/root/scripts/addNode.sh");
		if (!ip.equalsIgnoreCase("")){
		Node newVM = new Node();
		newVM.setNodeType(NodeType.VIRTUAL_MACHINE);
		Relationship rel = new Relationship();
		rel.setSourceElement(toBeScaled.getId());
		rel.setTargetElement(ip);
		rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
		newVM.setId(ip);
		toBeScaled.addNode(newVM,rel);
		
		monitoringAPI.refreshServiceStructure(cloudService);
}
	}

	@Override
	public void scaleIn(Node toBeScaled) {
		String ip = executeCommand("/root/scripts/removeNode.sh");
		
		if (!ip.equalsIgnoreCase("")){
			DependencyGraph dep = new DependencyGraph();
			dep.setCloudService(cloudService);
			Node toBeDel = dep.getNodeWithID(ip);
			toBeScaled.removeNode(toBeDel);
		}
		monitoringAPI.refreshServiceStructure(cloudService);

	}

	@Override
	public List<String> getElasticityCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enforceAction(String actionName, Node entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setControlledService(Node controlledService) {
		cloudService=controlledService;
		
	}

	@Override
	public Node getControlledService() {
		return cloudService;
	}

	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
		monitoringAPI=monitoring;
	}

}
