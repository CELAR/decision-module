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

package at.ac.tuwien.dsg.rSybl.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;



public class SYBLControlClient {
	protected String REST_API_URL="http://localhost/rSYBL-analysis-engine-0.1-SNAPSHOT/restWS";
    public void setApplicationDescription(String appDescription) {
	   

        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(REST_API_URL + "/setApplicationDescriptionCELAR");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/json");

            //write message body
            OutputStream os = connection.getOutputStream();
            os.write(appDescription.getBytes(Charset.forName("UTF-8")));
            os.flush();
            os.close();

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, line);
                }
            }

        } catch (Exception e) {
            Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
    }
    public void setApplicationDeployment(String appDescription) {
 	   

        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(REST_API_URL + "/setApplicationDeploymentDescriptionCELAR");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/json");

            //write message body
            OutputStream os = connection.getOutputStream();
            os.write(appDescription.getBytes(Charset.forName("UTF-8")));
            os.flush();
            os.close();

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, line);
                }
            }

        } catch (Exception e) {
            Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
    }
}
