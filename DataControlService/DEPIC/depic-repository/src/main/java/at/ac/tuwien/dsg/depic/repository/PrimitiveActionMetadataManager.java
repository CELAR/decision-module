/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.repository;

import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.AdjustmentCase;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Artifact;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MetricCondition;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.MonitoringAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.Parameter;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlAction;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlCase;
import at.ac.tuwien.dsg.depic.common.entity.primitiveaction.ResourceControlStrategy;
import at.ac.tuwien.dsg.depic.common.utils.Configuration;
import at.ac.tuwien.dsg.depic.common.utils.MySqlConnectionManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jun
 */
public class PrimitiveActionMetadataManager {

    MySqlConnectionManager connectionManager;
    
    String classPath;


    

    public PrimitiveActionMetadataManager(String classPath) {
         this.classPath = classPath;
        Configuration config = new Configuration(classPath);
        String ip = config.getConfig("DB.PAM.IP");
        String port = config.getConfig("DB.PAM.PORT");
        String database = config.getConfig("DB.PAM.DATABASE");
        String username = config.getConfig("DB.PAM.USERNAME");
        String password = config.getConfig("DB.PAM.PASSWORD");


        connectionManager = new MySqlConnectionManager(ip, port, database, username, password);

    }

    public PrimitiveActionMetadataManager(String ip, String port, String database, String username, String password) {
    
        connectionManager = new MySqlConnectionManager(ip, port, database, username, password);
    
    }
    
    

    public void storeMonitoringAction(MonitoringAction monitoringAction) {

        String sql = "INSERT INTO PrimitiveAction (ActionName, AssociatedQoRMetric, ArtifactName, ActionType) VALUES ('" + monitoringAction.getMonitoringActionName() + "','" + monitoringAction.getAssociatedQoRMetric() + "','" + monitoringAction.getArtifact().getName() + "', 'MonitoringAction')";
        connectionManager.ExecuteUpdate(sql);

        storeArtifact(monitoringAction.getArtifact(), monitoringAction.getMonitoringActionName());
        storeParameters(monitoringAction.getListOfParameters(), monitoringAction.getMonitoringActionName());
    }

    public void storeAdjustmentAction(AdjustmentAction adjustmentAction) {
        String sql = "INSERT INTO PrimitiveAction (ActionName, AssociatedQoRMetric, ArtifactName, ActionType) VALUES ('" + adjustmentAction.getActionName() + "','" + adjustmentAction.getAssociatedQoRMetric() + "','" + adjustmentAction.getArtifact().getName() + "', 'AdjustmentAction')";
        connectionManager.ExecuteUpdate(sql);

        storeArtifact(adjustmentAction.getArtifact(), adjustmentAction.getActionName());
        if (adjustmentAction.getListOfPrerequisiteActionIDs()!=null){
        storePrerequisiteActions(adjustmentAction.getListOfPrerequisiteActionIDs(), adjustmentAction.getActionName());
        }
        storeAdjustmentCases(adjustmentAction.getListOfAdjustmentCases(), adjustmentAction.getActionName());

    }

    public void storeResourceControlAction(ResourceControlAction resourceControlAction) {

        String sql = "INSERT INTO PrimitiveAction (ActionName, AssociatedQoRMetric, ArtifactName, ActionType) VALUES ('" + resourceControlAction.getActionName() + "','" + resourceControlAction.getAssociatedQoRMetric() + "','none', 'ResourceControlAction')";
        connectionManager.ExecuteUpdate(sql);

        List<ResourceControlCase> listOfResourceControlCases = resourceControlAction.getListOfResourceControlCases();

        storeResourceControlCases(listOfResourceControlCases, resourceControlAction.getActionName());
    }

