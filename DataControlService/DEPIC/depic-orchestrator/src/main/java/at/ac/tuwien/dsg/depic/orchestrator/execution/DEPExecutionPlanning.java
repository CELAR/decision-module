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

import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.Action;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.AdjustmentProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DirectedAcyclicalGraph;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ParallelGateway;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ExecutionStep;
import java.util.ArrayList;
import java.util.List;

public class DEPExecutionPlanning {

    private AdjustmentProcess adjustmentProcess;
    private List<ExecutionStep> executionPlan;

    public DEPExecutionPlanning(AdjustmentProcess adjustmentProcess) {
        this.adjustmentProcess = adjustmentProcess;
        executionPlan = new ArrayList<ExecutionStep>();
    }

    

    public List<ExecutionStep> planningExecution() {

        //find start point
        DirectedAcyclicalGraph dag = adjustmentProcess.getDirectedAcyclicalGraph();
        List<Action> listOfActions = dag.getListOfActions();
        List<ParallelGateway> listOfParallelGateways = dag.getListOfParallelGateways();

        Action startAction = null;
        ParallelGateway startParallelGateway = null;

        for (Action action : listOfActions) {

            if (action.getIncomming() == null) {
                startAction = action;
            }

        }

        for (ParallelGateway parallelGateway : listOfParallelGateways) {
            if (parallelGateway.getIncomming().isEmpty()) {
                startParallelGateway = parallelGateway;
            }
        }

        // if start point is action => add this action to exectution plan
        // call recursive to get next step from outgoing; ending condition: 
        if (startAction != null) {

            ExecutionStep executionStepStart = new ExecutionStep();
            List<String> listOfPrerequisiteActions1 = new ArrayList<String>();
            List<String> listOfExecutionActions1 = new ArrayList<String>();

            listOfExecutionActions1.add(startAction.getActionID());
            executionStepStart.setListOfExecutionActions(listOfExecutionActions1);
            executionStepStart.setListOfPrerequisiteActions(listOfPrerequisiteActions1);

            executionPlan.add(executionStepStart);

            if (startAction.getOutgoing() != null) {

                if (isAction(startAction.getOutgoing())) {

                    ExecutionStep nextExecutionStep = new ExecutionStep();
                    List<String> listOfPrerequisiteActionsNext = new ArrayList<String>();
                    List<String> listOfExecutionActionsNext = new ArrayList<String>();

                    listOfPrerequisiteActionsNext.add(startAction.getActionID());
                    listOfExecutionActionsNext.add(startAction.getOutgoing());

                    nextExecutionStep.setListOfExecutionActions(listOfExecutionActionsNext);
                    nextExecutionStep.setListOfPrerequisiteActions(listOfPrerequisiteActionsNext);
                    executionPlan.add(nextExecutionStep);

                    getNextStepFromAction(startAction.getOutgoing());

                } else {
                    ParallelGateway nextParallelGateway = getParallelGatewayFromID(startAction.getOutgoing());
                    if (nextParallelGateway != null) {
                        List<String> listOfOutgoing = nextParallelGateway.getOutgoing();

                        ExecutionStep nextExecutionStep = new ExecutionStep();
                        List<String> listOfPrerequisiteActionsNext = new ArrayList<String>();
                        List<String> listOfExecutionActionsNext = new ArrayList<String>();

                        listOfPrerequisiteActionsNext.add(startAction.getActionID());
                        for (String outgoingID : listOfOutgoing) {

                            if (isAction(outgoingID)) {

                                listOfExecutionActionsNext.add(outgoingID);
                            }
                        }

                        nextExecutionStep.setListOfExecutionActions(listOfExecutionActionsNext);
                        nextExecutionStep.setListOfPrerequisiteActions(listOfPrerequisiteActionsNext);

                        executionPlan.add(nextExecutionStep);

                        for (String outgoingID : listOfOutgoing) {

                            if (isAction(outgoingID)) {
                                getNextStepFromAction(outgoingID);

                            } else {
                                ParallelGateway foundParallelGateway = getParallelGatewayFromID(outgoingID);
                                if (foundParallelGateway != null) {
                                    getNextStepFromParallelGateway(foundParallelGateway,startAction.getActionID());
                                }

                            }

                        }
                    }
                }
            }
        }

        // if start point is pg => add all outgoing actions to exection plan
        // call recuresive ...
        if (startParallelGateway != null) {

            List<String> listOfOutgoing = startParallelGateway.getOutgoing();

            ExecutionStep executionStepStart = new ExecutionStep();
            List<String> listOfPrerequisiteActions1 = new ArrayList<String>();
            List<String> listOfExecutionActions1 = new ArrayList<String>();

            for (String outgoingID : listOfOutgoing) {

                if (isAction(outgoingID)) {

                    listOfExecutionActions1.add(outgoingID);
                }
            }

            executionStepStart.setListOfExecutionActions(listOfExecutionActions1);
            executionStepStart.setListOfPrerequisiteActions(listOfPrerequisiteActions1);

            executionPlan.add(executionStepStart);

            for (String outgoingID : listOfOutgoing) {

                if (isAction(outgoingID)) {
                    getNextStepFromAction(outgoingID);

                } else {
                    ParallelGateway foundParallelGateway = getParallelGatewayFromID(outgoingID);
                    if (foundParallelGateway != null) {
                        getNextStepFromParallelGateway(foundParallelGateway,"");
                    }

                }

            }

        }

        // refine => group action from pg
        refineExecutionStep();
        
        System.out.println("No:" +executionPlan.size());
        
        log();
        
        
        return executionPlan;
    }
    
    
    private void refineExecutionStep(){
        
        List<ExecutionStep> removeExecutionSteps = new ArrayList<ExecutionStep>();
        
        for (ExecutionStep executionStep : executionPlan){
            
            if (executionStep.getListOfExecutionActions().isEmpty()){
                removeExecutionSteps.add(executionStep);
            }
            
            
        }
        
         for (ExecutionStep executionStep : removeExecutionSteps){
            executionPlan.remove(executionStep);
        }
         
         
        removeExecutionSteps.clear();
        List<ExecutionStep> combineExecutionSteps = new ArrayList<ExecutionStep>(); 
        
        for (int i=0;i<executionPlan.size()-1;i++){
            ExecutionStep executionStepI = executionPlan.get(i);
            for(int j=i+1;j<executionPlan.size();j++){
                ExecutionStep executionStepJ = executionPlan.get(j);
                
                if (i!=j){
                    ExecutionStep combinedExecutionStep = combineExecutionStep(executionStepI, executionStepJ);
                    
                    if (combinedExecutionStep!=null){
                        removeExecutionSteps.add(executionStepI);
                        removeExecutionSteps.add(executionStepJ);
                        combineExecutionSteps.add(combinedExecutionStep);
                        
                    }
                    
    
                }
                
                
            }
            
            
        }
        
        for (ExecutionStep executionStep : removeExecutionSteps){
            executionPlan.remove(executionStep);
        }
        
        
        for (ExecutionStep executionStep : combineExecutionSteps){
            executionPlan.add(executionStep);
        }
         
        
        
    }
    
    
   

