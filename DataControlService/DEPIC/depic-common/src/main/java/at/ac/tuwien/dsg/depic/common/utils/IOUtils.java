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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


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
