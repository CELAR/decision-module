/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.depic.orchestrator.execution;


import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.eda.ElasticDataAsset;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.AdjustmentProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DataElasticityManagementProcess;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MonitoringAction;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.MonitoringProcess;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.PrimitiveActionMetadata;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlAction;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DaaSDescription;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DepicDescription;
import at.ac.tuwien.dsg.depic.common.entity.runtime.MonitoringSession;

import at.ac.tuwien.dsg.depic.common.entity.runtime.MonitoringMetric;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.DepicDesciptionImporter;

import at.ac.tuwien.dsg.depic.common.utils.IOUtils;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;
import at.ac.tuwien.dsg.depic.common.utils.Logger;
import at.ac.tuwien.dsg.depic.common.utils.RestfulWSClient;

import at.ac.tuwien.dsg.depic.orchestrator.execution.AdjustmentProcessExecution;
import at.ac.tuwien.dsg.depic.orchestrator.elasticityprocessesstore.ElasticityProcessesStore;

import at.ac.tuwien.dsg.depic.orchestrator.registry.ElasticServiceRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;


/**
 *
 * @author Jun
 */
public class DataElasticityMonitor{

    List<MonitoringMetric> listOfMonitoringMetrics;
    List<ElasticState> listOfElasticStates;
    List<ElasticState> listOfExpectedElasticStates;
    MonitoringSession monitoringSession;
    MonitoringProcess monitorProcess;
    List<AdjustmentProcess> listOfAdjustmentProcess;
    DBType eDaaSType;
    PrimitiveActionMetadata primitiveActionMetadata;
 

    public DataElasticityMonitor(MonitoringSession monitoringSession) {
        this.monitoringSession = monitoringSession;
        listOfMonitoringMetrics = new ArrayList<MonitoringMetric>();
        config();
    }
     

