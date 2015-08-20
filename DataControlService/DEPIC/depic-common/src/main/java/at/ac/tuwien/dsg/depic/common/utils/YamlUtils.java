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


import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.File;
import java.io.FileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;



public class YamlUtils {
    
    static String filePath;

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        YamlUtils.filePath = filePath;
    }
    
    
    
    public static void toYaml(Object obj,String fileName) {
        try {
            YamlWriter writer = new YamlWriter(new FileWriter(filePath+"/"+fileName));
            writer.write(obj);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(YamlUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static <T> T fromYaml(Class<T> className, String fileName){
        T obj = null;
        try {
            YamlReader reader = new YamlReader(new FileReader(filePath+"/"+fileName));
            obj = reader.read(className);
 
        } catch (Exception ex) {
            Logger.getLogger(YamlUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  obj;
    }
    
    
    
    
    public static <T> T unmarshallYaml(Class<T> className, String yml){
        
        UUID tempID = UUID.randomUUID(); 
        
        IOUtils iou = new IOUtils(filePath);
        iou.overWriteData(yml, tempID.toString()+".yaml");
        String path = filePath+"/"+tempID.toString()+".yaml";
        T obj = null;
        try {
            YamlReader reader = new YamlReader(new FileReader(path));
            obj = reader.read(className);
            File file = new File(path);
            file.delete();
        } catch (Exception ex) {
            Logger.getLogger(YamlUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        return  obj;
    }
    
    
    public static <T> String marshallYaml(Class<T> className, Object obj){
       
        UUID tempID = UUID.randomUUID();
        String path = filePath+"/"+tempID.toString()+".yaml";
        try {
            YamlWriter writer = new YamlWriter(new FileWriter(filePath+"/"+tempID.toString()+".yaml"));
    
            
            writer.write(obj);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(YamlUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        IOUtils iou = new IOUtils(filePath);

        String yml =iou.readData(tempID.toString()+".yaml");
        
        File file = new File(path);
        file.delete();
        
        
        return yml;
    }
    
   
}
