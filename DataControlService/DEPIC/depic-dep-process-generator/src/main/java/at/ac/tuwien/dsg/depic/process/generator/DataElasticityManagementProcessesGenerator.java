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


import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;

import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticState;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.AdjustmentProcess;

import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.DataElasticityManagementProcess;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.ElasticStateSet;
import at.ac.tuwien.dsg.depic.common.entity.eda.elasticprocess.MonitoringProcess;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MonitoringAction;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentCase;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.PrimitiveActionMetadata;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlCase;

import at.ac.tuwien.dsg.depic.common.entity.qor.QElement;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRMetric;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRModel;
import at.ac.tuwien.dsg.depic.common.entity.qor.Range;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DaaSDescription;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DepicDescription;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.DepicDesciptionImporter;
import at.ac.tuwien.dsg.depic.common.utils.IOUtils;
import at.ac.tuwien.dsg.depic.common.utils.JAXBUtils;

import at.ac.tuwien.dsg.depic.common.utils.Logger;
import at.ac.tuwien.dsg.depic.common.utils.YamlUtils;
import at.ac.tuwien.dsg.depic.repository.ElasticProcessRepositoryManager;

import at.ac.tuwien.dsg.depic.repository.PrimitiveActionMetadataManager;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import javax.xml.bind.JAXBException;



public class DataElasticityManagementProcessesGenerator {

    DataAnalyticsFunction daf;
    QoRModel qorModel;
    PrimitiveActionMetadata primitiveActionRepository;
    List<ElasticState> finalElasticStates;
    String errorLog;
    String rootPath;
    String classPath;

    public DataElasticityManagementProcessesGenerator() {
    }
    
    

/**
 * 
 * @param daf Data Analytics Function
 * @param qorModel Quality of Results
 */    
  
    
    /**
     * 
     * @param daf
     * @param qorModel
     * @param primitiveActionRepository
     * @param rooPath 
     */
    public DataElasticityManagementProcessesGenerator(DataAnalyticsFunction daf, QoRModel qorModel, String rooPath, String classPath) {
        this.daf = daf;
        this.qorModel = qorModel;
        this.classPath = classPath;
        this.rootPath = rooPath;
        config();
    }

    /**
     * 
     * @return Data Elasticity Management Process includes Monitoring Process, Adjustment Processes and Resource Control Plans
     */
    public DataElasticityManagementProcess generateElasticProcesses() {
        Logger.logInfo("Start generate DEP Processes ... ");

        finalElasticStates = generateFinalElasticStateSet();
        
        MonitoringProcessGenerator monitoringProcessGenerator = new MonitoringProcessGenerator(daf, qorModel, primitiveActionRepository, finalElasticStates, errorLog, rootPath);
        MonitoringProcess monitoringProcess= monitoringProcessGenerator.generateMonitoringProcess();
        
        AdjustmentProcessGenerator apg = new AdjustmentProcessGenerator(daf, qorModel, primitiveActionRepository, finalElasticStates, errorLog, rootPath);
        List<AdjustmentProcess> listOfAdjustmentProcesses = apg.generateAdjustmentProcesses(finalElasticStates);
        
        ResourceControlPlanGenerator rcpg = new ResourceControlPlanGenerator(daf, qorModel, primitiveActionRepository, finalElasticStates, errorLog, rootPath);       
        SYBLSpecification sYBLSpecification = rcpg.generateResourceControlPlan(finalElasticStates);

        DataElasticityManagementProcess depProcess = new DataElasticityManagementProcess(monitoringProcess, listOfAdjustmentProcesses, null);
        
       
       storeDEP(depProcess);
        
        depProcess.setsYBLSpecification(sYBLSpecification);
        return depProcess;
    }
    
