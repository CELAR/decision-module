/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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


/**
 *
 * @author Jun
 */
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
