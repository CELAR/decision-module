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

package at.ac.tuwien.dsg.rSybl.planningEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitoring;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.languageDescription.SYBLDescriptionParser;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;




public class ContextRepresentation {
	
	private MonitoredCloudService monitoredCloudService = new MonitoredCloudService(); 
	private DependencyGraph dependencyGraph;
	private HashMap<Node,Node> mapMonitoringVars=new HashMap<Node,Node>();
	private MonitoringAPIInterface monitoringAPI ;
	public ContextRepresentation(DependencyGraph cloudService, MonitoringAPIInterface monitoringAPI){
		this.dependencyGraph=cloudService;
		this.monitoringAPI=monitoringAPI;
		
	}
	public void initializeContext(DependencyGraph dependencyGraph){
		//find all targeted metrics
		createMonitoredService();	
	}
	
	
	
	private MonitoredEntity findTargetedMetrics(Node entity,MonitoredEntity monitoredEntity){
		
		for (ElasticityRequirement elasticityRequirement :entity.getElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation());
			monitoredEntity.setId(entity.getId());
   			
	   			for (Monitoring monitoring:syblSpecification.getMonitoring()){
					monitoredEntity.setMonitoredVar(monitoring.getMonitor().getEnvVar(),monitoring.getMonitor().getMetric());
		   			SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();
		   			String methodName = descriptionParser.getMethod(monitoring.getMonitor().getMetric());
					Float value = 0.0f;	
					if (!methodName.equals("")) {
							try {
								Class partypes[] = new Class[1];
								Object[] parameters = new Object[1];
								parameters[0]=entity;
								partypes[0]=Node.class;
								
								Method method = MonitoringInterface.class.getMethod(methodName,partypes);

								value = (Float)method.invoke(monitoringAPI,parameters);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							
						 value= (Float) monitoringAPI.getMetricValue(monitoring.getMonitor().getMetric(), entity);
							
						}
					monitoredEntity.setMonitoredValue(monitoring.getMonitor().getMetric(),value );		
	   		}
	   			
	   			for (Constraint constraint:syblSpecification.getConstraint()){
		   			SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();
		   			for (BinaryRestriction restriction:constraint.getToEnforce().getBinaryRestriction()){
		   				String right = restriction.getRightHandSide().getMetric();
		   				String left = restriction.getLeftHandSide().getMetric();
		   				String metric="";
		   				if (right!=null)
		   					metric =right;
		   				else metric=left;
		   				String methodName = descriptionParser.getMethod(metric);
		   				Float value = 0.0f;	
		   				if (!methodName.equals("")) {
							try {
								Class partypes[] = new Class[1];
								Object[] parameters = new Object[1];
								parameters[0]=entity;
								partypes[0]= Node.class;
								Method method = MonitoringInterface.class.getMethod(methodName,partypes);
								value = (Float)method.invoke(monitoringAPI,parameters);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							 value= (Float) monitoringAPI.getMetricValue(metric, entity);

						}
		   				monitoredEntity.setMonitoredValue(metric,value );	
		   			}
	   		
	   	}
	}
		return monitoredEntity;
	}
	
	private MonitoredCloudService createMonitoredService(){
		
		monitoredCloudService=(MonitoredCloudService) findTargetedMetrics(dependencyGraph.getCloudService(), monitoredCloudService);
		
		
		List<Node> topologies =new ArrayList<Node>();
		topologies.addAll(dependencyGraph.getCloudService().getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY));
		
			
		for (Node currentTopology:topologies){
			MonitoredComponentTopology monitoredTopology = new MonitoredComponentTopology();
			monitoredTopology = (MonitoredComponentTopology) findTargetedMetrics( currentTopology,monitoredTopology);
		    if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)!=null)
			for(Node componentTopology: currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)){
		    	MonitoredComponentTopology monitoredTopology1 = new MonitoredComponentTopology();
				monitoredTopology1 = (MonitoredComponentTopology) findTargetedMetrics(componentTopology,monitoredTopology1);
				for (Node component:componentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_UNIT)){
					MonitoredComponent monitoredComponent = new MonitoredComponent();
					monitoredComponent = (MonitoredComponent) findTargetedMetrics(component,monitoredComponent);
					monitoredTopology1.addMonitoredComponent(monitoredComponent);
				}
				monitoredTopology.addMonitoredTopology(monitoredTopology1);

		    }
		    for(Node component: currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_UNIT)){
		    	MonitoredComponent monitoredComponent = new MonitoredComponent();
				monitoredComponent = (MonitoredComponent) findTargetedMetrics(component,monitoredComponent);
				monitoredTopology.addMonitoredComponent(monitoredComponent);
		    }
			monitoredCloudService.addMonitoredTopology(monitoredTopology);

		}
		

		return monitoredCloudService;
	}
	
	public void doAction(ActionEffect action){
		for (String currentMetric:monitoredCloudService.getMonitoredMetrics()){
			if (action.getActionEffectForMetric(currentMetric,monitoredCloudService.getId())!=null)
			monitoredCloudService.setMonitoredValue(currentMetric, monitoredCloudService.getMonitoredValue(currentMetric) + action.getActionEffectForMetric(currentMetric,monitoredCloudService.getId()) );
		}
		for (MonitoredComponentTopology componentTopology:monitoredCloudService.getMonitoredTopologies()){
			for (String currentMetric:componentTopology.getMonitoredMetrics()){
				if(action.getActionEffectForMetric(currentMetric,componentTopology.getId())!=null)
				componentTopology.setMonitoredValue(currentMetric, componentTopology.getMonitoredValue(currentMetric) +action.getActionEffectForMetric(currentMetric,componentTopology.getId()));
			}
			for (MonitoredComponentTopology componentTopology2:componentTopology.getMonitoredTopologies()){
				for (String currentMetric:componentTopology2.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,componentTopology2.getId())!=null)
					componentTopology2.setMonitoredValue(currentMetric, componentTopology2.getMonitoredValue(currentMetric) +action.getActionEffectForMetric(currentMetric,componentTopology2.getId()) );
				}
				for (MonitoredComponent comp:componentTopology2.getMonitoredComponents()){
					for (String currentMetric:comp.getMonitoredMetrics()){
						if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null)
						comp.setMonitoredValue(currentMetric, comp.getMonitoredValue(currentMetric) +action.getActionEffectForMetric(currentMetric,comp.getId()) );
					}
				}
			}
			for (MonitoredComponent comp:componentTopology.getMonitoredComponents()){
				for (String currentMetric:comp.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null)
					comp.setMonitoredValue(currentMetric, comp.getMonitoredValue(currentMetric) +action.getActionEffectForMetric(currentMetric,comp.getId()) );
				}
			}
		}
	}
	public void undoAction(ActionEffect action){
		for (String currentMetric:monitoredCloudService.getMonitoredMetrics()){
			if (action.getActionEffectForMetric(currentMetric,monitoredCloudService.getId())!=null)
			monitoredCloudService.setMonitoredValue(currentMetric, monitoredCloudService.getMonitoredValue(currentMetric) +(-1)*action.getActionEffectForMetric(currentMetric,monitoredCloudService.getId()) );
		}
		for (MonitoredComponentTopology componentTopology:monitoredCloudService.getMonitoredTopologies()){
			for (String currentMetric:componentTopology.getMonitoredMetrics()){
				if (action.getActionEffectForMetric(currentMetric,componentTopology.getId())!=null)
				componentTopology.setMonitoredValue(currentMetric, componentTopology.getMonitoredValue(currentMetric) +(-1)*action.getActionEffectForMetric(currentMetric,componentTopology.getId()));
			}
			for (MonitoredComponentTopology componentTopology2:componentTopology.getMonitoredTopologies()){
				for (String currentMetric:componentTopology2.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,componentTopology2.getId())!=null)
					componentTopology2.setMonitoredValue(currentMetric, componentTopology2.getMonitoredValue(currentMetric) +(-1)*action.getActionEffectForMetric(currentMetric,componentTopology2.getId()) );
				}
				for (MonitoredComponent comp:componentTopology2.getMonitoredComponents()){
					for (String currentMetric:comp.getMonitoredMetrics()){
						if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null)
						comp.setMonitoredValue(currentMetric, comp.getMonitoredValue(currentMetric) +(-1)*action.getActionEffectForMetric(currentMetric,comp.getId()) );
					}
				}
			}
			for (MonitoredComponent comp:componentTopology.getMonitoredComponents()){
				for (String currentMetric:comp.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null)
					comp.setMonitoredValue(currentMetric, comp.getMonitoredValue(currentMetric) +(-1)*action.getActionEffectForMetric(currentMetric,comp.getId()) );
				}
			}
		}
		
	}
	public MonitoredEntity findMonitoredEntity(String id){
		boolean found=false;
		if (!found){
			if (id.equalsIgnoreCase(monitoredCloudService.getId())){
				found = true;
				return monitoredCloudService;
			}
		}

		List<MonitoredComponentTopology> topologies =new ArrayList<MonitoredComponentTopology>();
		if (monitoredCloudService.getMonitoredTopologies()!=null)
		topologies.addAll(monitoredCloudService.getMonitoredTopologies());
		
		List<MonitoredComponent> componentsToExplore = new ArrayList<MonitoredComponent>();
		while (!found && !topologies.isEmpty()){
			MonitoredComponentTopology currentTopology = topologies.get(0);
			if (currentTopology!=null){
				PlanningLogger.logger.info("id "+id+" current topology "+currentTopology+ "  "+ currentTopology.getId()+" ");
				
			if (currentTopology.getId().equalsIgnoreCase(id)){
				found=true;
				return currentTopology;
			}else{
				if (currentTopology.getMonitoredTopologies()!=null && currentTopology.getMonitoredTopologies().size()>0)
					topologies.addAll(currentTopology.getMonitoredTopologies());
				if (currentTopology.getMonitoredComponents()!=null && currentTopology.getMonitoredComponents().size()>0)
				componentsToExplore.addAll(currentTopology.getMonitoredComponents());
			}
			if (currentTopology.getMonitoredComponents()!=null && currentTopology.getMonitoredComponents().size()>0)
				componentsToExplore.addAll(currentTopology.getMonitoredComponents());
		}
			topologies.remove(0);
}
		
		while (!found && !componentsToExplore.isEmpty()){
			MonitoredComponent component =componentsToExplore.get(0);
			componentsToExplore.remove(0);
			if (component.getId().equalsIgnoreCase(id)){
				//System.out.println(component.getId());
				found=true;
				return component;
			}
		}
		return null;
	}
	public String getViolatedConstraints(){
		String constr = "";
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());

			//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
			MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
			for (Constraint constraint:syblSpecification.getConstraint()){
				boolean evaluate = true;
				if (constraint.getCondition()!=null){
					for (BinaryRestriction binaryRestriction:constraint.getCondition().getBinaryRestriction()){
						float currentLeftValue=0;
						float currentRightValue = 0;
						if (binaryRestriction.getLeftHandSide().getMetric()!=null){
							String metric = binaryRestriction.getLeftHandSide().getMetric();
							//System.out.println(monitoredEntity+" "+syblSpecification.getComponentId());
							try{
							currentLeftValue = monitoredEntity.getMonitoredValue(metric);
							if (currentLeftValue<0){
								if (monitoredEntity.getMonitoredVar(metric)!=null)
								currentLeftValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
								else currentRightValue=0;
							}
							}catch(Exception e){
								SYBLDirectivesEnforcementLogger.logger.error("Metric not found "+metric +" for entity "+monitoredEntity.getId());
							}
							currentRightValue=Float.parseFloat(binaryRestriction.getRightHandSide().getNumber());
						}else
						if (binaryRestriction.getRightHandSide().getMetric()!=null){
							String metric = binaryRestriction.getRightHandSide().getMetric();
							try{
							currentRightValue = monitoredEntity.getMonitoredValue(metric);
							//System.out.println("Current value for metric is  "+ currentRightValue);
							if (currentRightValue<0){
								if (monitoredEntity.getMonitoredVar(metric)!=null)
								currentRightValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
								else currentRightValue=0;
							}
							currentLeftValue=Float.parseFloat(binaryRestriction.getLeftHandSide().getNumber());
							}catch(Exception e){
								SYBLDirectivesEnforcementLogger.logger.error("Metric not found "+metric +" for entity "+monitoredEntity.getId());
							}
						}
						switch (binaryRestriction.getType()){
						case "lessThan":
							if (currentLeftValue>=currentRightValue){
								evaluate=false;
							}
							break;
						case "greaterThan":
							if (currentLeftValue<=currentRightValue){
								evaluate=false;
							}
							break;
						case "lessThanOrEqual":
							if (currentLeftValue>currentRightValue){
								evaluate=false;
							}
							break;
						case "greaterThanOrEqual":
							if (currentLeftValue<currentRightValue){
								evaluate=false;
							}
							break;
						case "differentThan":
							if (currentLeftValue==currentRightValue){
								evaluate=false;
							}
							break;
						case "equals":
							if (currentLeftValue!=currentRightValue){
								evaluate=false;
							}
							break;
						default:
							if (currentLeftValue>=currentRightValue){
								evaluate=false;
							}
							break;
						}
					}				
					
				}
				if (evaluate)
				for (BinaryRestriction binaryRestriction:constraint.getToEnforce().getBinaryRestriction()){
					float currentLeftValue=0;
					float currentRightValue = 0;
					if (binaryRestriction.getLeftHandSide().getMetric()!=null){
						String metric = binaryRestriction.getLeftHandSide().getMetric();
						//System.out.println(monitoredEntity+" "+syblSpecification.getComponentId());
						currentLeftValue = monitoredEntity.getMonitoredValue(metric);
						if (currentLeftValue<0){
							if (monitoredEntity.getMonitoredVar(metric)!=null)
							currentLeftValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
							else currentRightValue=0;
						}
						currentRightValue=Float.parseFloat(binaryRestriction.getRightHandSide().getNumber());
					}else
					if (binaryRestriction.getRightHandSide().getMetric()!=null){
						String metric = binaryRestriction.getRightHandSide().getMetric();
						currentRightValue = monitoredEntity.getMonitoredValue(metric);
						//System.out.println("Current value for metric is  "+ currentRightValue);
						if (currentRightValue<0){
							if (monitoredEntity.getMonitoredVar(metric)!=null)
							currentRightValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
							else currentRightValue=0;
						}
						currentLeftValue=Float.parseFloat(binaryRestriction.getLeftHandSide().getNumber());
					}
					switch (binaryRestriction.getType()){
					case "lessThan":
						if (currentLeftValue>=currentRightValue){
							//System.out.println("Violated constraint "+constraint.getId());
							constr+=constraint.getId()+" ";
						}
						break;
					case "greaterThan":
						if (currentLeftValue<=currentRightValue){
							//System.out.println("Violated constraint "+constraint.getId());
							constr+=constraint.getId()+" ";
						}
						break;
					case "lessThanOrEqual":
						if (currentLeftValue>currentRightValue){
						//	System.out.println("Violated constraint "+constraint.getId());
							constr+=constraint.getId()+" ";
						}
						break;
					case "greaterThanOrEqual":
						if (currentLeftValue<currentRightValue){
							//System.out.println("Violated constraint "+constraint.getId());

							constr+=constraint.getId()+" ";
						}
						break;
					case "differentThan":
						if (currentLeftValue==currentRightValue){
						//	System.out.println("Violated constraint "+constraint.getId());
							constr+=constraint.getId()+" ";
						}
						break;
					case "equals":
						if (currentLeftValue!=currentRightValue){
							//System.out.println("Violated constraint "+constraint.getId());
							constr+=constraint.getId()+" ";
						}
						break;
					default:
						if (currentLeftValue>=currentRightValue){
							//System.out.println("Violated constraint "+constraint.getId());
							constr+=constraint.getId()+" ";
						}
						break;
					}
				}
			}
		}
		return constr;
	}
	public int countFixedStrategies(ContextRepresentation previousContextRepresentation){
		int nbFixedStrategies = 0;
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
		//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
			MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
			for (Strategy strategy:syblSpecification.getStrategy()){
				if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximum")||strategy.getToEnforce().getActionName().toLowerCase().contains("minimum")){
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximum")){
						String[] s= strategy.getToEnforce().getActionName().split("[()]");
						if (monitoredEntity.getMonitoredValue(s[1])>previousContextRepresentation.getValueForMetric(monitoredEntity, s[1])){
							nbFixedStrategies+=1;
						}
					}
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimum")){
						String[] s= strategy.getToEnforce().getActionName().split("[()]");
						if (monitoredEntity.getMonitoredValue(s[1])<previousContextRepresentation.getValueForMetric(monitoredEntity, s[1])){
							nbFixedStrategies+=1;
						}
					}
				}
			}
		}
		return nbFixedStrategies;
	}
	public float getValueForMetric(MonitoredEntity monitoredEntity,String metricName){
		return monitoredEntity.getMonitoredValue(metricName);
	}
	public int countViolatedConstraints(){
		int numberofViolatedConstraints=0;
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
			//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
			MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
			for (Constraint constraint:syblSpecification.getConstraint()){
				boolean evaluate = true;
				if (constraint.getCondition()!=null){
					for (BinaryRestriction binaryRestriction:constraint.getCondition().getBinaryRestriction()){
						float currentLeftValue=0;
						float currentRightValue = 0;
						if (binaryRestriction.getLeftHandSide().getMetric()!=null){
							String metric = binaryRestriction.getLeftHandSide().getMetric();
							try{
							currentLeftValue = monitoredEntity.getMonitoredValue(metric);
							}catch(Exception e){
								SYBLDirectivesEnforcementLogger.logger.error(monitoredEntity.getId()+" "+" "+" searching value for metric "+ metric);
							}
							if (currentLeftValue<0){
								if (monitoredEntity.getMonitoredVar(metric)!=null)
								currentLeftValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
								else currentRightValue=0;
							}
							currentRightValue=Float.parseFloat(binaryRestriction.getRightHandSide().getNumber());
						}else
						if (binaryRestriction.getRightHandSide().getMetric()!=null){
							String metric = binaryRestriction.getRightHandSide().getMetric();
							currentRightValue = monitoredEntity.getMonitoredValue(metric);
							//System.out.println("Current value for metric is  "+ currentRightValue);
							if (currentRightValue<0){
								if (monitoredEntity.getMonitoredVar(metric)!=null)
								currentRightValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
								else currentRightValue=0;
							}
							currentLeftValue=Float.parseFloat(binaryRestriction.getLeftHandSide().getNumber());
						}
						switch (binaryRestriction.getType()){
						case "lessThan":
							if (currentLeftValue>=currentRightValue){
								evaluate=false;
							}
							break;
						case "greaterThan":
							if (currentLeftValue<=currentRightValue){
								evaluate=false;
							}
							break;
						case "lessThanOrEqual":
							if (currentLeftValue>currentRightValue){
								evaluate=false;
							}
							break;
						case "greaterThanOrEqual":
							if (currentLeftValue<currentRightValue){
								evaluate=false;
							}
							break;
						case "differentThan":
							if (currentLeftValue==currentRightValue){
								evaluate=false;
							}
							break;
						case "equals":
							if (currentLeftValue!=currentRightValue){
								evaluate=false;
							}
							break;
						default:
							if (currentLeftValue>=currentRightValue){
								evaluate=false;
							}
							break;
						}
					}				
					
				}
				if (evaluate)
				for (BinaryRestriction binaryRestriction:constraint.getToEnforce().getBinaryRestriction()){
					float currentLeftValue=0;
					float currentRightValue = 0;
					if (binaryRestriction.getLeftHandSide().getMetric()!=null){
						String metric = binaryRestriction.getLeftHandSide().getMetric();
						currentLeftValue = monitoredEntity.getMonitoredValue(metric);
						if (currentLeftValue<0){
							if (monitoredEntity.getMonitoredVar(metric)!=null)
							currentLeftValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
							else currentRightValue=0;
						}
						currentRightValue=Float.parseFloat(binaryRestriction.getRightHandSide().getNumber());
					}else
					if (binaryRestriction.getRightHandSide().getMetric()!=null){
						String metric = binaryRestriction.getRightHandSide().getMetric();
						currentRightValue = monitoredEntity.getMonitoredValue(metric);
						//System.out.println("Current value for metric is  "+ currentRightValue);
						if (currentRightValue<0){
							if (monitoredEntity.getMonitoredVar(metric)!=null)
							currentRightValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
							else currentRightValue=0;
						}
						currentLeftValue=Float.parseFloat(binaryRestriction.getLeftHandSide().getNumber());
					}
					switch (binaryRestriction.getType()){
					case "lessThan":
						if (currentLeftValue>=currentRightValue){
							//PlanningLogger.logger.info("Violated constraint "+constraint.getId());
							numberofViolatedConstraints=numberofViolatedConstraints+1;
						}
						break;
					case "greaterThan":
						if (currentLeftValue<=currentRightValue){
							//PlanningLogger.logger.info("Violated constraint "+constraint.getId());
							numberofViolatedConstraints=numberofViolatedConstraints+1;
						}
						break;
					case "lessThanOrEqual":
						if (currentLeftValue>currentRightValue){
						//	PlanningLogger.logger.info("Violated constraint "+constraint.getId());
							numberofViolatedConstraints=numberofViolatedConstraints+1;
						}
						break;
					case "greaterThanOrEqual":
						if (currentLeftValue<currentRightValue){
							//PlanningLogger.logger.info("Violated constraint "+constraint.getId());

							numberofViolatedConstraints=numberofViolatedConstraints+1;
						}
						break;
					case "differentThan":
						if (currentLeftValue==currentRightValue){
						//	PlanningLogger.logger.info("Violated constraint "+constraint.getId());
							numberofViolatedConstraints=numberofViolatedConstraints+1;
						}
						break;
					case "equals":
						if (currentLeftValue!=currentRightValue){
							//PlanningLogger.logger.info("Violated constraint "+constraint.getId());
							numberofViolatedConstraints=numberofViolatedConstraints+1;
						}
						break;
					default:
						if (currentLeftValue>=currentRightValue){
							//PlanningLogger.logger.info("Violated constraint "+constraint.getId());
							numberofViolatedConstraints=numberofViolatedConstraints+1;
						}
						break;
					}
				}
			}
		}
		//PlanningLogger.logger.info("Number of violated constraints"+ numberofViolatedConstraints);
		return numberofViolatedConstraints;
	}
}