    private void storeDEP(DataElasticityManagementProcess depProcess){

        String elasticStateSetXML ="";
        
        ElasticStateSet elasticStateSet = new ElasticStateSet(finalElasticStates);
        
        try {
            elasticStateSetXML = JAXBUtils.marshal(elasticStateSet, ElasticStateSet.class);
        } catch (JAXBException ex) {
           
        }
        
        String elasticityProcessesXML = YamlUtils.marshallYaml(DataElasticityManagementProcess.class, depProcess);
            
        DepicDesciptionImporter desciptionImporter = new DepicDesciptionImporter();
        DepicDescription depicDescription = desciptionImporter.importDescription(classPath);
        DaaSDescription daaSDescription = depicDescription.getDaaSDescription();
        
        ElasticProcessRepositoryManager elStore = new ElasticProcessRepositoryManager(classPath);
        elStore.storeElasticityProcesses(daaSDescription.getDaasName(),elasticStateSetXML, elasticityProcessesXML, "", daaSDescription.getdBType().getDBType());
    }
    

    public List<ElasticState> getFinalElasticStates() {
        return finalElasticStates;
    }

   
    // establish elasticity state
    public List<ElasticState> generateFinalElasticStateSet() {

        List<ElasticState> listOfFinalElasticStates = new ArrayList<ElasticState>();
        List<QElement> listOfQElements = qorModel.getListOfQElements();

        for (QElement qElement : listOfQElements) {
            List<ElasticState> listOfFinalElasticState_qelement = decomposeQElement(qElement);

            if (listOfFinalElasticState_qelement != null) {
                listOfFinalElasticStates.addAll(listOfFinalElasticState_qelement);
            } else {

            }
        }
        return listOfFinalElasticStates;
    }

