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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;


public class Configuration {
    
    private String classPath;

    public Configuration(String classPath) {
        this.classPath = classPath;
    }
    
    

    public String getConfig(String configureName) {

        String path = getConfigPath();

        Properties prop = new Properties();
        String configString = "";
        InputStream input = null;

        try {
            input = new FileInputStream(path + "/config.properties");
            prop.load(input);
            configString = prop.getProperty(configureName);

        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }

        return configString;
    }
    
    public void setConfig(String configureName, String configureValue) {

        String path = getConfigPath();

        Properties prop = new Properties();
       
        InputStream input = null;

        try {
            input = new FileInputStream(path + "/config.properties");
            prop.load(input);
           

        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
        
        OutputStream output = null;
        prop.setProperty(configureName, configureValue);
        try {
            output = new FileOutputStream(path + "/config.properties");
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
       
    }
  

    
    
    public String getConfigPath(){
        String path = classPath;

        int index = path.indexOf("/classes/at/ac");
        path = path.substring(0, index);
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.logInfo(ex.toString());
        }
        
        return path;
    }

    
    
    public String getArtifactPath(){
       

        String path = Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        int index = path.indexOf("/classes/at/ac");
        path = path.substring(0, index);
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
           System.err.println(ex);
        }

        path = path + "/classes/artifact";
        
        Logger.logInfo("Artifact Path: -" + path + "-");
        
        
        
        return path;
    
    }
}
