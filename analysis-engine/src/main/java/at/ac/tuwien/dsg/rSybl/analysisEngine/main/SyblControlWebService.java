/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */


package at.ac.tuwien.dsg.rSybl.analysisEngine.main;

import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;




@WebService(serviceName="rSYBL")//, endpointInterface ="at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeAPI.SYBLAPIInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class SyblControlWebService {
	private ControlService controlService;
	public SyblControlWebService(){
		controlService=new ControlService();
	}
	@WebMethod
	public void processAnnotation(String entity,SYBLAnnotation annotation){
		try {
			controlService.processAnnotation(entity, annotation);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	}

}
