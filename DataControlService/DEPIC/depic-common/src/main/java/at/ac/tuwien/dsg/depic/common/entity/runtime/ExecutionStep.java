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
package at.ac.tuwien.dsg.depic.common.entity.runtime;

import java.util.List;


public class ExecutionStep {
    List<String> listOfPrerequisiteActions;
    List<String> listOfExecutionActions;
    List<String> waitingActions;
    List<String> executingActions;
    List<String> finishedActions;

    public ExecutionStep() {
    }

    public List<String> getListOfPrerequisiteActions() {
        return listOfPrerequisiteActions;
    }

    public void setListOfPrerequisiteActions(List<String> listOfPrerequisiteActions) {
        this.listOfPrerequisiteActions = listOfPrerequisiteActions;
    }

    public List<String> getListOfExecutionActions() {
        return listOfExecutionActions;
    }

    public void setListOfExecutionActions(List<String> listOfExecutionActions) {
        this.listOfExecutionActions = listOfExecutionActions;
    }

    public List<String> getWaitingActions() {
        return waitingActions;
    }

    public void setWaitingActions(List<String> waitingActions) {
        this.waitingActions = waitingActions;
    }

    public List<String> getExecutingActions() {
        return executingActions;
    }

    public void setExecutingActions(List<String> executingActions) {
        this.executingActions = executingActions;
    }

    public List<String> getFinishedActions() {
        return finishedActions;
    }

    public void setFinishedActions(List<String> finishedActions) {
        this.finishedActions = finishedActions;
    }

    

    
    
    
}