    private List<ElasticState> decomposeQElement(QElement qElement) {


        List<QoRMetric> listOfQoRMetrics = qorModel.getListOfMetrics();

        List<List> listOfConditionSet = new ArrayList<List>();

        int counter = 0;
        for (QoRMetric qorMetric : listOfQoRMetrics) {



            List<MetricCondition> listOfConditions = findEstimatedResultForQoRMetric(qorMetric);


            if (listOfConditions.size() > 0) {
                Range r = findMatchingRange(qElement, qorMetric);

                if (r != null) {

                    List<MetricCondition> unitConditions = new ArrayList<MetricCondition>();

                    for (MetricCondition condition : listOfConditions) {
                      

                        if (condition.getLowerBound() == r.getFromValue() && condition.getUpperBound() == r.getToValue()) {
                            unitConditions.add(condition);
                        } else if (condition.getLowerBound() > r.getFromValue() && condition.getUpperBound() < r.getToValue()) {

                            MetricCondition condition_pre = new MetricCondition(
                                    condition.getMetricName(),
                                    condition.getConditionID() + "_" + String.valueOf(++counter),
                                    r.getFromValue(),
                                    condition.getLowerBound());

                            MetricCondition condition_post = new MetricCondition(
                                    condition.getMetricName(),
                                    condition.getConditionID() + "_" + String.valueOf(++counter),
                                    condition.getUpperBound(),
                                    r.getToValue());

                            unitConditions.add(condition);
                            unitConditions.add(condition_pre);
                            unitConditions.add(condition_post);

                        } else if (condition.getLowerBound() > r.getFromValue() && condition.getUpperBound() == r.getToValue()) {

                            MetricCondition condition_pre = new MetricCondition(
                                    condition.getMetricName(),
                                    condition.getConditionID() + "_" + String.valueOf(++counter),
                                    r.getFromValue(),
                                    condition.getLowerBound());
                            unitConditions.add(condition);
                            unitConditions.add(condition_pre);

                        } else if (condition.getLowerBound() == r.getFromValue() && condition.getUpperBound() < r.getToValue()) {
                            MetricCondition condition_post = new MetricCondition(
                                    condition.getMetricName(),
                                    condition.getConditionID() + "_" + String.valueOf(++counter),
                                    condition.getUpperBound(),
                                    r.getToValue());

                            unitConditions.add(condition);
                            unitConditions.add(condition_post);
                        } else if (condition.getLowerBound() < r.getFromValue() && condition.getUpperBound() >= r.getToValue()) {
                            errorLog = errorLog + "\n Can not decompose conditions of metric " + qorMetric.getName() + " in " + qElement.getqElementID() + ".";

                            System.err.println(errorLog);

                        } else if (condition.getLowerBound() <= r.getFromValue() && condition.getUpperBound() > r.getToValue()) {
                            errorLog = errorLog + "\n Can not decompose conditions of metric " + qorMetric.getName() + " in " + qElement.getqElementID() + ".";

                            System.err.println(errorLog);

                        } else {
                            MetricCondition condition_u = new MetricCondition(
                                    condition.getMetricName(),
                                    condition.getConditionID() + "_" + String.valueOf(++counter),
                                    r.getFromValue(),
                                    r.getToValue());
                            unitConditions.add(condition_u);

                        }
                    }

                    if (unitConditions.size() > 0) {

                        listOfConditionSet.add(unitConditions);
                    }

                }
            }
        }

        List<ElasticState> listOfFinalElasticStates = new ArrayList<ElasticState>();

        int noOfMetric = listOfConditionSet.size();
        List<int[]> combinations = new ArrayList<int[]>();

        for (int k = 0; k < 1000; k++) {
            int[] conditionIndice = new int[noOfMetric];

            for (int i = 0; i < noOfMetric; i++) {

                List<MetricCondition> conditionMetric_i = listOfConditionSet.get(i);
                int noOfConditions = conditionMetric_i.size();
                int conditionIndex = randomInt(0, noOfConditions);
                conditionIndice[i] = conditionIndex;
            }

            if (!isDuplicated(combinations, conditionIndice)) {
                combinations.add(conditionIndice);
            }

        }

        for (int[] conbination : combinations) {

            List<MetricCondition> eStateConditions = new ArrayList<MetricCondition>();
            String eStateID = "";

            for (int i = 0; i < conbination.length; i++) {

                List<MetricCondition> conditionMetric_i = listOfConditionSet.get(i);
                MetricCondition metricCondition = conditionMetric_i.get(conbination[i]);
                MetricCondition newMetricCondition = new MetricCondition(metricCondition.getMetricName(), metricCondition.getConditionID(), metricCondition.getLowerBound(), metricCondition.getUpperBound());
                eStateConditions.add(newMetricCondition);
                eStateID = eStateID + metricCondition.getConditionID() + ";";

            }

            ElasticState elasticState = new ElasticState(eStateID, eStateConditions);
            listOfFinalElasticStates.add(elasticState);
        }

        return listOfFinalElasticStates;

    }

    private List<MetricCondition> findEstimatedResultForQoRMetric(QoRMetric qorMetric) {
        List<AdjustmentAction> listOfAdjustmentActions = primitiveActionRepository.getListOfAdjustmentActions();

        List<AdjustmentCase> listOfAdjustmentCases = null;

        for (AdjustmentAction adjustmentAction : listOfAdjustmentActions) {
            System.err.println("aa1: " + adjustmentAction.getAssociatedQoRMetric());

            if (qorMetric.getName().equals(adjustmentAction.getAssociatedQoRMetric())) {
                System.err.println("aa2: " + adjustmentAction.getAssociatedQoRMetric());
                System.err.println("aa3: " + adjustmentAction.getListOfAdjustmentCases().size());
                listOfAdjustmentCases = adjustmentAction.getListOfAdjustmentCases();
                break;
            }
        }
        
        
        if (listOfAdjustmentCases==null && !isAssociatedWithResourceControlAction(qorMetric.getName())) {      
            errorLog = errorLog + "\n No adjustment action found for metric " + qorMetric.getName() + ". Conditions of this metric are not added to eState.";                        
            System.err.println(errorLog);
        }
        
        

        List<MetricCondition> listOfConditions = new ArrayList<MetricCondition>();

        if (listOfAdjustmentCases != null) {

            for (AdjustmentCase adjustmentCase : listOfAdjustmentCases) {

                if (adjustmentCase.getEstimatedResult() != null) {

                    MetricCondition condition = adjustmentCase.getEstimatedResult();
                    System.err.println("a a1: " + condition.getLowerBound());
                    System.err.println("a a2: " + condition.getUpperBound());
                    if (condition != null) {
                        listOfConditions.add(condition);
                    }
                } else {
                    errorLog = errorLog + "\n No estimated results for metric " + qorMetric.getName() + ". Need to customize elasticity actions for this metric in monitoring/adjustment process.";

                    System.err.println(errorLog);
                }

            }
        }

        System.err.println("No of conditions: " + listOfConditions.size());


        List<ResourceControlAction> listOfResourceControlActions = primitiveActionRepository.getListOfResourceControls();

        List<ResourceControlCase> listOfResourceControlCases = null;

        for (ResourceControlAction resourceControlAction : listOfResourceControlActions) {
            if (qorMetric.getName().equals(resourceControlAction.getAssociatedQoRMetric())) {
                listOfResourceControlCases = resourceControlAction.getListOfResourceControlCases();
            }
        }

        if (listOfResourceControlCases != null) {
            for (ResourceControlCase resourceControlCase : listOfResourceControlCases) {
                MetricCondition condition = resourceControlCase.getEstimatedResult();
                listOfConditions.add(condition);
            }
        }

        return listOfConditions;
    }

