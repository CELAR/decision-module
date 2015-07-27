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
package at.ac.tuwien.dsg.depic.process.generator;

import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.Action;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.AdjustmentProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DirectedAcyclicalGraph;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ParallelGateway;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentCase;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AnalyticTask;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Parameter;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.PrimitiveActionMetadata;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlAction;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRModel;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;
import at.ac.tuwien.dsg.depic.common.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;


public class AdjustmentProcessGenerator {
    
    
    DataAnalyticsFunction daf;
    QoRModel qorModel;
    PrimitiveActionMetadata primitiveActionRepository;
    List<ElasticState> finalElasticStates;
    String errorLog;
    String rootPath;

    public AdjustmentProcessGenerator(DataAnalyticsFunction daf, QoRModel qorModel, PrimitiveActionMetadata primitiveActionRepository, List<ElasticState> finalElasticStates, String errorLog, String rootPath) {
        this.daf = daf;
        this.qorModel = qorModel;
        this.primitiveActionRepository = primitiveActionRepository;
        this.finalElasticStates = finalElasticStates;
        this.errorLog = errorLog;
        this.rootPath = rootPath;
    }
    
    
    
    
    
    public List<AdjustmentProcess> generateAdjustmentProcesses(List<ElasticState> listOfFinalElasticStates) {

        List<AdjustmentProcess> listOfAdjustmentProcesses = new ArrayList<AdjustmentProcess>();

        for (ElasticState elasticState : listOfFinalElasticStates) {
            List<MetricCondition> listOfConditions = elasticState.getListOfConditions();

            List<AdjustmentAction> listOfAdjustmentActions = new ArrayList<AdjustmentAction>();
            for (MetricCondition metricCondition : listOfConditions) {
                AdjustmentAction adjustmentAction = findAdjustmentAction(metricCondition);
                if (adjustmentAction != null) {
                    listOfAdjustmentActions.add(adjustmentAction);
                }
            }

            AdjustmentProcess adjustmentProcess = new AdjustmentProcess(elasticState, listOfAdjustmentActions, null);
            buildWorkflowForAdjustmentProcess(adjustmentProcess);
            listOfAdjustmentProcesses.add(adjustmentProcess);

        }

        return listOfAdjustmentProcesses;
    }

    

    private boolean isAssociatedWithAdjustmentAction(String metricName) {
        boolean rs = false;
        List<AdjustmentAction> listOfAdjustmentActions = primitiveActionRepository.getListOfAdjustmentActions();

        for (AdjustmentAction adjustmentAction : listOfAdjustmentActions) {
            if (metricName.equals(adjustmentAction.getAssociatedQoRMetric())) {
                rs = true;
                break;
            }
        }

        return rs;
    }

