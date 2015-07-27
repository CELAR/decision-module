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



import at.ac.tuwien.dsg.depic.common.entity.runtime.MonitoringMetric;
import com.sun.jersey.api.client.Client;  
import com.sun.jersey.api.client.ClientResponse;  
import com.sun.jersey.api.client.WebResource; 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class RestfulWSClient {

    
    private String ip;
    private String port;
    private String resource;
    private String url;
  
    public RestfulWSClient(String ip, String port, String resource) {
        this.ip = ip;
        this.port = port;
        this.resource = resource;
        url = "http://" + ip + ":" + port + resource;
      
    }

    public RestfulWSClient(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String callPutMethod(String xmlString) {
        String rs="";
          try {  
      
        
            Client client = Client.create();  
            WebResource webResource = client.resource(url);  

            ClientResponse response = webResource.type("application/xml").accept("application/xml").put(ClientResponse.class, xmlString);
            if (response.getStatus() != 200) {  
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());  
            }  
   
        
            String output = response.getEntity(String.class);  
            System.out.println("\n============getCResponse============");  
            System.out.println(output);  
   
    
          
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return rs;
    }
    
    public MonitoringMetric callMonitoringService(String xmlString) {
        MonitoringMetric output = null;
          try {  
      
        
            Client client = Client.create();  
            WebResource webResource = client.resource(url);  

            ClientResponse response = webResource.type("application/xml").accept("application/xml").put(ClientResponse.class, xmlString);
            if (response.getStatus() != 200) {  
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());  
            }  
   
        
               output = response.getEntity(MonitoringMetric.class);  
            System.out.println("\n============getCResponse============");  
 
   
    
          
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return output;
    }
    
    
    public String callJcatascopiaMetricWS(String metricName, String uri){
        
        String metricID = "";
        
        try {


            
            Client client = Client.create();
          
           
            //WebResource webResource = client.resource("http://128.130.172.230:8080/JCatascopia-Web/restAPI/metrics/29e6550085ae4ee193532cd51fae39c1:pgActiveConnections");
            
            WebResource webResource = client.resource(uri);

            ClientResponse response = webResource
                   
                    .accept("application/json")
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);
            System.out.println("\n============getResponse============");
            System.out.println(output);
            
            
            JSONObject json = (JSONObject)new JSONParser().parse(output);
            String allMetricsStr = json.get("metrics").toString();
            
            System.out.println(allMetricsStr);
            
             try {

                JSONArray nameArray =(JSONArray)new JSONParser().parse(allMetricsStr);
                 System.out.println(nameArray.size());
                 for(Object js : nameArray){
                     JSONObject element = (JSONObject)js;
                     
                     if (metricName.equals(element.get("name"))) {
                         metricID = element.get("metricID").toString();
                     }
                     
                 }


            } catch (Exception e) {
                   
            }

        } catch (Exception e) {
          
        }
        
        return  metricID;

    }
    
    public String getJCMetricValue(String metricID, String uri){
        
        String metricValue = "";
        
        try {


            
            Client client = Client.create();
            
            String jcURL = uri + metricID;
           
            WebResource webResource = client.resource(jcURL);
       
            ClientResponse response = webResource
                   
                    .accept("application/json")
                    .get(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);
            System.out.println("\n============getResponse============");
            System.out.println(output);
            
        
             
            JSONObject json = (JSONObject)new JSONParser().parse(output);
            String allMetricsStr = json.get("values").toString();
            
            System.out.println(allMetricsStr);
            
             try {

                JSONArray nameArray =(JSONArray)new JSONParser().parse(allMetricsStr);
                 System.out.println(nameArray.size());
                 for(Object js : nameArray){
                     JSONObject element = (JSONObject)js;
                     
                     if (metricID.equals(element.get("metricID"))) {
                         metricValue = element.get("value").toString();
                     }
                     
                 }


            } catch (Exception e) {
                   
            }


        } catch (Exception e) {
          
        }
        
        return  metricValue;

    }
    
    public int callPutMethodRC(String xmlString) {
        int statusCode =0;
        

        return statusCode;
    }

    public void callPostMethod(String xmlString) {


    }
    
    
    public String callGetMethod() {
      String rs ="";
        return rs;

    }

}
