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
package at.ac.tuwien.dsg.depic.common.utils;

import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction.DataAnalyticsFunction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import at.ac.tuwien.dsg.depic.common.entity.qor.QElement;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRMetric;
import at.ac.tuwien.dsg.depic.common.entity.qor.QoRModel;
import at.ac.tuwien.dsg.depic.common.entity.qor.Range;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TPolicy;
import generated.oasis.tosca.TServiceTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;


public class ToscaParser {


    public QoRModel parseQoRModel(String toscaString) {
        
        
        List<MetricCondition> listOfConditions = extractConditions(toscaString);
        System.out.println("Size: " + listOfConditions.size());
        QoRModel qoRModel = convertToQoRModel(listOfConditions);
        

        return qoRModel;
    }
    
    
    private List<MetricCondition> extractConditions(String toscaString) {

        List<MetricCondition> metricConditions = new ArrayList<MetricCondition>();

        try {
            TDefinitions tDefinitions = ToscaXmlProcess.readToscaXML(toscaString);
            TNodeTemplate tNodeTemplate = ToscaStructureQuery.getNodetemplateById("DataControllerUnit", tDefinitions);
            List<TPolicy> listOfTPolicy = tNodeTemplate.getPolicies().getPolicy();

            for (TPolicy policy : listOfTPolicy) {

                String policyStr = policy.getName();
                System.out.println(policy.getName());

                Constraint constraint = SYBLDirectiveMappingFromXML.mapSYBLAnnotationToXMLConstraint(policyStr);
                List<BinaryRestrictionsConjunction> listOfBinaryRestrictionsConjunctions = constraint.getToEnforce().getBinaryRestriction();

                for (BinaryRestrictionsConjunction binaryRestrictionsConjunction : listOfBinaryRestrictionsConjunctions) {

                    List<BinaryRestriction> listOfBinaryRestrictions = binaryRestrictionsConjunction.getBinaryRestrictions();

                    for (BinaryRestriction binaryRestriction : listOfBinaryRestrictions) {

                        String metricName = binaryRestriction.getLeftHandSide().toString();
                        String metricValue = binaryRestriction.getRightHandSide().toString();
                        String operator = binaryRestriction.getType();
                        System.out.println(metricName);
                        System.out.println(metricValue);
                        System.out.println(operator);

                        if (operator.equals("greaterThan")) {
                            MetricCondition mc = new MetricCondition(metricName, constraint.getId(), Double.parseDouble(metricValue), Double.MAX_VALUE);
                            metricConditions.add(mc);
                        } else if (operator.equals("lessThan")) {
                            MetricCondition mc = new MetricCondition(metricName, constraint.getId(), Double.MIN_VALUE, Double.parseDouble(metricValue));
                            metricConditions.add(mc);
                        }

                    }

                }

            }

        } catch (JAXBException ex) {
            Logger.getLogger(ToscaParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ToscaParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return metricConditions;
      
    }
    
    
    private QoRModel convertToQoRModel(List<MetricCondition> listOfMetricConditions){
        
        QoRModel qoRModel = new QoRModel();
        
        List<QoRMetric> listOfQoRMetrics = new ArrayList<QoRMetric>();
        List<QElement> listOfQElements = new ArrayList<QElement>();
     
        for (int i=0;i<listOfMetricConditions.size();i++){
            
            MetricCondition metricConditionI = listOfMetricConditions.get(i);
            
            String metricNameI = metricConditionI.getMetricName();
            double lowerBoundI = metricConditionI.getLowerBound();
            double upperBoundI = metricConditionI.getUpperBound();
            
            
            for (int j=i+1;j<listOfMetricConditions.size();j++){
                MetricCondition metricConditionJ = listOfMetricConditions.get(j);
                
                String metricNameJ = metricConditionJ.getMetricName();
                double lowerBoundJ = metricConditionJ.getLowerBound();
                double upperBoundJ = metricConditionJ.getUpperBound();
                
                
                if (i!=j && metricNameI.equals(metricNameJ)) {
                    
                    System.out.println("EQUAL");
                    
                    System.out.println(lowerBoundI);
                     System.out.println(upperBoundI);
                      System.out.println(lowerBoundJ);
                     System.out.println(upperBoundJ);
                    
                    if (upperBoundI==Double.MAX_VALUE && lowerBoundJ==Double.MIN_VALUE) {
                        List<Range> listOfRanges = new ArrayList<Range>();
                        Range rangeIJ = new Range(metricConditionI.getConditionID(), lowerBoundI, upperBoundJ);
                        listOfRanges.add(rangeIJ);
                        
                        QoRMetric qoRMetric = new QoRMetric(metricNameI, "%", listOfRanges);
                        listOfQoRMetrics.add(qoRMetric);
                        
                    } else if (upperBoundJ==Double.MAX_VALUE && lowerBoundI==Double.MIN_VALUE){
                         List<Range> listOfRanges = new ArrayList<Range>();
                        Range rangeIJ = new Range(metricConditionI.getConditionID(), lowerBoundJ, upperBoundI);
                        listOfRanges.add(rangeIJ);
                        QoRMetric qoRMetric = new QoRMetric(metricNameI, "%", listOfRanges);
                        listOfQoRMetrics.add(qoRMetric);
                    }
                    
                    
                    
                }
                
                
                
            }
            
            
            
            
        }
        
        QElement qElement = new QElement();
        List<String> listOfQElementRanges = new ArrayList<String>();
       
        for (QoRMetric qoRMetric : listOfQoRMetrics){
            Range r = qoRMetric.getListOfRanges().get(0);
            listOfQElementRanges.add(r.getRangeID());
            
        }
        qElement.setListOfRanges(listOfQElementRanges);
        listOfQElements.add(qElement);

        qoRModel.setListOfMetrics(listOfQoRMetrics);
        qoRModel.setListOfQElements(listOfQElements);
        
        return qoRModel;
        
    }
    
    
}
