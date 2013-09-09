/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group
    E184.                 
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

package  at.ac.tuwien.dsg.rSybl.analysisEngine.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPI;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPI;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningGreedyAlgorithm;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;







public class ControlService{
	SYBLService syblService;
	MonitoringAPIInterface monitoringAPI ;
	EnforcementAPIInterface enforcementAPI;

	DependencyGraph dependencyGraph ;
	PlanningGreedyAlgorithm planningGreedyAlgorithm;

	
	public ControlService(){
		InputProcessing input = new InputProcessing();
		dependencyGraph=input.loadDependencyGraph();
	
		try {
			//SYBLDirectivesEnforcementLogger.logger.info("Current graph is "+dependencyGraph.graphToString());

		//	at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node clService = MappingToWS.mapNodeToNode(dependencyGraph.getCloudService());
			Node node = new Node();
			node = dependencyGraph.getCloudService();
			monitoringAPI = new MonitoringAPI();
			monitoringAPI.setControlledService(node);
			enforcementAPI = new EnforcementAPI();
			enforcementAPI.setControlledService(node);
			enforcementAPI.setMonitoringPlugin(monitoringAPI);

    	} catch (Exception e) {
    		SYBLDirectivesEnforcementLogger.logger.error( "Control service Instantiation "+e.toString());
    		e.printStackTrace();
		}
		
			syblService=new SYBLService(dependencyGraph,monitoringAPI,enforcementAPI);
			    //CloudService cloudService, ArrayList<SYBLSpecification> syblSpecifications
		    disableConflictingConstraints();
		    PlanningLogger.logger.info("1 Number of elasticity requirements are "+dependencyGraph.getAllElasticityRequirements().size());

    	for (ElasticityRequirement syblSpecification:dependencyGraph.getAllElasticityRequirements()){
		    SYBLAnnotation annotation = syblSpecification.getAnnotation();
		    SYBLDirectivesEnforcementLogger.logger.info("Processing annotations for entity "+syblSpecification.getAnnotation().getEntityID() + " the annotations are "+annotation);
		    syblService.processAnnotations(syblSpecification.getAnnotation().getEntityID(), annotation);

		}
 	    PlanningLogger.logger.info("2 Number of elasticity requirements are "+dependencyGraph.getAllElasticityRequirements().size());

    	planningGreedyAlgorithm = new PlanningGreedyAlgorithm(dependencyGraph,monitoringAPI,enforcementAPI);
	   
 	    PlanningLogger.logger.info("3 Number of elasticity requirements are "+dependencyGraph.getAllElasticityRequirements().size());

		    planningGreedyAlgorithm.start();
    		}
	