    private Range findMatchingRange(QElement qElement, QoRMetric qorMetric) {

        Range foundRange = null;

        List<String> listOfRangeIDs_qelement = qElement.getListOfRanges();

        List<Range> listOfPreDefinedRange_qormetric = qorMetric.getListOfRanges();

        for (Range range : listOfPreDefinedRange_qormetric) {
            for (String rangeID : listOfRangeIDs_qelement) {
                if (range.getRangeID().equals(rangeID)) {
                    foundRange = new Range(rangeID, range.getFromValue(), range.getToValue());
                }
            }
        }

        return foundRange;

    }

    private boolean isDuplicated(List<int[]> combinations, int[] conditionIndice) {

        boolean rs = false;

        for (int i = 0; i < combinations.size(); i++) {
            int[] condition_i = combinations.get(i);

            String conditionString_i = "";
            String comparedCondition = "";

            for (int j = 0; j < condition_i.length; j++) {
                conditionString_i = conditionString_i + String.valueOf(condition_i[j]) + ";";
                comparedCondition = comparedCondition + String.valueOf(conditionIndice[j]) + ";";
            }

            if (conditionString_i.equals(comparedCondition)) {
                rs = true;
                break;
            }
        }

        return rs;
    }

    private int randomInt(int min, int max) {
        Random random = new Random();
        int randomNumber = random.nextInt(max - min) + min;
        return randomNumber;
    }

    
    private boolean isAssociatedWithResourceControlAction(String metricName) {
        boolean rs = false;
        List<ResourceControlAction> listOfResourceControlActions = primitiveActionRepository.getListOfResourceControls();

        for (ResourceControlAction rc : listOfResourceControlActions) {
            if (metricName.equals(rc.getAssociatedQoRMetric())) {
                rs = true;
                break;
            }
        }

        return rs;
    }

    
    private void config(){
        errorLog = "";
        YamlUtils.setFilePath(rootPath);
        loadPrimitiveActionMetadata();
        
    }
    
    private void loadPrimitiveActionMetadata(){
        
        PrimitiveActionMetadataManager pamm = new PrimitiveActionMetadataManager(classPath);
        List<MonitoringAction> listOfMonitoringActions = pamm.getMonitoringActionList();
        List<AdjustmentAction> listOfAdjustmentActions = pamm.getAdjustmentActionList();
        List<ResourceControlAction> listOfResourceControlActions = pamm.getResourceControlActionList();
        
        primitiveActionRepository = new PrimitiveActionMetadata(listOfAdjustmentActions, listOfMonitoringActions, listOfResourceControlActions);
        
    }

}
