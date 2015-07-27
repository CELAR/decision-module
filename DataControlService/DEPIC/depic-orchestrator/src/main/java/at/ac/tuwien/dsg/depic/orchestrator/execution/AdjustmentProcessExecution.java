/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.orchestrator.execution;

import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.AdjustmentProcess;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ExternalServiceRequest;
import at.ac.tuwien.dsg.depic.common.entity.runtime.MonitoringSession;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Parameter;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;
import at.ac.tuwien.dsg.depic.common.utils.Logger;
import at.ac.tuwien.dsg.depic.common.utils.RestfulWSClient;
import at.ac.tuwien.dsg.depic.orchestrator.registry.ElasticServiceRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Jun
 */
public class AdjustmentProcessExecution {

    List<ElasticState> listOfExpectedElasticStates;
    List<AdjustmentProcess> listOfAdjustmentProcesses;
    List<String> traversedGatewayList;
    DBType eDaaSType;
    MonitoringSession monitoringSession;

    public AdjustmentProcessExecution() {
    }
    
    

    public AdjustmentProcessExecution(List<ElasticState> listOfExpectedElasticStates, List<AdjustmentProcess> listOfAdjustmentProcesses, MonitoringSession monitoringSession, DBType eDaaSType) {
        this.listOfExpectedElasticStates = listOfExpectedElasticStates;
        this.listOfAdjustmentProcesses = listOfAdjustmentProcesses;
        traversedGatewayList = new ArrayList<String>();
        this.monitoringSession = monitoringSession;
        this.eDaaSType = eDaaSType;
    }

    public void startControlElasticState(ElasticState currentElasticState) {

        List<AdjustmentProcess> listOfPotentialControlProcesses = new ArrayList<AdjustmentProcess>();

        for (ElasticState expectedElasticState : listOfExpectedElasticStates) {

            AdjustmentProcess cp = determineAppropriateControlProcess(expectedElasticState.geteStateID());
            if (cp != null) {
                Logger.logInfo("Potential Next eState FOUND");
                listOfPotentialControlProcesses.add(cp);
            }

        }

        AdjustmentProcess adjustmentProcess = determineTheBestControlProcess(listOfPotentialControlProcesses);
        Logger.logInfo("BEST ADJUSTMENT PROCESS FOUND");
        Logger.logInfo("FINAL Elastic STATE");

        String log = "";
        log += "FINAL Elastic STATE: " + adjustmentProcess.getFinalEState().geteStateID();

        logElasticState(adjustmentProcess.getFinalEState());

        long t1 = System.currentTimeMillis();

        startControlProcess(adjustmentProcess);

        long t2 = System.currentTimeMillis();

        System.err.println("ADJUSTMENT_PROCESS_RUNTIME: " + monitoringSession.getSessionID() + "  -  " + (t2 - t1));
        log = log + "ADJUSTMENT_PROCESS_RUNTIME: " + monitoringSession.getSessionID() + "  -  " + (t2 - t1);
        System.err.println("\n" + log);

    }

    private AdjustmentProcess determineAppropriateControlProcess(String elasticStateID_j) {

        AdjustmentProcess cp = null;
        for (AdjustmentProcess controlProcess : listOfAdjustmentProcesses) {
            if (controlProcess.getFinalEState().geteStateID().equals(elasticStateID_j)) {
                cp = controlProcess;
            }
        }

        return cp;
    }

    private AdjustmentProcess determineTheBestControlProcess(List<AdjustmentProcess> potentialControlProcesses) {

        int minControlActions = Integer.MAX_VALUE;
        int cpIndex = 0;
        for (AdjustmentProcess cp : potentialControlProcesses) {
            if (cp.getListOfAdjustmentActions().size() < minControlActions) {
                minControlActions = cp.getListOfAdjustmentActions().size();
                cpIndex = potentialControlProcesses.indexOf(cp);
            }

        }

        return potentialControlProcesses.get(cpIndex);
    }

    public void startControlProcess(AdjustmentProcess cp) {

        Logger.logInfo("EXECUTE Control Process ...");
        executeControlProcess(cp);

    }

    private void executeControlProcess(AdjustmentProcess cp) {

        List<AdjustmentAction> listOfControlActions = cp.getListOfAdjustmentActions();

        for (AdjustmentAction controlAction : listOfControlActions) {

            List<Parameter> listOfParams = new ArrayList<Parameter>();
            Parameter p = new Parameter("attributeIndex", "int", "3");
            listOfParams.add(p);
            ExternalServiceRequest controlRequest = new ExternalServiceRequest(monitoringSession.getEdaasName(), monitoringSession.getSessionID(), monitoringSession.getDataAssetID(), listOfParams);

            String requestXML = "";
            try {
                requestXML = JAXBUtils.marshal(controlRequest, ExternalServiceRequest.class);
            } catch (JAXBException ex) {
                System.err.println(ex);
            }

            String uri = "";

            do {

                uri = ElasticServiceRegistry.getElasticServiceURI(controlAction.getActionName(), eDaaSType);
                if (uri.equals("")) {
                    Logger.logInfo("Waiting_for_Active_Elastic_Serivce ... " + monitoringSession.getSessionID() + " - " + controlAction.getActionName());
                } else {
                    Logger.logInfo("Ready_Service: " + monitoringSession.getSessionID() + " - " + uri);
                    ElasticServiceRegistry.occupyElasticService(uri);
                }

                try {
                    Thread.sleep(10000);

                } catch (InterruptedException ex) {
                    System.err.println(ex);
                }

            } while (uri.equals(""));

            Logger.logInfo("RUN: " + uri);
            RestfulWSClient ws = new RestfulWSClient(uri);
            ws.callPutMethod(requestXML);

            ElasticServiceRegistry.releaseElasticService(uri);

        }

    }

    private boolean isTraversed(String gatewayID) {
        boolean rs = false;

        for (String traversedPG : traversedGatewayList) {
            if (traversedPG.equals(gatewayID)) {
                rs = true;
            }
        }

        return rs;
    }

    private void logElasticState(ElasticState elasticState) {

        Logger.logInfo("\n***");
        Logger.logInfo("eState ID: " + elasticState.geteStateID());
        List<MetricCondition> conditions = elasticState.getListOfConditions();
        for (MetricCondition condition : conditions) {
            Logger.logInfo("\n---");
            System.err.print("    metric: " + condition.getMetricName());
            System.err.print("    id: " + condition.getConditionID());
            System.err.print("    lower: " + condition.getLowerBound());
            System.err.print("    uppper: " + condition.getUpperBound());

        }

    }

}
