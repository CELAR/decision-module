/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 * @author Jun
 */
public class IOUtils {
  
    private String tomcatTempFolder;

    public IOUtils() {
    
        tomcatTempFolder = System.getProperty("java.io.tmpdir");
        
    }

    public IOUtils(String tomcatTempFolder) {
        this.tomcatTempFolder = tomcatTempFolder;
    }

    public void writeData(String data, String fileName) {

        fileName =  tomcatTempFolder +"/" + fileName;
        FileWriter fstream;
        try {
            fstream = new FileWriter(fileName, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(data);

            out.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    public void overWriteData(String data, String fileName) {

        fileName =  tomcatTempFolder +"/" + fileName;
        FileWriter fstream;
        try {
            fstream = new FileWriter(fileName, false);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(data);

            out.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public  String readData(String fileName) {

        fileName =  tomcatTempFolder +"/" + fileName;
        String data = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
          //  BufferedReader br = new BufferedReader(new InputStreamReader(
           //           new FileInputStream(fileName), "UTF8"));
            
            
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        return data;
    }

}
