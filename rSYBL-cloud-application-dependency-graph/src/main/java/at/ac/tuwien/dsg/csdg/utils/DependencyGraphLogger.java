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

package at.ac.tuwien.dsg.csdg.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;



public class DependencyGraphLogger {
	   public static Logger logger ;
	   static{
//		      SimpleLayout layout = new SimpleLayout();    
//			   FileAppender appender=null;
//			   Date date = new Date();
//			try {
		    String date = new Date().toString();
            date = date.replace(" ", "_");
            date = date.replace(":", "_");
            System.getProperties().put("recording_date", date);

				PropertyConfigurator.configure(Configuration.class.getResourceAsStream("/config/Log4j.properties"));
							logger = Logger.getLogger("rSYBLLogger");
							logger.info("Cloud application dependency graph ");
//				appender = new FileAppender(layout,"../logs/rSYBL_DependencyGraphLogger_"+date.getHours()+"_"+date.getMinutes()+".log",false);
//			
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}    
//			      logger.addAppender(appender);
//
//			      logger.setLevel((Level) Level.ALL);

	   }
}