    private ExecutionStep combineExecutionStep(ExecutionStep executionStepI, ExecutionStep executionStepJ) {
        ExecutionStep combinedExecutionStep = null;
        
        
        List<String> executionActionsI = executionStepI.getListOfExecutionActions();
        List<String> executionActionsJ = executionStepJ.getListOfExecutionActions();
        
        boolean rs =true;
        for (String exeActionI : executionActionsI){
            if (!executionActionsJ.contains(exeActionI)){
                rs = false;
                break;
            }
        }
        
        
        if (rs){
            List<String> prerequisiteActionList = new ArrayList<String>();
            List<String> executionActionList = new ArrayList<String>();
            
            for (String exeAction : executionActionsI){
                
                String newExeAction = new String(exeAction);
                executionActionList.add(newExeAction);
                
            }
            
            
            for (String prerequisiteActionI : executionStepI.getListOfPrerequisiteActions()){
                
                String newPrerequisiteAction  = new String(prerequisiteActionI);
                prerequisiteActionList.add(newPrerequisiteAction);
                
            }
            
            
            for (String prerequisiteActionJ : executionStepJ.getListOfPrerequisiteActions()){
                
                String newPrerequisiteAction  = new String(prerequisiteActionJ);
                prerequisiteActionList.add(newPrerequisiteAction);
                
            }
            
            combinedExecutionStep = new ExecutionStep();
            combinedExecutionStep.setListOfExecutionActions(executionActionList);
            combinedExecutionStep.setListOfPrerequisiteActions(prerequisiteActionList);
            
            
            
            
        }
        
     
        
        
        return  combinedExecutionStep;
    }

    private void log() {

        for (ExecutionStep executionStep : executionPlan) {

            List<String> prerequisiteActions = executionStep.getListOfPrerequisiteActions();
            List<String> executionActions = executionStep.getListOfExecutionActions();

            System.out.println("--- ExecutionStep ---");

            for (String actionID : prerequisiteActions) {
                System.out.println("Prereuqisite Action: " + actionID);
            }

            for (String actionID : executionActions) {
                System.out.println("Execution Action: " + actionID);
            }

            System.out.println("\n");

        }

    }

    private void getNextStepFromAction(String actionID) {

        Action foundAction = getActionFromID(actionID);

        if (foundAction.getOutgoing() != null) {

            if (isAction(foundAction.getOutgoing())) {

                ExecutionStep nextExecutionStep = new ExecutionStep();
                List<String> listOfPrerequisiteActionsNext = new ArrayList<String>();
                List<String> listOfExecutionActionsNext = new ArrayList<String>();

                listOfPrerequisiteActionsNext.add(actionID);
                listOfExecutionActionsNext.add(foundAction.getOutgoing());

                nextExecutionStep.setListOfExecutionActions(listOfExecutionActionsNext);
                nextExecutionStep.setListOfPrerequisiteActions(listOfPrerequisiteActionsNext);

                executionPlan.add(nextExecutionStep);

                getNextStepFromAction(foundAction.getOutgoing());

            } else {

                ParallelGateway foundParallelGateway = getParallelGatewayFromID(foundAction.getOutgoing());
                if (foundParallelGateway != null) {
                    getNextStepFromParallelGateway(foundParallelGateway,foundAction.getActionID());
                }
            }

        }

    }