    private AdjustmentAction findAdjustmentAction(MetricCondition metricCondition) {
        List<AdjustmentAction> listOfAdjustmentActions = primitiveActionRepository.getListOfAdjustmentActions();

        AdjustmentAction foundAdjustmentAction = null;
        for (AdjustmentAction adjustmentAction : listOfAdjustmentActions) {
            if (metricCondition.getMetricName().equals(adjustmentAction.getAssociatedQoRMetric())) {
                List<AdjustmentCase> listOfAdjustmentCases = adjustmentAction.getListOfAdjustmentCases();

                for (AdjustmentCase adjustmentCase : listOfAdjustmentCases) {

                    if (adjustmentCase.getEstimatedResult() == null) {
                        

                    } else {

                        if (adjustmentCase.getEstimatedResult().getLowerBound() >= metricCondition.getLowerBound()
                                && adjustmentCase.getEstimatedResult().getUpperBound() <= metricCondition.getUpperBound()) {

                            if (adjustmentCase.getAnalyticTask() == null) {
                                MetricCondition estimatedResult = adjustmentCase.getEstimatedResult();
                                MetricCondition estimatedResult_c = new MetricCondition(estimatedResult.getMetricName(), estimatedResult.getConditionID(), estimatedResult.getLowerBound(), estimatedResult.getUpperBound());
                                List<Parameter> listOfParams = adjustmentCase.getListOfParameters();
                                List<Parameter> listOfParams_c = new ArrayList<Parameter>();
                                for (Parameter param : listOfParams) {
                                    Parameter param_c = new Parameter(param.getParameterName(), param.getType(), param.getValue());
                                    listOfParams_c.add(param_c);
                                }

                                AdjustmentCase foundAdjustmentCase = new AdjustmentCase(
                                        estimatedResult_c,
                                        adjustmentCase.getAnalyticTask(),
                                        listOfParams_c);

                                List<AdjustmentCase> listOfFoundAdjustmentCases = new ArrayList<AdjustmentCase>();
                                listOfFoundAdjustmentCases.add(foundAdjustmentCase);

                                foundAdjustmentAction = new AdjustmentAction(
                                        adjustmentAction.getActionID(),
                                        adjustmentAction.getActionName(),
                                        adjustmentAction.getArtifact(),
                                        adjustmentAction.getAssociatedQoRMetric(),
                                        adjustmentAction.getListOfPrerequisiteActionIDs(),
                                        listOfFoundAdjustmentCases);

                                System.err.println("Found Action: " + adjustmentAction.getActionName());
                                System.err.println("Metric Condtidion: " + metricCondition.getMetricName() + " - " + metricCondition.getLowerBound() + " - " + metricCondition.getUpperBound());
                                System.err.println("Estimated Result: " + estimatedResult.getLowerBound() + " - " + estimatedResult.getUpperBound());
                            } else {
                                if (matchingAnalyticTaskFromDAF(adjustmentCase.getAnalyticTask())) {
                                    MetricCondition estimatedResult = adjustmentCase.getEstimatedResult();
                                    MetricCondition estimatedResult_c = new MetricCondition(estimatedResult.getMetricName(), estimatedResult.getConditionID(), estimatedResult.getLowerBound(), estimatedResult.getUpperBound());
                                    List<Parameter> listOfParams = adjustmentCase.getListOfParameters();
                                    List<Parameter> listOfParams_c = new ArrayList<Parameter>();
                                    for (Parameter param : listOfParams) {
                                        Parameter param_c = new Parameter(param.getParameterName(), param.getType(), param.getValue());
                                        listOfParams_c.add(param_c);
                                    }

                                    AdjustmentCase foundAdjustmentCase = new AdjustmentCase(
                                            estimatedResult_c,
                                            adjustmentCase.getAnalyticTask(),
                                            listOfParams_c);

                                    List<AdjustmentCase> listOfFoundAdjustmentCases = new ArrayList<AdjustmentCase>();
                                    listOfFoundAdjustmentCases.add(foundAdjustmentCase);

                                    foundAdjustmentAction = new AdjustmentAction(
                                            adjustmentAction.getActionID(),
                                            adjustmentAction.getActionName(),
                                            adjustmentAction.getArtifact(),
                                            adjustmentAction.getAssociatedQoRMetric(),
                                            adjustmentAction.getListOfPrerequisiteActionIDs(),
                                            listOfFoundAdjustmentCases);
                                } else {
                                    errorLog = errorLog + "\n Analytic Task " + adjustmentCase.getAnalyticTask().getTaskName() 
                                            + " in daf does not match. Please customize elasticity actions for metric "+metricCondition.getMetricName();
                                    System.err.println(errorLog);
                                }
                            }

                        }
                    }

                }

            }
        }
        
        
        

        return foundAdjustmentAction;

    }

