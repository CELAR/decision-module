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

package at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {

    private static  Properties configuration ;
    private static String languageDescriptionFile="/config/languageDescription.xml";
    static{
        configuration = new Properties();
        try {
//
			InputStream is = Configuration.class.getClassLoader().getResourceAsStream("/config.properties");
			configuration.load(is);
//			configuration.load(new FileReader("./config.properties"));

        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

    public static String getModelDescrFile(){
    	return configuration.getProperty("CloudServiceModelDescription");
    }
    
    public static String getLanguageDescription(){
    	return languageDescriptionFile;
        //return configuration.getProperty("SYBLLanguageDescription");
     }
    
 
    public static String getDirectivesPath(){
    	return configuration.getProperty("SYBLDirectives");
    }
    public static String getMonitoringServiceURL(){
    	return configuration.getProperty("MonitoringServiceURL");
    }
    public static long getRefreshPeriod(){
    	try{
    	return Integer.parseInt(configuration.getProperty("REFRESH_PERIOD"));
    	}catch(Exception e){
    		SYBLDirectivesEnforcementLogger.logger.error("The refresh period(in the config file) needs to be an integer");
    		return 120000;
    	}
    }

}