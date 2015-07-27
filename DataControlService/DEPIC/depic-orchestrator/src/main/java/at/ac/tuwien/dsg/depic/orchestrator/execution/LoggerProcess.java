/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.orchestrator.execution;

import at.ac.tuwien.dsg.depic.common.entity.eda.ElasticDataAsset;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.AdjustmentProcess;

import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.MonitoringProcess;
import at.ac.tuwien.dsg.depic.common.utils.Logger;
import at.ac.tuwien.dsg.depic.orchestrator.elasticityprocessesstore.ElasticityProcessesStore;
import java.util.List;

/**
 *
 * @author Jun
 */
public class LoggerProcess {
   
    List<ElasticState> listOfInitialState;
    List<ElasticState> listOfFinalState;
    MonitoringProcess monitorProcess;
    List<AdjustmentProcess> listOfControlProcesses;

    
    public void config(){
        ElasticityProcessesStore elasticityProcessesStore = new ElasticityProcessesStore(); 
        ElasticDataAsset eda = elasticityProcessesStore.getElasticDataAsset("daf2");
       listOfFinalState = eda.getListOfFinalElasticState();
        
        Logger.logInfo("FINAL ESTATE SET ------- \n");
        for (ElasticState  estate : listOfFinalState){
           
            logElasticState(estate);
        }
        
        
        
        Logger.logInfo("CONTROL PROCESSES LIST ------ \n");
        for (AdjustmentProcess cp : listOfControlProcesses){
            logControlProcesses(cp);
        }
        
        
    
    }
    
    
     private void logElasticState(ElasticState elasticState ){
  
            Logger.logInfo("\n***"); 
            Logger.logInfo("eState ID: " + elasticState.geteStateID());
            List<MetricCondition> conditions = elasticState.getListOfConditions();
            for(MetricCondition condition : conditions){
                Logger.logInfo("\n---"); 
                System.out.print("    metric: " + condition.getMetricName());
                System.out.print("    id: " + condition.getConditionID());
                System.out.print("    lower: " + condition.getLowerBound());
                System.out.print("    uppper: " + condition.getUpperBound());
                
            
            
        }
        
    }
    
     
     public void logControlProcesses(AdjustmentProcess controlProcess) {

        Logger.logInfo("\nLOG CONTROL PROCESS");

        Logger.logInfo("\n***");
        List<AdjustmentAction> listOfControlActions = controlProcess.getListOfAdjustmentActions();

        Logger.logInfo("PROCESS ----------- ");
   
        Logger.logInfo("eState fi: " + controlProcess.getFinalEState().geteStateID());
        for (AdjustmentAction controlAction : listOfControlActions) {
            Logger.logInfo("control action: " + controlAction.getActionName());
        }

        Logger.logInfo(".............");

        List<MetricCondition> conditions_in = controlProcess.getFinalEState().getListOfConditions();
        for (MetricCondition c : conditions_in) {
            Logger.logInfo("   id: " + c.getConditionID());
            Logger.logInfo("   metric: " + c.getMetricName());
            Logger.logInfo("   lower: " + c.getLowerBound());
            Logger.logInfo("   upper: " + c.getUpperBound());

        }

        Logger.logInfo("eState fi: " + controlProcess.getFinalEState().geteStateID());
        List<MetricCondition> conditions_fi = controlProcess.getFinalEState().getListOfConditions();
        for (MetricCondition c : conditions_fi) {
            Logger.logInfo("   id: " + c.getConditionID());
            Logger.logInfo("   metric: " + c.getMetricName());
            Logger.logInfo("   lower: " + c.getLowerBound());
            Logger.logInfo("   upper: " + c.getUpperBound());

        }

        for (AdjustmentAction controlAction : listOfControlActions) {
            Logger.logInfo("control action: " + controlAction.getActionID());
            Logger.logInfo("control action: " + controlAction.getActionName());
        }
    }
    
}
