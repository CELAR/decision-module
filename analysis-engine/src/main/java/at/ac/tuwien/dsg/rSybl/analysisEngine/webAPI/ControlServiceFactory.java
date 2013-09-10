package at.ac.tuwien.dsg.rSybl.analysisEngine.webAPI;

import at.ac.tuwien.dsg.rSybl.analysisEngine.main.ControlService;

public class ControlServiceFactory {
	private static ControlService controlService;
	public static ControlService getControlServiceInstance(){
		if (controlService==null){
			controlService=new ControlService();
		}
		
		return controlService;
	}
}
