
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

import at.ac.tuwien.dsg.depic.common.entity.runtime.DBType;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ExecutionSession;
import at.ac.tuwien.dsg.depic.common.entity.runtime.ExecutionStep;
import java.util.ArrayList;
import java.util.List;


public class DEPExecutionEngine {

    private static List<ExecutionSession> listOfExecutionSessions;

    public static void addExecutionSession(ExecutionSession executionSession) {

        if (listOfExecutionSessions == null) {
            listOfExecutionSessions = new ArrayList<ExecutionSession>();

        }

        for (ExecutionStep executionStep : executionSession.getListOfExecutionSteps()) {
            List<String> listOfExecutionActions = executionStep.getListOfExecutionActions();
            List<String> listOfWaitingActions = new ArrayList<String>();
            List<String> listOfExecutingActions = new ArrayList<String>();
            List<String> listOfFinishedActions = new ArrayList<String>();

            for (String exeAction : listOfExecutionActions) {
                System.out.println("Executiong STEP: Adding to waiting: " + exeAction);
                String waitingAction = new String(exeAction);
                listOfWaitingActions.add(waitingAction);
            }

            executionStep.setExecutingActions(listOfExecutingActions);
            executionStep.setWaitingActions(listOfWaitingActions);
            executionStep.setFinishedActions(listOfFinishedActions);

        }

        listOfExecutionSessions.add(executionSession);

    }

    public static boolean isFinished(String sessionID) {

        boolean isFinished = true;

        for (ExecutionSession executionSession : listOfExecutionSessions) {

            if (executionSession.getMonitoringSession().getSessionID().equals(sessionID)) {

                List<ExecutionStep> listOfExecutionSteps = executionSession.getListOfExecutionSteps();

                for (ExecutionStep executionStep : listOfExecutionSteps) {

                    if (!(executionStep.getExecutingActions().isEmpty() && executionStep.getWaitingActions().isEmpty())) {

                        isFinished = false;
                    }

                }

            }

        }

        return isFinished;
    }

    public static void checkExecution(String sessionID) {

        boolean isBreak = false;

        for (ExecutionSession executionSession : listOfExecutionSessions) {

            if (executionSession.getMonitoringSession().getSessionID().equals(sessionID)) {

                List<ExecutionStep> listOfExecutionSteps = executionSession.getListOfExecutionSteps();

                for (ExecutionStep executionStep : listOfExecutionSteps) {

                    System.out.println("No of Executing Actions: " + executionStep.getExecutingActions().size());
                    System.out.println("No of Finished Actions: " + executionStep.getFinishedActions().size());
                    System.out.println("No of Waiting Actions: " + executionStep.getWaitingActions().size());

                    if (executionStep.getExecutingActions().isEmpty() && executionStep.getWaitingActions().isEmpty()) {
                        // skip

                    } else if (!executionStep.getExecutingActions().isEmpty() && executionStep.getWaitingActions().isEmpty()) {
                        // skip

                    } else if (executionStep.getExecutingActions().isEmpty() && !executionStep.getWaitingActions().isEmpty()) {
                        for (String nextExeAction : executionStep.getWaitingActions()) {
                            System.out.println("STARTING ACTION: " + nextExeAction);
                            ActionExecutor actionExecutor = new ActionExecutor(executionSession, nextExeAction);
                            actionExecutor.start();
                            isBreak = true;
                        }

                        if (isBreak) {
                            break;
                        }

                    } else if (!executionStep.getExecutingActions().isEmpty() && !executionStep.getWaitingActions().isEmpty()) {
                        //skip
                    }

                    if (isBreak) {
                        break;
                    }

                }

                if (isBreak) {
                    break;
                }

            }

            if (isBreak) {
                break;
            }

        }

    }

    public static void actionExecuting(String sessionID, String actionID) {
        for (ExecutionSession executionSession : listOfExecutionSessions) {

            if (executionSession.getMonitoringSession().getSessionID().equals(sessionID)) {

                List<ExecutionStep> listOfExecutionSteps = executionSession.getListOfExecutionSteps();

                for (ExecutionStep executionStep : listOfExecutionSteps) {
                    executionStep.getWaitingActions().remove(actionID);
                    executionStep.getExecutingActions().add(actionID);

                }
            }

        }
    }

    public static void actionExecutionFinished(String sessionID, String actionID) {

        for (ExecutionSession executionSession : listOfExecutionSessions) {

            if (executionSession.getMonitoringSession().getSessionID().equals(sessionID)) {

                List<ExecutionStep> listOfExecutionSteps = executionSession.getListOfExecutionSteps();

                for (ExecutionStep executionStep : listOfExecutionSteps) {
                    executionStep.getExecutingActions().remove(actionID);
                    executionStep.getFinishedActions().add(actionID);

                }
            }

        }

    }

}