    public void storeResourceControlCases(List<ResourceControlCase> listOfResourceControlCases, String actionName) {

        for (int i = 0; i < listOfResourceControlCases.size(); i++) {
            ResourceControlCase resourceControlCase = listOfResourceControlCases.get(0);

            
            String analyticTaskName = "";
            
            if (resourceControlCase.getAnalyticTask()!=null){
                analyticTaskName = resourceControlCase.getAnalyticTask().getTaskName();
            }
            
            String sql = "INSERT INTO ResourceControlCase (ResourceControlActionName, ResourceControlCase, EstimatedResultFrom, EstimatedResultTo, AnalyticsTaskName) "
                    + "VALUES ('" + actionName + "', '" + i + "' , " + resourceControlCase.getEstimatedResult().getLowerBound() + ", " + resourceControlCase.getEstimatedResult().getUpperBound() + ", '" + analyticTaskName + "')";
            System.err.println(sql);

            connectionManager.ExecuteUpdate(sql);

            
            if (resourceControlCase.getAnalyticTask()!=null){
            
            List<Parameter> listOfAnalyticTaskParameters = resourceControlCase.getAnalyticTask().getParameters();
            storeAnalyticTaskParameters(listOfAnalyticTaskParameters, actionName, String.valueOf(i), resourceControlCase.getAnalyticTask().getTaskName());

            }
            
            
            List<ResourceControlStrategy> listOfResourceControlStrategies = resourceControlCase.getListOfResourceControlStrategies();
            storeResourceControlStrategies(listOfResourceControlStrategies, actionName, String.valueOf(i));
        }

    }

    private void storeResourceControlStrategies(List<ResourceControlStrategy> listOfResourceControlStrategies, String actionName, String resourceControlCase) {

        for (ResourceControlStrategy resourceControlStrategy : listOfResourceControlStrategies) {

            String sql = "INSERT INTO ResourceControlStrategy (ResourceControlActionName,ResourceControlCase,  ControlMetric, EffectedActionName,  ScaleInConditionFrom, ScaleInConditionTo, ScaleOutConditionFrom, ScaleOutConditionTo) "
                    + "VALUES ('" + actionName + "', '" + resourceControlCase + "' , '" + resourceControlStrategy.getControlMetric() + "', '" + resourceControlStrategy.getPrimitiveAction() + "' "
                    + ", " + resourceControlStrategy.getScaleInCondition().getLowerBound() + ", " + resourceControlStrategy.getScaleInCondition().getUpperBound() + ""
                    + ", " + resourceControlStrategy.getScaleOutCondition().getLowerBound() + ", " + resourceControlStrategy.getScaleOutCondition().getLowerBound() + ")";

            System.err.println(sql);
            connectionManager.ExecuteUpdate(sql);

        }

    }

    public void storeAdjustmentCases(List<AdjustmentCase> listOfAdjustmentCases, String actionName)  {

        for (int i = 0; i < listOfAdjustmentCases.size(); i++) {
            AdjustmentCase adjustmentCase = listOfAdjustmentCases.get(i);
            
            String analyticTaskName = "";
            
            if (adjustmentCase.getAnalyticTask()!=null){
                analyticTaskName = adjustmentCase.getAnalyticTask().getTaskName();
            }
            
            
            String sql = "INSERT INTO AdjustmentCase (ActionName, AdjustmentCase, EstimatedResultFrom, EstimatedResultTo, AnalyticsTaskName) "
                    + "VALUES ('" + actionName + "', '" + i + "', " + adjustmentCase.getEstimatedResult().getLowerBound() + ", " + adjustmentCase.getEstimatedResult().getUpperBound() + ", '" + analyticTaskName + "')";
            connectionManager.ExecuteUpdate(sql);

            
            if (adjustmentCase.getAnalyticTask()!=null) {
            
            List<Parameter> listOfAnalyticTaskParameters = adjustmentCase.getAnalyticTask().getParameters();
            storeAnalyticTaskParameters(listOfAnalyticTaskParameters, actionName, String.valueOf(i), adjustmentCase.getAnalyticTask().getTaskName());

            }
            
            
            if (adjustmentCase.getListOfParameters()!=null) {
            
            List<Parameter> listOfAdjustmentCaseParameters = adjustmentCase.getListOfParameters();
            storeAdjustmentCaseParameters(listOfAdjustmentCaseParameters, actionName, String.valueOf(i));
            }
            
        }

    }

