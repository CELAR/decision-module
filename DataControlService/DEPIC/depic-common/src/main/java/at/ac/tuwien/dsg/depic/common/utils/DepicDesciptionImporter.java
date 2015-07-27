/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.utils;

import at.ac.tuwien.dsg.depic.common.entity.runtime.DepicDescription;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Jun
 */
public class DepicDesciptionImporter {

    public DepicDescription importDescription(String classPath) {
        DepicDescription depicDescription = null;
        
        Configuration cfg = new Configuration(classPath);
        String filePath = cfg.getConfigPath();
        IOUtils iou = new IOUtils(filePath);
 
        String depicDescriptionXML = iou.readData("depicDescription.xml");
        try {
            depicDescription = JAXBUtils.unmarshal(depicDescriptionXML, DepicDescription.class );
            
            System.out.println(depicDescription.getDaaSDescription().getDaasName());
        } catch (JAXBException ex) {
            
        }
        
        return depicDescription;
    }

}
