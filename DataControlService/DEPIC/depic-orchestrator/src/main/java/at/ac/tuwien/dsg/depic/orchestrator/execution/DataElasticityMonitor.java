/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184. This work was partially supported by the European Commission in terms
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


public class DataElasticityMonitor {

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
            
            String agentID = ws.callJcatascopiaAgentIDWS(
                    cfg.getConfig("AGENT.IP"), 
                    cfg.getConfig("JCATASCOPIA.AGENT.ID.REST"));
            Logger.logInfo("Agent ID: " + agentID);
            
            
            String metricID = ws.callJcatascopiaMetricWS(metricName, cfg.getConfig("JCATASCOPIA.ALL.METRICS.REST"), agentID);
            Logger.logInfo("Metric ID: " + metricID);
            

            String metricValue = ws.getJCMetricValue(metricID, cfg.getConfig("JCATASCOPIA.METRIC.VALUE.REST"));


            double monitoringValue = Double.parseDouble(metricValue);

            MonitoringMetric monitoringMetric = new MonitoringMetric(metricName, monitoringValue);
            System.out.println("Monitoring Value String: " + monitoringMetric.getMetricValue());

            listOfMonitoringMetrics.add(monitoringMetric);

        }

        Logger.logInfo("MONITORING RESULT: " + monitoringSession.getSessionID() + " \n");
        String log = System.currentTimeMillis() + "\t" + monitoringSession.getSessionID() + "\t" + monitoringSession.getDataAssetID() + "\t";

        for (MonitoringMetric monitoringMetric : listOfMonitoringMetrics) {

            Logger.logInfo("Metric: " + monitoringMetric.getMetricName() + " - Value: " + monitoringMetric.getMetricValue());
            log = log + monitoringMetric.getMetricValue() + "\t";

        }

        ElasticState currentElasticState = determineCurrentElasticState();

        if (currentElasticState == null) {

            Logger.logInfo("FAIL VALIDATION");
            log = log + "FAIL" + "\t";
//            AdjustmentProcessExecution controller = new AdjustmentProcessExecution(listOfElasticStates, listOfAdjustmentProcess, monitoringSession, eDaaSType);
//            controller.startControlElasticState(currentElasticState);
            
            ProcessExecutor processExecutor = new ProcessExecutor(listOfElasticStates, listOfAdjustmentProcess, monitoringSession, currentElasticState);
            processExecutor.start();
            
            

        } else {
            Logger.logInfo("PASS VALIDATION");
            log = log + "PASS" + "\t";
            Logger.logInfo("Current Elastic State ...");

        }
        long t2 = System.currentTimeMillis();
        Logger.logInfo("MONITORING_RUNTIME: " + (t2 - t1));
        log = log + (t2 - t1) + "\n";

        try {

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

            if (rs) {
                currentElasticState = elasticState;
                break;
            }

        }
        return currentElasticState;
    }

    private double findMetricValue(String metricName) {
        double metricValue = 0;

        for (MonitoringMetric monitoringMetric : listOfMonitoringMetrics) {
            if (monitoringMetric.getMetricName().equals(metricName)) {
                metricValue = monitoringMetric.getMetricValue();
            }
        }

        return metricValue;
    }

    private boolean isExpectedElasticState(ElasticState elasticState) {

        boolean rs = false;

        for (ElasticState finalEState : listOfElasticStates) {
            if (elasticState.geteStateID().equals(finalEState.geteStateID())) {
                rs = true;
                break;
            }
        }

        return rs;
    }

    private boolean associateWithResourceControlAction(String metricName) {
        boolean rs = false;
        List<ResourceControlAction> listOfResourceControlActions = primitiveActionMetadata.getListOfResourceControls();

        for (ResourceControlAction rca : listOfResourceControlActions) {
            if (rca.getAssociatedQoRMetric().equals(metricName)) {
                rs = true;
            }
        }

        return rs;
    }

    private void config() {

        DepicDesciptionImporter desciptionImporter = new DepicDesciptionImporter();
        DepicDescription depicDescription = desciptionImporter.importDescription(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        DaaSDescription daaSDescription = depicDescription.getDaaSDescription();

        Configuration cfg = new Configuration(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        ElasticityProcessesStore elasticityProcessesStore = new ElasticityProcessesStore();
        ElasticDataAsset eda = elasticityProcessesStore.getElasticDataAsset(daaSDescription.getDaasName());

        DataElasticityManagementProcess elasticityProcess = eda.getElasticProcess();

        eDaaSType = daaSDescription.getdBType();

        listOfElasticStates = eda.getListOfFinalElasticState();

        monitorProcess = elasticityProcess.getMonitoringProcess();
        listOfAdjustmentProcess = elasticityProcess.getListOfAdjustmentProcesses();

        primitiveActionMetadata = depicDescription.getPrimitiveActionMetadata();
        
        monitoringSession.setDataAssetID(daaSDescription.getDaasName());
        monitoringSession.setSessionID(daaSDescription.getDaasName());
        monitoringSession.seteDaaSType(daaSDescription.getdBType());
        monitoringSession.setEdaasName(daaSDescription.getDaasName());

    }

    
    private ElasticState findElasticStateWithID(String elasticStateID) {

        for (ElasticState elasticState : listOfElasticStates) {

            if (elasticState.geteStateID().equals(elasticStateID)) {
                return elasticState;
            }
        }
        return null;
    }

   

    public String getMonitoringMetricName(String actionID) {
        String metricName = "";

        List<MonitoringAction> listOfMonitoringActions = primitiveActionMetadata.getListOfMonitoringActions();

        for (MonitoringAction ma : listOfMonitoringActions) {
            if (ma.getMonitoringActionName().equals(actionID)) {
                metricName = ma.getAssociatedQoRMetric();
                break;
            }
        }
        return metricName;
    }

    public void getJcatascopiaMetricID(String metricName) {

    }

    public void getAvailableMetrics() {

    }

}