    private void getNextStepFromParallelGateway(ParallelGateway parallelGateway, String previosActionID) {

        List<String> listOfOutgoing = parallelGateway.getOutgoing();

        ExecutionStep executionStepPG = new ExecutionStep();
        List<String> listOfPrerequisiteActionsPG = new ArrayList<String>();
        List<String> listOfExecutionActionsPG = new ArrayList<String>();
        
        listOfPrerequisiteActionsPG.add(previosActionID);

        for (String outgoingID : listOfOutgoing) {

            if (isAction(outgoingID)) {

                listOfExecutionActionsPG.add(outgoingID);
            }
        }

        executionStepPG.setListOfExecutionActions(listOfExecutionActionsPG);
        executionStepPG.setListOfPrerequisiteActions(listOfPrerequisiteActionsPG);

        executionPlan.add(executionStepPG);

        for (String outputId : listOfOutgoing) {

            if (isAction(outputId)) {

                Action foundAction = getActionFromID(outputId);

                if (foundAction.getOutgoing() != null) {

                    if (isAction(foundAction.getOutgoing())) {

                        ExecutionStep nextExecutionStep = new ExecutionStep();
                        List<String> listOfPrerequisiteActionsNext = new ArrayList<String>();
                        List<String> listOfExecutionActionsNext = new ArrayList<String>();

                        listOfPrerequisiteActionsNext.add(foundAction.getActionID());
                        listOfExecutionActionsNext.add(foundAction.getOutgoing());

                        nextExecutionStep.setListOfExecutionActions(listOfExecutionActionsNext);
                        nextExecutionStep.setListOfPrerequisiteActions(listOfPrerequisiteActionsNext);

                        executionPlan.add(nextExecutionStep);
                        getNextStepFromAction(foundAction.getOutgoing());

                    } else {

                        ParallelGateway nextParallelGateway = getParallelGatewayFromID(outputId);
                        if (nextParallelGateway != null) {
                            getNextStepFromParallelGateway(nextParallelGateway,foundAction.getActionID());
                        }
                    }
                }

            } else {

                ParallelGateway foundParallelGateway = getParallelGatewayFromID(outputId);

                if (foundParallelGateway != null) {

                    List<String> listOfOutgoingOfFoundGW = foundParallelGateway.getOutgoing();

                    if (!listOfOutgoingOfFoundGW.isEmpty()) {

                        ExecutionStep executionStepStart = new ExecutionStep();
                        List<String> listOfPrerequisiteActions1 = new ArrayList<String>();
                        List<String> listOfExecutionActions1 = new ArrayList<String>();
                        
                        listOfPrerequisiteActions1.add(previosActionID);

                        for (String outgoingID : listOfOutgoingOfFoundGW) {

                            if (isAction(outgoingID)) {

                                listOfExecutionActions1.add(outgoingID);
                            }
                        }

                        executionStepStart.setListOfExecutionActions(listOfExecutionActions1);
                        executionStepStart.setListOfPrerequisiteActions(listOfPrerequisiteActions1);

                        executionPlan.add(executionStepStart);

                        for (String outgoingID : listOfOutgoingOfFoundGW) {

                            if (isAction(outgoingID)) {
                                getNextStepFromAction(outgoingID);

                            } else {
                                ParallelGateway foundNextParallelGateway = getParallelGatewayFromID(outgoingID);

                                if (foundNextParallelGateway != null) {
                                    getNextStepFromParallelGateway(foundNextParallelGateway,previosActionID);
                                }
                            }

                        }
                    }

                }
            }
        }

    }

    private boolean isAction(String outputID) {

        boolean rs = false;

        DirectedAcyclicalGraph dag = adjustmentProcess.getDirectedAcyclicalGraph();
        List<Action> listOfActions = dag.getListOfActions();

        for (Action action : listOfActions) {
            if (action.getActionID().equals(outputID)) {
                rs = true;
            }
        }

        return rs;
    }

    private Action getActionFromID(String actionID) {

        Action foundAction = null;

        DirectedAcyclicalGraph dag = adjustmentProcess.getDirectedAcyclicalGraph();
        List<Action> listOfActions = dag.getListOfActions();

        for (Action action : listOfActions) {
            if (action.getActionID().equals(actionID)) {
                foundAction = action;
            }
        }

        return foundAction;
    }

    private ParallelGateway getParallelGatewayFromID(String parallelGatewayID) {

        ParallelGateway foundParallelGateway = null;

        DirectedAcyclicalGraph dag = adjustmentProcess.getDirectedAcyclicalGraph();
        List<ParallelGateway> listOfParallelGateways = dag.getListOfParallelGateways();

        for (ParallelGateway parallelGateway : listOfParallelGateways) {
            if (parallelGateway.getGatewayID().equals(parallelGatewayID)) {
                foundParallelGateway = parallelGateway;
            }
        }

        return foundParallelGateway;
    }

}