    public void startMonitoringService() {
        List<MonitoringAction> listOfMonitoringActions = monitorProcess.getListOfMonitoringActions();

        Logger.logInfo("Execute Monitoring Process ...");

        long t1 = System.currentTimeMillis();

        for (MonitoringAction monitorAction : listOfMonitoringActions) {

            String monitoringServiceName = monitorAction.getMonitoringActionName();
            Logger.logInfo("Run Monitoring Service ID: " + monitoringServiceName);

            Configuration cfg = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            
            String metricName = getMonitoringMetricName(monitoringServiceName);
            
            RestfulWSClient ws = new RestfulWSClient("");
            String metricID = ws.callJcatascopiaMetricWS(metricName,cfg.getConfig("JCATASCOPIA.ALL.METRICS.REST"));
            
            
            String metricValue = ws.getJCMetricValue(metricID,cfg.getConfig("JCATASCOPIA.METRIC.VALUE.REST"));

            System.out.println("metric name: " + metricName);
            System.out.println("metric value: " + metricValue);
            
            double monitoringValue = Double.parseDouble(metricValue);

            MonitoringMetric monitoringMetric = new MonitoringMetric(metricName, monitoringValue);
            System.out.println("Monitoring Value String: " + monitoringMetric.getMetricValue());

            listOfMonitoringMetrics.add(monitoringMetric);

        }

       
        Logger.logInfo("MONITORING RESULT: "+monitoringSession.getSessionID()+" \n");
        String log = System.currentTimeMillis() +"\t"+monitoringSession.getSessionID() + "\t"+ monitoringSession.getDataAssetID()+"\t";
        
        for (MonitoringMetric monitoringMetric : listOfMonitoringMetrics) {

            Logger.logInfo("Metric: " + monitoringMetric.getMetricName() + " - Value: " + monitoringMetric.getMetricValue());
            log = log + monitoringMetric.getMetricValue() + "\t";
            
        }

        ElasticState currentElasticState = determineCurrentElasticState();

        if (currentElasticState == null) {
            
                Logger.logInfo("FAIL VALIDATION");
                log  = log + "FAIL" + "\t";
                AdjustmentProcessExecution controller = new AdjustmentProcessExecution(listOfElasticStates, listOfAdjustmentProcess, monitoringSession, eDaaSType);
                controller.startControlElasticState(currentElasticState);
            
        } else {
            Logger.logInfo("PASS VALIDATION");
                log = log + "PASS" + "\t";
            Logger.logInfo("Current Elastic State ...");

            logElasticState(currentElasticState);
            
        }
         long t2 = System.currentTimeMillis();
         Logger.logInfo("MONITORING_RUNTIME: " + (t2 - t1));
            log = log +(t2 - t1) + "\n";
        
        try {

            IOUtils iou = new IOUtils("/home/ubuntu/log");
            iou.writeData(log, "depic_monitor.xml");

            System.out.println("\n" + log);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataElasticityMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private ElasticState determineCurrentElasticState() {
        ElasticState currentElasticState = null;
        Logger.logInfo("Determine Current ElasticState");
        Logger.logInfo("NoOf EStates: " + listOfElasticStates.size());

        for (ElasticState elasticState : listOfElasticStates) {

            List<MetricCondition> conditions = elasticState.getListOfConditions();
            boolean rs = true;
            for (MetricCondition condition : conditions) {
                String metricName = condition.getMetricName();

       
                    double metricValue = findMetricValue(metricName);

                    Logger.logInfo("Metric: " + metricName + " - Value: " + metricValue);
                    Logger.logInfo("Lower Bound: " + condition.getLowerBound() + " - upper bound: " + condition.getUpperBound());

                    if (!(metricValue >= condition.getLowerBound() && metricValue <= condition.getUpperBound())) {
                        rs = false;
                    }
                
            }
            
            if (rs){
                currentElasticState = elasticState;
                break;
            }
            
        }
        return currentElasticState;   
    }
    
    private double findMetricValue(String metricName){
        double metricValue=0;
        
        for (MonitoringMetric monitoringMetric : listOfMonitoringMetrics) {
            if (monitoringMetric.getMetricName().equals(metricName)){
                metricValue = monitoringMetric.getMetricValue();
            }
        }
        
        return metricValue;
    }
 
    private boolean isExpectedElasticState(ElasticState elasticState) {
        
        boolean rs= false;
        
        for (ElasticState finalEState: listOfElasticStates){
            if (elasticState.geteStateID().equals(finalEState.geteStateID())){
                rs=true;
                break;
            }
        }
        
        return rs;
    }
    
    private boolean associateWithResourceControlAction(String metricName){
        boolean rs = false;
        List<ResourceControlAction> listOfResourceControlActions = primitiveActionMetadata.getListOfResourceControls();
       
        for (ResourceControlAction rca: listOfResourceControlActions){
            if (rca.getAssociatedQoRMetric().equals(metricName)){
                rs=true;
            }
        }
        
        return rs;
    }
  
    private void config(){
        
        DepicDesciptionImporter desciptionImporter = new DepicDesciptionImporter();
        DepicDescription depicDescription = desciptionImporter.importDescription(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        DaaSDescription daaSDescription = depicDescription.getDaaSDescription();
        
        Configuration cfg = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        
        
        ElasticityProcessesStore elasticityProcessesStore = new ElasticityProcessesStore(); 
        ElasticDataAsset eda = elasticityProcessesStore.getElasticDataAsset(daaSDescription.getDaasName());
        
        DataElasticityManagementProcess elasticityProcess= eda.getElasticProcess();
   
        eDaaSType = daaSDescription.getdBType();
        
        listOfElasticStates = eda.getListOfFinalElasticState();
       
       monitorProcess = elasticityProcess.getMonitoringProcess();
        listOfAdjustmentProcess = elasticityProcess.getListOfAdjustmentProcesses();
        
      
        
        
        primitiveActionMetadata = depicDescription.getPrimitiveActionMetadata();

    }
    
    private void mappingExpectedEStateIDs(List<String> expectElasticStateIDs){
        
        listOfExpectedElasticStates = new ArrayList<ElasticState>();
        
        for (String eStateID : expectElasticStateIDs){
            ElasticState elasticState = findElasticStateWithID(eStateID);
            listOfExpectedElasticStates.add(elasticState);
            
        }
        
        
    }
    
    
    private ElasticState findElasticStateWithID(String elasticStateID){
        
        for (ElasticState elasticState: listOfElasticStates){
            
            if (elasticState.geteStateID().equals(elasticStateID)){
                return elasticState;
            }
        }
        return null;
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
    
    
    public String getMonitoringMetricName(String actionID){
        String metricName="";
      
        List<MonitoringAction> listOfMonitoringActions = primitiveActionMetadata.getListOfMonitoringActions();

        
        for (MonitoringAction ma: listOfMonitoringActions) {
            if(ma.getMonitoringActionName().equals(actionID)){
                metricName = ma.getAssociatedQoRMetric();
                break;
            }
        }
        return metricName;
    }
    
    public void getJcatascopiaMetricID(String metricName){
        
        
        
    }
    
    
    public void getAvailableMetrics(){
        
        
    }
 
    
}
