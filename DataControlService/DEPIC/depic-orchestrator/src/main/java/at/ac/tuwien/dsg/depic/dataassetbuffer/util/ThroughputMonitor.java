/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.util;

import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Jun
 */
public class ThroughputMonitor {
    private static final List<Operation> objList= new LinkedList<Operation>();
    private static final int maxBuffer = 5000;
    private static final int timeInterval = 3;

    
    public static void trackingLoad(DataPartitionRequest request){
        
        String daStr = request.getEdaas()+";"+request.getDataAssetID()+";"+request.getCustomerID();
            
        long timeStamp = System.currentTimeMillis();
        System.out.println("Tracking: " + timeStamp);
        Operation operation = new Operation(timeStamp, daStr);
        if (objList.size() < maxBuffer) {
            objList.add(operation);
        } else {
            objList.remove(0);
            objList.add(operation);
        }
    }
    
    public static double calculateThroughput(DataPartitionRequest request){
         String daStr = request.getEdaas()+";"+request.getDataAssetID()+";"+request.getCustomerID();
        int counter=0;
        long currentTimeStamp = System.currentTimeMillis();
        System.out.println("Calculate Throughout - noObj: " + objList.size());
        for (int i=objList.size()-1;i>=0;i--){
            
            Operation operation = objList.get(i);
            
            long timeStamp = operation.getTimeStamp();         
            long interval = currentTimeStamp-timeStamp;
            
            System.out.println("gap: " + interval);
     
            String daRequest = operation.getDataAssetRequest();
        
            if (interval < timeInterval * 1000) {
                if (daStr.equals(daRequest)) {
                    counter++;
                }
            } else {
                
                break;
            }
        }
      
        double throughput = (counter*1.0)/(timeInterval*1.0);
       
        System.out.println("counter: " + counter);
        System.out.println("throughput: " + throughput);
        
        return throughput;
    }
    
    
    
    
}