	public void writeCurrentDirectivesToFile(String file){
    	BufferedWriter out = null;
   	 try {
		        out = new BufferedWriter(new FileWriter(file));
		        
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
   	 
   	for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			    SYBLAnnotation annotation = elReq.getAnnotation();
		    try {
		    	out.write(annotation.getEntityID()+"\n");
				out.write(annotation.getConstraints()+"\n");
				out.write(annotation.getStrategies()+"\n");
				out.write(annotation.getMonitoring()+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		  //  syblService.processAnnotations(syblSpecification.getComponentId(), annotation);
		}
   	try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean checkDistribution(Node arg1)
			throws RemoteException {
		// TODO Auto-generated method stub
		return syblService.checkIfContained( arg1);
	}
	
	public void processAnnotation( String arg1, SYBLAnnotation arg2)
			throws RemoteException {
		syblService.processAnnotations(arg1, arg2);
	}
	
	
	
	
	public boolean checkIfConstraintsAreConflicting(Constraint constraint1,Constraint constraint2){
		boolean conflict = false;
		String metricLeft1= "";
		Float numberRight1=0.0f;
		
		if (constraint1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null){
			 metricLeft1 = constraint1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
			numberRight1 = Float.parseFloat(constraint1.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getNumber());
			
			
		}else{
			metricLeft1 = constraint1.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
			numberRight1 = Float.parseFloat(constraint1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getNumber());
			if (constraint1.getToEnforce().getBinaryRestriction().get(0).getType().contains("lessThan"))
			constraint1.getToEnforce().getBinaryRestriction().get(0).getType().replaceAll("lessThan", "greaterThan");
			else constraint1.getToEnforce().getBinaryRestriction().get(0).getType().replaceAll("greaterThan", "lessThan");
			}
		String metricLeft2= "";
		Float numberRight2=0.0f;
		
		if (constraint2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null){
			 metricLeft2 = constraint2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
			numberRight2= Float.parseFloat(constraint2.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getNumber());
			
			
		}else{
			metricLeft2 = constraint2.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
			numberRight2 = Float.parseFloat(constraint2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getNumber());
			if (constraint2.getToEnforce().getBinaryRestriction().get(0).getType().contains("lessThan"))
			constraint2.getToEnforce().getBinaryRestriction().get(0).getType().replaceAll("lessThan", "greaterThan");
			else constraint2.getToEnforce().getBinaryRestriction().get(0).getType().replaceAll("greaterThan", "lessThan");
		}
		if (metricLeft1.equalsIgnoreCase(metricLeft2))
		if ( constraint1.getToEnforce().getBinaryRestriction().get(0).getType().contains("lessThan")){
			if (constraint2.getToEnforce().getBinaryRestriction().get(0).getType().contains("greaterThan")){
				if (numberRight1<=numberRight2) return true;
			}
		}else{
			if ( constraint1.getToEnforce().getBinaryRestriction().get(0).getType().contains("greaterThan")){
				if (constraint2.getToEnforce().getBinaryRestriction().get(0).getType().contains("lessThan")){
					if (numberRight1>=numberRight2) return true;
				}
			}
		}
		return conflict;
	}
	
	public void disableConflictingConstraints(){
		HashMap<String,String> toRemove = new HashMap<String,String>();
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification=SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
		if (!syblSpecification.getType().equalsIgnoreCase("component")){
			List<Constraint> constraints=syblSpecification.getConstraint();
			//System.err.println("Searching for "+syblSpecification.getComponentId());
			Node entity = dependencyGraph.getNodeWithID(syblSpecification.getComponentId());
			if (entity.getNodeType()==NodeType.CLOUD_SERVICE){
				entity=( entity).getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).get(0);
			}
			if (entity.getNodeType()==NodeType.SERVICE_TOPOLOGY){
				//ComponentTopology componentTopology = (ComponentTopology) entity;
				for (Node topology: entity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)){
					for( ElasticityRequirement el: topology.getElasticityRequirements()){
						SYBLSpecification specification=SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(el.getAnnotation());
						if (specification.getComponentId().equalsIgnoreCase(topology.getId())){
							for (Constraint c1:constraints){
								for (Constraint c2:specification.getConstraint()){
									String metric1 ="";
									if (c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric1=c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
									else metric1= c1.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
									
									String metric2 ="";
									if (c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric2=c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
									else metric2= c2.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
									
									if ( metric1.equalsIgnoreCase(metric2))
									if (checkIfConstraintsAreConflicting(c1, c2) ){
										toRemove.put(metric1,c1.getId());
									}else{
										if (toRemove.containsKey(metric1))
										{
											if (toRemove.get(metric1).equalsIgnoreCase(c1.getId())){
												toRemove.remove(metric1);
											}
										}
									}
								}
							}
						}
					}
					if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)!=null){
						for (Node topology1:topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)){
							for( ElasticityRequirement el: topology.getElasticityRequirements()){
								SYBLSpecification specification=SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(el.getAnnotation());
									if (specification.getComponentId().equalsIgnoreCase(topology1.getId())){
									for (Constraint c1:constraints){
										for (Constraint c2:specification.getConstraint()){
											String metric1 ="";
											if (c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric1=c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
											else metric1= c1.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
											
											String metric2 ="";
											if (c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric2=c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
											else metric2= c2.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
											
											if ( metric1.equalsIgnoreCase(metric2))
											if (checkIfConstraintsAreConflicting(c1, c2)){
												toRemove.put(metric1,c1.getId());
											}else{
												if (toRemove.containsKey(metric1))
												{
													if (toRemove.get(metric1).equalsIgnoreCase(c1.getId())){
														toRemove.remove(metric1);
													}
												}
											}										}
									}								}
							}
						}
					}
					for (Node comp:topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)){
						for( ElasticityRequirement el: topology.getElasticityRequirements()){
							SYBLSpecification specification=SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(el.getAnnotation());
							if (specification.getComponentId().equalsIgnoreCase(comp.getId())){
								for (Constraint c1:constraints){
									for (Constraint c2:specification.getConstraint()){
										String metric1 ="";
										if (c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric1=c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
										else metric1= c1.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
										
										String metric2 ="";
										if (c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric2=c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
										else metric2= c2.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
										
										if ( metric1.equalsIgnoreCase(metric2))
										if (checkIfConstraintsAreConflicting(c1, c2) ){
											toRemove.put(metric1,c1.getId());
										}else{
											if (toRemove.containsKey(metric1))
											{
												if (toRemove.get(metric1).equalsIgnoreCase(c1.getId())){
													toRemove.remove(metric1);
												}
											}
										}									}
								}							}
						}
					}
				}
				if ((entity).getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null)
				for (Node comp:entity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)){
					for( ElasticityRequirement el: comp.getElasticityRequirements()){
						SYBLSpecification specification=SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(el.getAnnotation());
						if (specification.getComponentId().equalsIgnoreCase(comp.getId())){
							for (Constraint c1:constraints){
								for (Constraint c2:specification.getConstraint()){
									String metric1 ="";
									if (c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric1=c1.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
									else metric1= c1.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
									
									String metric2 ="";
									if (c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric()!=null) metric2=c2.getToEnforce().getBinaryRestriction().get(0).getLeftHandSide().getMetric();
									else metric2= c2.getToEnforce().getBinaryRestriction().get(0).getRightHandSide().getMetric();
									
									if ( metric1.equalsIgnoreCase(metric2))
									if (checkIfConstraintsAreConflicting(c1, c2) ){
										toRemove.put(metric1,c1.getId());
									}else{
										if (toRemove.containsKey(metric1))
										{
											if (toRemove.get(metric1).equalsIgnoreCase(c1.getId())){
												toRemove.remove(metric1);
											}
										}
									}								}
							}						}
					}
				}
			}
		
		}
		}
	for (ElasticityRequirement elasticityRequirement:dependencyGraph.getAllElasticityRequirements()){
		SYBLSpecification specification=SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation());
		List<Constraint> constr = new ArrayList<Constraint>();
		constr.addAll(specification.getConstraint());	
		for (Constraint i : constr){
			if (toRemove.containsValue(i.getId()))
			{
				SYBLDirectivesEnforcementLogger.logger.info("Removing the constraint "+i.getId());
				specification.getConstraint().remove(constr.indexOf(i));
			}
		}
	}
	}
	
	

}