    private void buildWorkflowForAdjustmentProcess(AdjustmentProcess adjustmentProcess) {

        Logger.logInfo("BUILDING WORKFLOW FOR CONTROL PROCESS ................ ");

        int numberOfActionConnection = 0;

        List<AdjustmentAction> listOfAdjustmentActions = adjustmentProcess.getListOfAdjustmentActions();
        List<Action> listOfActions = new ArrayList<Action>();

        List<ParallelGateway> listOfParallelGateways = new ArrayList<ParallelGateway>();

        for (AdjustmentAction adjustmentAction : listOfAdjustmentActions) {
            String actionID = getUDID();
            adjustmentAction.setActionID(actionID);
            Action action = new Action(actionID, adjustmentAction.getActionName());
            listOfActions.add(action);
        }

        for (Action action : listOfActions) {

            List<String> listOfActionDependencies = findDependencyActions(action);

            Logger.logInfo("No of Dependency Actions: " + listOfActionDependencies.size());
            if (listOfActionDependencies.size() > 1) {

                ParallelGateway parallelGateway = new ParallelGateway();
                List<String> incomingList = new ArrayList<String>();
                List<String> outgoingList = new ArrayList<String>();

                outgoingList.add(action.getActionID());

                UUID parallelGatewayID = UUID.randomUUID();
                parallelGateway.setGatewayID(parallelGatewayID.toString());
                action.setIncomming(parallelGateway.getGatewayID());
                numberOfActionConnection++;

                Logger.logInfo("NEW Parallel Gateway ID: " + parallelGateway.getGatewayID());
                Logger.logInfo("PG set outgoing ID : " + action.getActionID());

                for (String actionDependency : listOfActionDependencies) {

                    int prerequisiteActionIndex = findActionIndex(listOfActions, actionDependency);
                    Action prerequisiteAction = listOfActions.get(prerequisiteActionIndex);

                    Logger.logInfo("ActionDependency Name: " + prerequisiteAction.getActionName());
                    Logger.logInfo("ActionDependency ID: " + prerequisiteAction.getActionID());

                    prerequisiteAction.setOutgoing(parallelGateway.getGatewayID());
                    incomingList.add(prerequisiteAction.getActionID());
                    numberOfActionConnection++;

                }

                parallelGateway.setIncomming(incomingList);
                parallelGateway.setOutgoing(outgoingList);
                listOfParallelGateways.add(parallelGateway);

            } else if (listOfActionDependencies.size() == 1) {
                String actionDependency = listOfActionDependencies.get(0);
                int prerequisiteActionIndex = findActionIndex(listOfActions, actionDependency);
                Action prerequisiteAction = listOfActions.get(prerequisiteActionIndex);

                if (!prerequisiteAction.getActionID().equals(action.getOutgoing())) {
                    action.setIncomming(prerequisiteAction.getActionID());
                    prerequisiteAction.setOutgoing(action.getActionID());
                    numberOfActionConnection++;
                }
            }

        }

        // MAKE START ACTIVITY
        List<Action> nullIncommingAdjustmentActions = new ArrayList<Action>();
        List<ParallelGateway> nullIncommingParallelGateways = new ArrayList<ParallelGateway>();

        for (Action ca : listOfActions) {
            if (ca.getIncomming() == null) {
                nullIncommingAdjustmentActions.add(ca);
            }

        }

        for (ParallelGateway pg : listOfParallelGateways) {
            if (pg.getIncomming().isEmpty()) {
                nullIncommingParallelGateways.add(pg);
            }
        }

        if (nullIncommingAdjustmentActions.size() >= 2) {
            List<String> startPGIncomingList = new ArrayList<String>();
            List<String> startPGOutgoingList = new ArrayList<String>();
            ParallelGateway startPG = new ParallelGateway();

            UUID startParallelGatewayID = UUID.randomUUID();
            startPG.setGatewayID(startParallelGatewayID.toString());

            for (Action ca : nullIncommingAdjustmentActions) {
                startPGOutgoingList.add(ca.getActionID());
                ca.setIncomming(startPG.getGatewayID());
                numberOfActionConnection++;
            }

            for (ParallelGateway pg : nullIncommingParallelGateways) {
                startPGOutgoingList.add(pg.getGatewayID());
                pg.getIncomming().add(startPG.getGatewayID());
            }
            startPG.setIncomming(startPGIncomingList);
            startPG.setOutgoing(startPGOutgoingList);
            listOfParallelGateways.add(startPG);
        }

        // MAKE END ACTIVITY
        List<Action> nullOutgoingAdjustmentActions = new ArrayList<Action>();
        List<ParallelGateway> nullOutgoingParallelGateways = new ArrayList<ParallelGateway>();
        for (Action ca : listOfActions) {
            if (ca.getOutgoing() == null) {
                nullOutgoingAdjustmentActions.add(ca);
            }

        }

        for (ParallelGateway pg : listOfParallelGateways) {
            if (pg.getOutgoing().isEmpty()) {
                nullOutgoingParallelGateways.add(pg);
            }
        }
        if (nullOutgoingAdjustmentActions.size() >= 2) {

            List<String> endPGIncomingList = new ArrayList<String>();
            List<String> endPGOutgoingList = new ArrayList<String>();
            ParallelGateway endPG = new ParallelGateway();

            UUID endParallelGatewayID = UUID.randomUUID();
            endPG.setGatewayID(endParallelGatewayID.toString());

            for (Action ca : nullOutgoingAdjustmentActions) {
                endPGIncomingList.add(ca.getActionID());
                ca.setOutgoing(endPG.getGatewayID());
                numberOfActionConnection++;
            }

            for (ParallelGateway pg : nullOutgoingParallelGateways) {
                endPGIncomingList.add(pg.getGatewayID());
                pg.getOutgoing().add(endPG.getGatewayID());
            }
            endPG.setIncomming(endPGIncomingList);
            endPG.setOutgoing(endPGOutgoingList);
            listOfParallelGateways.add(endPG);
        }

        DirectedAcyclicalGraph dag = new DirectedAcyclicalGraph();
        dag.setListOfActions(listOfActions);
        dag.setListOfParallelGateways(listOfParallelGateways);
        numberOfActionConnection += 2;
        int noOfActions = listOfActions.size();
        adjustmentProcess.setDirectedAcyclicalGraph(dag);

        Logger.logInfo("no_of_connection: " + numberOfActionConnection);
        Logger.logInfo("no_of_action: " + noOfActions);

    }

