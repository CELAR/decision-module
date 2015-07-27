/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.entity.dataanalyticsfunction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jun
 */


public enum DataAssetForm {
    CSV("CSV"), XML("XML"), FIGURE("FIGURE");

    private String dataAssetForm;

    private DataAssetForm(String dataAssetForm) {
        this.dataAssetForm = dataAssetForm;
    }

    public String getDataAssetForm() {
        return dataAssetForm;
    }
}
