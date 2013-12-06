/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.                 This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).

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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api;

import java.util.ArrayList;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;



public interface MonitoringAPIInterface {
    
	public Double getCurrentCPUSize(Node e)  ;
    
	public Double getCostPerHour(Node e)  ;
    	
	public Double getCurrentRAMSize(Node e) ;
    
   
    public Double getCurrentMemUsage(Node e);
    
	public Double getTotalCostSoFar(Node e) ;

    
	public Node getControlledService() ;
    
	public void setControlledService(Node controlledService) ;
    
	public Double getCurrentReadLatency(Node e)  ;
    
	public Double getCurrentReadCount(Node e)  ;
    
    public Double getCurrentWriteLatency(Node e)  ;
    
	public Double getCurrentWriteCount(Node e)  ;
    public Double getMetricValue(String metricName, Node e);
	public Double getCurrentCPUUsage(Node e) ;
    public void submitElasticityRequirements(ArrayList<ElasticityRequirement> description);
	public Double getCurrentHDDSize(Node e)  ; 
    
	public void scaleinstarted(Node arg0);
	public void scaleinended(Node arg0);

    
	public void scaleoutstarted(Node arg0);
	public void scaleoutended(Node arg0);
   	
	public Double getCurrentLatency(Node arg0)  ;

    
	public Double getCurrentOperationCount(Node arg0);

    
	public Double getCurrentHDDUsage(Node e) ;
    public void enforcingActionStarted(String actionName, Node e);
    public void enforcingActionEnded(String actionName, Node e);

    public Double getNumberInstances(Node e);
    public void refreshServiceStructure(Node cloudService);
}