    private int findActionIndex(List<Action> listOfActions, String prerequisiteAction) {

        int index = 0;

        for (Action ca : listOfActions) {
            if (ca.getActionName().equals(prerequisiteAction)) {
                index = listOfActions.indexOf(ca);
                break;
            }

        }

        return index;
    }

    private List<String> findDependencyActions(Action action) {

        List<String> prerequisiteActionNames = new ArrayList<String>();
        List<AdjustmentAction> listOfAdjustmentActions = primitiveActionRepository.getListOfAdjustmentActions();

        for (AdjustmentAction adjustmentAction : listOfAdjustmentActions) {
            if (adjustmentAction.getActionName().endsWith(action.getActionName())) {
                prerequisiteActionNames = adjustmentAction.getListOfPrerequisiteActionIDs();
                break;
            }
        }

        return prerequisiteActionNames;
    }

    private boolean matchingAnalyticTaskFromDAF(AnalyticTask pamAnalyticTask) {
        AnalyticTask analyticTask = null;
        try {
            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

            String daw = daf.getDaw();

            int beginIndex = daw.indexOf("<depic>");
            int endIndex = daw.indexOf("</depic>");
            String analyticTasksStr = header + daw.substring(beginIndex + 7, endIndex);
            analyticTask = JAXBUtils.unmarshal(analyticTasksStr, AnalyticTask.class);

        } catch (JAXBException ex) {
            java.util.logging.Logger.getLogger(DataElasticityManagementProcessesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean rs = true;
        if (pamAnalyticTask != null && analyticTask != null) {

            if (pamAnalyticTask.getTaskName().equals(analyticTask.getTaskName())) {

                List<Parameter> listOfParameters1 = analyticTask.getParameters();
                List<Parameter> listOfParameters2 = pamAnalyticTask.getParameters();

                for (Parameter pam1 : listOfParameters1) {
                    for (Parameter pam2 : listOfParameters2) {
                        if (pam1.getParameterName().equals(pam2.getParameterName())) {
                            if (!pam1.getValue().equals(pam2.getValue())) {
                                rs = false;
                            }
                        }

                    }

                }

            } else {
                rs = false;
            }

        } else {
            rs = false;
        }

        return rs;

    }
    
    
    private String getUDID() {
        UUID actionID = UUID.randomUUID();
        return actionID.toString();
    }
}
