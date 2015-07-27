/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.dataassetbuffer.util;

/**
 *
 * @author Jun
 */
public class Operation {
    long timeStamp;
    String dataAssetRequest;

    public Operation() {
    }

    public Operation(long timeStamp, String dataAssetRequest) {
        this.timeStamp = timeStamp;
        this.dataAssetRequest = dataAssetRequest;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDataAssetRequest() {
        return dataAssetRequest;
    }

    public void setDataAssetRequest(String dataAssetRequest) {
        this.dataAssetRequest = dataAssetRequest;
    }
    
    
}
