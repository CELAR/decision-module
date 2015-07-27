/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.common.utils;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Jun
 */
public class JAXBUtils {

    public static <T> String marshal(Object source, Class<T> configurationClass) throws JAXBException {
        JAXBContext jAXBContext = JAXBContext.newInstance(configurationClass);
        StringWriter writer = new StringWriter();
        jAXBContext.createMarshaller().marshal(source, writer);
        return writer.toString();
    }

    public static <T> T unmarshal(String ObjXml, Class<T> configurationClass) throws JAXBException {
        JAXBContext bContext = JAXBContext.newInstance(configurationClass);
        Unmarshaller um = bContext.createUnmarshaller();
        T obj = (T) um.unmarshal(new StringReader(ObjXml));
        
        return obj;
    }

}
