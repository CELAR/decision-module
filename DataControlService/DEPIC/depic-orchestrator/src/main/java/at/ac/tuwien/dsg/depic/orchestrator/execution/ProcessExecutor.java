/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 E184.  This work was partially supported by the European Commission in terms
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

import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.AdjustmentProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;

import at.ac.tuwien.dsg.depic.common.entity.runtime.ExecutionSession;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ExecutionStep;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ExternalServiceRequest;
import at.ac.tuwien.dsg.depic.common.entity.runtime.MonitoringSession;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;
import at.ac.tuwien.dsg.depic.common.utils.RestfulWSClient;
import at.ac.tuwien.dsg.depic.orchestrator.registry.ElasticServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;

public class ProcessExecutor implements Runnable{
    
    private Thread t;
    private ExecutionSession executionSession;
    private List<ElasticState> listOfExpectedElasticStates;
    private List<AdjustmentProcess> listOfAdjustmentProcesses;
    private MonitoringSession monitoringSession;
    private ElasticState currentElasticState;
    
    
    public ProcessExecutor(List<ElasticState> listOfExpectedElasticStates, List<AdjustmentProcess> listOfAdjustmentProcesses, MonitoringSession monitoringSession,ElasticState currentElasticState) {
       
        this.listOfExpectedElasticStates = listOfExpectedElasticStates;
        this.listOfAdjustmentProcesses = listOfAdjustmentProcesses;
        this.monitoringSession = monitoringSession;
        this.currentElasticState = currentElasticState;
    }
    
    public void run() {

        AdjustmentProcess adjustmentProcess = findAppropriateAdjustmentProcess();
        
        DEPExecutionPlanning depep = new DEPExecutionPlanning(adjustmentProcess);
        List<ExecutionStep> executionPlan = depep.planningExecution();
        
        executionSession = new ExecutionSession(monitoringSession, executionPlan, adjustmentProcess);
        
        
        
        boolean isFinished = false;
        DEPExecutionEngine.addExecutionSession(executionSession);


        do {
            
            DEPExecutionEngine.checkExecution(executionSession.getMonitoringSession().getSessionID());
            
            isFinished = DEPExecutionEngine.isFinished(executionSession.getMonitoringSession().getSessionID());
            
            
            
            
            try {
                Thread.sleep(2000);

            } catch (InterruptedException ex) {

            }
        } while (!isFinished);
        
        System.out.println("PROCESS EXECUTION FINISHED !!!");
    }

    public void start() {

        if (t == null) {
            t = new Thread(this, "");
            t.start();
        }
    
    }
    
    
    
    
    ///////
    ///////
    //////
    
    private AdjustmentProcess findAppropriateAdjustmentProcess() {

        List<AdjustmentProcess> listOfPotentialControlProcesses = new ArrayList<AdjustmentProcess>();

        for (ElasticState expectedElasticState : listOfExpectedElasticStates) {

            AdjustmentProcess cp = determineAppropriateControlProcess(expectedElasticState.geteStateID());
            if (cp != null) {
                
                listOfPotentialControlProcesses.add(cp);
            }

        }

        AdjustmentProcess adjustmentProcess = determineTheBestControlProcess(listOfPotentialControlProcesses);


     
     
//
//        long t1 = System.currentTimeMillis();
//
//        startControlProcess(adjustmentProcess);
//
//        long t2 = System.currentTimeMillis();

       
       return adjustmentProcess;
    

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

      
        executeControlProcess(cp);

    }

    private void executeControlProcess(AdjustmentProcess cp) {

        List<AdjustmentAction> listOfControlActions = cp.getListOfAdjustmentActions();

        for (AdjustmentAction controlAction : listOfControlActions) {

            ExternalServiceRequest controlRequest = new ExternalServiceRequest(monitoringSession.getEdaasName(), monitoringSession.getSessionID(), monitoringSession.getDataAssetID(), null);

            String requestXML = "";
            try {
                requestXML = JAXBUtils.marshal(controlRequest, ExternalServiceRequest.class);
            } catch (JAXBException ex) {
                System.err.println(ex);
            }

            String uri = "";

            do {

                uri = ElasticServiceRegistry.getElasticServiceURI(controlAction.getActionName(), monitoringSession.geteDaaSType());
                if (uri.equals("")) {
                  //  Logger.logInfo("Waiting_for_Active_Elastic_Serivce ... " + monitoringSession.getSessionID() + " - " + controlAction.getActionName());
                } else {
                   // Logger.logInfo("Ready_Service: " + monitoringSession.getSessionID() + " - " + uri);
                    ElasticServiceRegistry.occupyElasticService(uri);
                }

                try {
                    Thread.sleep(10000);

                } catch (InterruptedException ex) {
                    System.err.println(ex);
                }

            } while (uri.equals(""));

            //Logger.logInfo("RUN: " + uri);
            RestfulWSClient ws = new RestfulWSClient(uri);
            ws.callPutMethod(requestXML);

            ElasticServiceRegistry.releaseElasticService(uri);

        }

    }

   


    
    
}
