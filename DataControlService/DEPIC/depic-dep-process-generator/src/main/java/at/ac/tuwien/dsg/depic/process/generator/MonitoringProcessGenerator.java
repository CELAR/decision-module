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
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DirectedAcyclicalGraph;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.MonitoringProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ParallelGateway;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Artifact;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MonitoringAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Parameter;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.PrimitiveActionMetadata;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRMetric;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRModel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MonitoringProcessGenerator {
    DataAnalyticsFunction daf;
    QoRModel qorModel;
    PrimitiveActionMetadata primitiveActionRepository;
    List<ElasticState> finalElasticStates;
    String errorLog;
    String rootPath;

    public MonitoringProcessGenerator(DataAnalyticsFunction daf, QoRModel qorModel, PrimitiveActionMetadata primitiveActionRepository, List<ElasticState> finalElasticStates, String errorLog, String rootPath) {
        this.daf = daf;
        this.qorModel = qorModel;
        this.primitiveActionRepository = primitiveActionRepository;
        this.finalElasticStates = finalElasticStates;
        this.errorLog = errorLog;
        this.rootPath = rootPath;
    }

   
    
    public MonitoringProcess generateMonitoringProcess() {

        List<MonitoringAction> listOfMonitoringActions = new ArrayList<MonitoringAction>();

        List<QoRMetric> listOfQoRMetrics = qorModel.getListOfMetrics();

        for (QoRMetric metric : listOfQoRMetrics) {
            String qorMetricName = metric.getName();
            MonitoringAction monitoringAction = findCorrespondingMonitoringActionFromQoRMetric(qorMetricName);
            if (monitoringAction != null) {
                listOfMonitoringActions.add(monitoringAction);
            }
        }
       
        MonitoringProcess monitorProcess = parallelizeMonitoringActions(listOfMonitoringActions);

        return monitorProcess;
    }

    private MonitoringAction findCorrespondingMonitoringActionFromQoRMetric(String qorMetricName) {
        List<MonitoringAction> listOfMonitoringActions = primitiveActionRepository.getListOfMonitoringActions();

        MonitoringAction foundMonitoringAction = null;
        for (MonitoringAction ma : listOfMonitoringActions) {
            if (qorMetricName.equals(ma.getAssociatedQoRMetric())) {
                foundMonitoringAction = copyMonitoringActionInstance(ma);
            }
        }

        return foundMonitoringAction;
    }

    private MonitoringAction copyMonitoringActionInstance(MonitoringAction ma) {

        Artifact artifact = new Artifact(
                ma.getArtifact().getName(),
                ma.getArtifact().getDescription(),
                ma.getArtifact().getLocation(),
                ma.getArtifact().getType(),
                ma.getArtifact().getRestfulAPI());

        List<Parameter> listOfParameters = new ArrayList<Parameter>();

        for (Parameter param : ma.getListOfParameters()) {

            Parameter paramI = new Parameter(
                    param.getParameterName(),
                    param.getType(),
                    param.getValue());

            listOfParameters.add(paramI);

        }

        MonitoringAction monitoringAction = new MonitoringAction(
                ma.getMonitorActionID(),
                ma.getMonitoringActionName(),
                artifact,
                ma.getAssociatedQoRMetric(),
                listOfParameters);

        return monitoringAction;
    }

    private MonitoringProcess parallelizeMonitoringActions(List<MonitoringAction> listOfMonitoringActions) {

        List<Action> listOfAction = new ArrayList<Action>();

        for (MonitoringAction ma : listOfMonitoringActions) {

            String actionID = getUDID();
            ma.setMonitorActionID(actionID);
          
            
            Action action = new Action(actionID, ma.getMonitoringActionName());
            listOfAction.add(action);
        }

        String parallelGatewayInId = getUDID();
        String parallelGatewayOutId = getUDID();
        List<String> inputListOfIncommings = new ArrayList<String>();
        List<String> inputlistOfOutgoings = new ArrayList<String>();
        List<String> outputListOfIncommings = new ArrayList<String>();
        List<String> outListOfOutgoings = new ArrayList<String>();

        for (Action action : listOfAction) {
            action.setIncomming(parallelGatewayInId);
            action.setOutgoing(parallelGatewayOutId);
            inputlistOfOutgoings.add(action.getActionID());
            outputListOfIncommings.add(action.getActionID());
        }

        ParallelGateway pg_in = new ParallelGateway(parallelGatewayInId, inputListOfIncommings, inputlistOfOutgoings);
        ParallelGateway pg_out = new ParallelGateway(parallelGatewayOutId, outputListOfIncommings, outListOfOutgoings);

        List<ParallelGateway> listOfParallelGateways = new ArrayList<ParallelGateway>();
        listOfParallelGateways.add(pg_in);
        listOfParallelGateways.add(pg_out);

        DirectedAcyclicalGraph dag = new DirectedAcyclicalGraph();
        dag.setListOfActions(listOfAction);
        dag.setListOfParallelGateways(listOfParallelGateways);

        MonitoringProcess monitoringProcess = new MonitoringProcess(listOfMonitoringActions, dag);
        
    
        return monitoringProcess;
    }

    private String getUDID() {
        UUID actionID = UUID.randomUUID();
        return actionID.toString();
    }
}