    public void storeAnalyticTaskParameters(List<Parameter> listOfAnalyticTaskParameters, String actionName, String adjustmentCase, String analyticTask) {

        for (Parameter parameter : listOfAnalyticTaskParameters) {

            String sql = "INSERT INTO AnalyticTask (ActionName, AdjustmentCase, AnalyticTask, ParameterName, ParameterType, ParameterValue) "
                    + "VALUES ('" + actionName + "', '" + adjustmentCase + "', '" + analyticTask + "', '" + parameter.getParameterName() + "', '" + parameter.getType() + "', '" + parameter.getValue() + "')";
            System.err.println(sql);

            connectionManager.ExecuteUpdate(sql);

        }

    }

    public void storeAdjustmentCaseParameters(List<Parameter> listOfAdjustmentCaseParameters, String actionName, String adjustmentCase) {

        for (Parameter parameter : listOfAdjustmentCaseParameters) {
            String sql = "INSERT INTO AdjustmentActionParameter (ActionName, AdjustmentCase, ParameterName, ParameterType, ParameterValue) "
                    + "VALUES ('" + actionName + "', '" + adjustmentCase + "',  '" + parameter.getParameterName() + "', '" + parameter.getType() + "', '" + parameter.getValue() + "')";
            System.err.println(sql);
            connectionManager.ExecuteUpdate(sql);

        }
    }

    public void storePrerequisiteActions(List<String> prerequisiteActionlist, String dependentAction) {

        for (String prerequisiteAction : prerequisiteActionlist) {
            String sql = "INSERT INTO PrerequisiteAction (ActionName, PrerequisiteAction) VALUES ('" + dependentAction + "', '" + prerequisiteAction + "')";
            connectionManager.ExecuteUpdate(sql);
        }

    }

    public void storeArtifact(Artifact artifact, String actionName) {

        String sql = "INSERT INTO Artifact (ArtifactName, ActionName, ArtifactDescription, Location, Type, RESTfulAPI, HttpMethod) "
                + "VALUES ('" + artifact.getName() + "','" + actionName + "','" + artifact.getDescription() + "','" + artifact.getLocation() + "','" + artifact.getType() + "','" + artifact.getRestfulAPI() + "','" + artifact.getHttpMethod() + "')";
        connectionManager.ExecuteUpdate(sql);
    }

    public void storeParameters(List<Parameter> listOfParameters, String actionName) {

        for (Parameter parameter : listOfParameters) {
            String sql = "INSERT INTO MonitoringActionParameter (ActionName, ParameterName, ParameterType, ParameterValue) "
                    + "VALUES ('" + actionName + "','" + parameter.getParameterName() + "','" + parameter.getType() + "','" + parameter.getValue() + "')";
            connectionManager.ExecuteUpdate(sql);
        }

    }

    public List<MonitoringAction> getMonitoringActionList() {
        String sql = "SELECT * from PrimitiveAction WHERE ActionType='MonitoringAction'";
        List<MonitoringAction> listOfActions = new ArrayList<>();

        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String actionName = rs.getString("ActionName");
                String associatedQoRMetric = rs.getString("AssociatedQoRMetric");

                Artifact artifact = getArtifact(actionName);
                List<Parameter> listOfParameters = getListOfParametersForMonitoringAction(actionName);

                MonitoringAction monitoringAction = new MonitoringAction(actionName, actionName, artifact, associatedQoRMetric, listOfParameters);
                listOfActions.add(monitoringAction);
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }

        return listOfActions;

    }
    
    

    public List<AdjustmentAction> getAdjustmentActionList() {
        String sql = "SELECT * from PrimitiveAction WHERE ActionType='AdjustmentAction'";
        List<AdjustmentAction> listOfActions = new ArrayList<>();

        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String actionName = rs.getString("ActionName");
                String associatedQoRMetric = rs.getString("AssociatedQoRMetric");
                
                Artifact artifact = getArtifact(actionName);
                List<String> prerequisiteActionList = getPrerequisiteActions(actionName);
                
                List<AdjustmentCase> listOfAdjustmentCases = getAdjustmentCases(actionName, associatedQoRMetric);
                AdjustmentAction adjustmentAction = new AdjustmentAction(actionName, actionName, artifact, associatedQoRMetric, prerequisiteActionList, listOfAdjustmentCases);
               
                
                listOfActions.add(adjustmentAction);
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }

        return listOfActions;

    }
    
    
  
    
    public List<AdjustmentCase> getAdjustmentCases(String actionName, String associatedQoRMetric){
        
        
        
        String sql = "SELECT * FROM AdjustmentCase WHERE ActionName='"+actionName+"'";
        
        List<AdjustmentCase> listOfAdjustmentCases = new ArrayList<>();
        
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        int i =1;
        try {
            while (rs.next()) {

                String adjustmentCase = rs.getString("AdjustmentCase");
                double estimatedResultFrom = rs.getDouble("EstimatedResultFrom");
                double estimatedResultTo = rs.getDouble("EstimatedResultTo");
                String conditionID = "c_" + associatedQoRMetric + "_" + i++;
                
                List<Parameter> listOfParameters = getAdjustmentCasesParameters(actionName, adjustmentCase);
                AdjustmentCase adjustmentCaseObj = new AdjustmentCase(
                        new MetricCondition(associatedQoRMetric, conditionID, estimatedResultFrom, estimatedResultTo), null, listOfParameters);
                
                listOfAdjustmentCases.add(adjustmentCaseObj);
                
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        
        return listOfAdjustmentCases;
    }
    
    public List<Parameter> getAdjustmentCasesParameters(String actionName, String adjustmentCase){
        
        List<Parameter> listOfParameters = new ArrayList<>();
        
        String sql = "SELECT * FROM AdjustmentActionParameter WHERE ActionName='"+actionName+"' AND AdjustmentCase='"+adjustmentCase+"'";
        
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String parameterName = rs.getString("ParameterName");
                String parameterType = rs.getString("ParameterType");
                String parameterValue = rs.getString("ParameterValue");
                
                Parameter parameter = new Parameter(parameterName, parameterType, parameterValue);
                listOfParameters.add(parameter);
                    
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return listOfParameters;
    }
    
    
    public List<String> getPrerequisiteActions(String actionName){
        
        String sql = "SELECT * FROM PrerequisiteAction WHERE ActionName='"+actionName+"'";
        
        List<String> listOfPrerequisteActions = new ArrayList<>();
        
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String prerequisiteAction = rs.getString("PrerequisiteAction");
                listOfPrerequisteActions.add(prerequisiteAction);
                
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return listOfPrerequisteActions;
        
    }
    
    
    

    public List<ResourceControlAction> getResourceControlActionList() {
        String sql = "SELECT * from PrimitiveAction WHERE ActionType='ResourceControlAction'";
        List<ResourceControlAction> listOfActions = new ArrayList<>();

        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String actionName = rs.getString("ActionName");
                String associatedQoRMetric = rs.getString("AssociatedQoRMetric");
                
                List<ResourceControlCase> listOfResourceControlCases = getListOfResourceControlCase(actionName, associatedQoRMetric);
                
                ResourceControlAction resourceControlAction = new ResourceControlAction(actionName, associatedQoRMetric, listOfResourceControlCases);
                listOfActions.add(resourceControlAction);
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }

        return listOfActions;

    }

    
    public List<ResourceControlCase> getListOfResourceControlCase(String actionName, String associatedQoRMetric){

        List<ResourceControlCase> listOfResourceControlCases = new ArrayList<>();
        
        String sql = "SELECT * from ResourceControlCase WHERE ResourceControlActionName='"+actionName+"'";

        ResultSet rs = connectionManager.ExecuteQuery(sql);
        int i=1;
        try {
            while (rs.next()) {

                String resourceControlActionName = rs.getString("ResourceControlActionName");
                String resourceControlCase = rs.getString("ResourceControlCase");
                double estimatedResultFrom = rs.getDouble("EstimatedResultFrom");
                double estimatedResultTo = rs.getDouble("EstimatedResultTo");
                
                List<ResourceControlStrategy> listOfResourceControlStrategies = getListOfResourceControlStrategies(actionName, resourceControlCase);
                
                String conditionID = "c_" + associatedQoRMetric + "_" + i++;
                ResourceControlCase resourceControlCaseObj = new ResourceControlCase(
                        new MetricCondition(associatedQoRMetric, conditionID, estimatedResultFrom, estimatedResultTo), null, listOfResourceControlStrategies);
                listOfResourceControlCases.add(resourceControlCaseObj);
                
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return listOfResourceControlCases;
        
    }
    
    public List<ResourceControlStrategy> getListOfResourceControlStrategies(String actionName, String adjustmenCase){
        
        List<ResourceControlStrategy> listOfResourceControlStrategies = new ArrayList<>();
        
        String sql = "SELECT * from ResourceControlStrategy WHERE ResourceControlActionName='"+actionName+"' AND ResourceControlCase='"+adjustmenCase+"'";
        
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String controlMetric = rs.getString("ControlMetric");
                String effectedActionName = rs.getString("EffectedActionName");
                double scaleInConditionFrom = rs.getDouble("ScaleInConditionFrom");
                double scaleInConditionTo = rs.getDouble("ScaleInConditionTo");
                double scaleOutConditionFrom = rs.getDouble("ScaleOutConditionFrom");
                double scaleOutConditionTo = rs.getDouble("ScaleOutConditionTo");
                
                ResourceControlStrategy resourceControlStrategy = new ResourceControlStrategy(
                        new MetricCondition(controlMetric, "", scaleInConditionFrom, scaleInConditionTo),
                        new MetricCondition(controlMetric, "", scaleOutConditionFrom, scaleOutConditionTo), 
                        controlMetric, effectedActionName);
                
                listOfResourceControlStrategies.add(resourceControlStrategy);
                    
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return listOfResourceControlStrategies;
        
    }
    
    public Artifact getArtifact(String actionName){
        
        Artifact artifact = null;
        
        String sql = "SELECT * FROM Artifact WHERE ActionName='"+actionName+"'";
        
        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String artifactName = rs.getString("ArtifactName");
                String description = rs.getString("ArtifactDescription");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                String restfulApi = rs.getString("RESTfulAPI");
                String httpMethod = rs.getString("HttpMethod");
                
                artifact = new Artifact(artifactName, description, location, type, restfulApi, httpMethod);

            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        return artifact;
        
    }

    public List<Parameter> getListOfParametersForMonitoringAction(String monitoringActionName) {

        
        
        List<Parameter> listOfParameters = new ArrayList<>();
        String sql = "SELECT * FROM MonitoringActionParameter WHERE ActionName='" + monitoringActionName + "'";

        ResultSet rs = connectionManager.ExecuteQuery(sql);
        try {
            while (rs.next()) {

                String parameterName = rs.getString("ParameterName");
                String parameterType = rs.getString("ParameterType");
                String parameterValue = rs.getString("ParameterValue");
                Parameter parameter = new Parameter(parameterName, parameterType, parameterValue);
                listOfParameters.add(parameter);

            }
            rs.close();

        } catch (Exception ex) {
            System.err.println(ex);
        }

        return listOfParameters;

    }
}
