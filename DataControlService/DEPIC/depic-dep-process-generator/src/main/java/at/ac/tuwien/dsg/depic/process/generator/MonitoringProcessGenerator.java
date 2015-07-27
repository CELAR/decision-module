/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author Jun
 */
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
        System.out.println("No of monitoring actions: " + listOfMonitoringActions.size());
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
            
            System.out.println("Monitoring Action Name: " + ma.getMonitoringActionName());
            
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
        
        
        for (int i=0;i<listOfMonitoringActions.size();i++){
            System.out.println("Test MA: " + listOfMonitoringActions.get(i).getMonitoringActionName());
        }

        return monitoringProcess;
    }

    private String getUDID() {
        UUID actionID = UUID.randomUUID();
        return actionID.toString();
    }
}
