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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MonitoredComponentTopology extends MonitoredEntity {
	private String id;
	public HashMap<String,Float> monitoredData=new HashMap<String,Float>();
	public ArrayList<MonitoredComponent> monitoredComponents = new ArrayList<MonitoredComponent>();
	public ArrayList<MonitoredComponentTopology> monitoredComponentTopologies= new ArrayList<MonitoredComponentTopology>();
	public HashMap<String,String> monitoredVariables = new HashMap<String,String>();


	public void setMonitoredValue(String data, Float value){
		monitoredData.put(data,value);
	}
	public Float getMonitoredValue(String data){
		return monitoredData.get(data);
	}	
	public void addMonitoredTopology(MonitoredComponentTopology componentTopology){
		monitoredComponentTopologies.add(componentTopology);
	}
	public List<MonitoredComponentTopology> getMonitoredTopologies(){
		return monitoredComponentTopologies;
	}
	public void addMonitoredComponent(MonitoredComponent componentTopology){
		monitoredComponents.add(componentTopology);
	}
	public List<MonitoredComponent> getMonitoredComponents(){
		return monitoredComponents;
	}
	public Collection<String> getMonitoredMetrics(){
		return monitoredData.keySet();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setMonitoredVar(String data, String value){
		monitoredVariables.put(data,value);
	}
	public String getMonitoredVar(String data){
		return monitoredVariables.get(data);
	}
}
