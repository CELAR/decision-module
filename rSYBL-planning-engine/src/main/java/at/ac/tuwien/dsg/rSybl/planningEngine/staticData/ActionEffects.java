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

package at.ac.tuwien.dsg.rSybl.planningEngine.staticData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;





public class ActionEffects {
	public static ActionEffect scaleOutEffectForCassandraDB = new ActionEffect();
//	public static ActionEffect scaleOutEffectForWebServer=new ActionEffect();

	public static ActionEffect scaleInEffectForCassandraDB = new ActionEffect();
//	public static ActionEffect scaleInEffectForWebServer= new ActionEffect();


	public static HashMap<String,List<ActionEffect>> getActionEffects(DependencyGraph dependencyGraph,MonitoringAPIInterface syblAPI){
		HashMap<String,List<ActionEffect>> actionEffects = new HashMap<String,List<ActionEffect>>();
		
		{
			scaleOutEffectForCassandraDB.setTargetedEntityID("CassandraNode");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -30.0f,"CassandraNode");
//			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -40.0f,"DataControllerServiceUnit");
//			scaleOutEffectForCassandraDB.setActionEffectForMetric("latency", -30.0f,"DataNodeServiceUnit");
//			scaleOutEffectForCassandraDB.setActionEffectForMetric("cost", 0.12f,"DataNodeServiceUnit");
			scaleOutEffectForCassandraDB.setActionName("scaleOutEffectForDataNode");
			scaleOutEffectForCassandraDB.setActionType("scaleout");

		}


//		{
//			scaleOutEffectForWebServer.setTargetedEntityID("EventProcessingServiceUnit") ; 
//			scaleOutEffectForWebServer.setActionEffectForMetric("cpu.usage", -40.0f,"EventProcessingServiceUnit");
//			scaleOutEffectForWebServer.setActionEffectForMetric("cost",0.12f,"EventProcessingServiceUnit");
//			scaleOutEffectForWebServer.setActionEffectForMetric("responseTime", -1000.0f,"EventProcessingServiceUnit");
//			scaleOutEffectForWebServer.setActionEffectForMetric("throughput", 1000f,"EventProcessingServiceUnit");
//			scaleOutEffectForWebServer.setActionName("scaleOutEffectForEventProcessingServiceUnit");
//			scaleOutEffectForWebServer.setActionType("scaleout");
//
//		}
//		{
//			scaleOutEffectForHadoopSlave.setTargetedEntityID("HadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionEffectForMetric("cpu.usage", -30.0f,"HadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionEffectForMetric("cost", 50.0f,"HadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionName("scaleOutEffectForHadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionType("scaleout");
//
//		}
		
		

		{
			scaleInEffectForCassandraDB.setTargetedEntityID("CassandraNode");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cpuUsage", 35.0f,"CassandraNode");
//			scaleInEffectForCassandraDB.setActionEffectForMetric("latency", 0.0001f,"DataNodeServiceUnit");
//			scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -0.12f,"DataNodeServiceUnit");
			scaleInEffectForCassandraDB.setActionName("scaleInEffectForDataNode");
			scaleInEffectForCassandraDB.setActionType("scalein");

		}


//		{
//			scaleInEffectForWebServer.setTargetedEntityID("EventProcessingServiceUnit") ; 
//			scaleInEffectForWebServer.setActionEffectForMetric("cpuUsage", 40.0f,"EventProcessingServiceUnit");
//			scaleInEffectForWebServer.setActionEffectForMetric("cost", 0.12f,"EventProcessingServiceUnit");
//			scaleInEffectForWebServer.setActionEffectForMetric("responseTime", 400.0f,"EventProcessingServiceUnit");
//			scaleInEffectForWebServer.setActionEffectForMetric("throughput", -1000.0f,"EventProcessingServiceUnit");
//			scaleInEffectForWebServer.setActionName("scaleInEffectForEventProcessingServiceUnit");
//			scaleInEffectForWebServer.setActionType("scalein");
//
//		}
//		{
//			scaleInEffectForHadoopSlave.setTargetedEntityID("HadoopSlave");
//			scaleInEffectForHadoopSlave.setActionEffectForMetric("cpu.usage", 30.0f,"HadoopSlave");
//			scaleInEffectForHadoopSlave.setActionEffectForMetric("cost", -50.0f,"HadoopSlave");
//			scaleInEffectForHadoopSlave.setActionName("scaleInEffectForHadoopSlave");
//			scaleInEffectForHadoopSlave.setActionType("scalein");
//		}
		 if (actionEffects.containsKey(scaleOutEffectForCassandraDB.getTargetedEntityID()))
			 actionEffects.get(scaleOutEffectForCassandraDB.getTargetedEntityID()).add(scaleOutEffectForCassandraDB);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleOutEffectForCassandraDB);
			 actionEffects.put(scaleOutEffectForCassandraDB.getTargetedEntityID(), l);
		 }
		 
//		 if (actionEffects.containsKey(scaleOutEffectForWebServer.getTargetedEntityID()))
//			 actionEffects.get(scaleOutEffectForWebServer.getTargetedEntityID()).add(scaleOutEffectForWebServer);
//		 else{
//			 List <ActionEffect > l = new ArrayList<ActionEffect>();
//			 l.add(scaleOutEffectForWebServer);
//			 actionEffects.put(scaleOutEffectForWebServer.getTargetedEntityID(), l);
//		 }
		 
//		 if (actionEffects.containsKey(scaleOutEffectForHadoopSlave.getTargetedEntityID()))
//			 actionEffects.get(scaleOutEffectForHadoopSlave.getTargetedEntityID()).add(scaleOutEffectForHadoopSlave);
//		 else{
//			 List <ActionEffect > l = new ArrayList<ActionEffect>();
//			 l.add(scaleOutEffectForHadoopSlave);
//			 actionEffects.put(scaleOutEffectForHadoopSlave.getTargetedEntityID(), l);
//		 }
		 
		 if (actionEffects.containsKey(scaleInEffectForCassandraDB.getTargetedEntityID()))
			 actionEffects.get(scaleInEffectForCassandraDB.getTargetedEntityID()).add(scaleInEffectForCassandraDB);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleInEffectForCassandraDB);
			 actionEffects.put(scaleInEffectForCassandraDB.getTargetedEntityID(), l);
		 }
		 
//		 if (actionEffects.containsKey(scaleInEffectForWebServer.getTargetedEntityID()))
//			 actionEffects.get(scaleInEffectForWebServer.getTargetedEntityID()).add(scaleInEffectForWebServer);
//		 else{
//			 List <ActionEffect > l = new ArrayList<ActionEffect>();
//			 l.add(scaleInEffectForWebServer);
//			 actionEffects.put(scaleInEffectForWebServer.getTargetedEntityID(), l);
//		 }
//	 if (actionEffects.containsKey(scaleInEffectForHadoopSlave.getTargetedEntityID()))
//			 actionEffects.get(scaleInEffectForHadoopSlave.getTargetedEntityID()).add(scaleInEffectForHadoopSlave);
//		 else{
//			 List <ActionEffect > l = new ArrayList<ActionEffect>();
//			 l.add(scaleInEffectForHadoopSlave);
//			 actionEffects.put(scaleInEffectForHadoopSlave.getTargetedEntityID(), l);
//		 }
		 

		for (Entry<String, List<ActionEffect>> e:actionEffects.entrySet()){
			for (ActionEffect ef:e.getValue()){
				
				ef.refreshMetricsForAboveLevels(dependencyGraph,syblAPI);
				
			}
		}
		return actionEffects;
	}
	
}
