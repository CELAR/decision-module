/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.mela.common.jaxbEntities.monitoringConcepts.MonitoredElementMonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.dbalancer.BalancerInfo;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.dbalancer.BalancerType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.dbalancer.BalancingStatus;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.dbalancer.StartBalancer;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin.MELA_API;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;

/**
 *
 * @author Georgiana
 */
public class ClusterBalancing {

    private Node cloudService;
    private String API_URL = "";

    public ClusterBalancing(Node cloudService) {
        this.cloudService = cloudService;
        API_URL = Configuration.getEnforcementServiceURL();

    }

    private BalancingStatus checkStatus() {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(API_URL + "balancer/id/status");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    RuntimeLogger.logger.error(line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            JAXBContext jAXBContext = JAXBContext.newInstance(BalancingStatus.class);
            BalancingStatus status = (BalancingStatus) jAXBContext.createUnmarshaller().unmarshal(inputStream);


            return status;
        } catch (Exception e) {
            RuntimeLogger.logger.error("DBalancer error " + e.getMessage() + " " + e.getCause());
            if (connection != null) {
                connection.disconnect();
            }
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private BalancerInfo checkInfo() {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(API_URL + "balancer/info");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    RuntimeLogger.logger.error(line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            JAXBContext jAXBContext = JAXBContext.newInstance(BalancerInfo.class);
            BalancerInfo info = (BalancerInfo) jAXBContext.createUnmarshaller().unmarshal(inputStream);

        
            return info;
        } catch (Exception e) {
            RuntimeLogger.logger.error("DBalancer error " + e.getMessage() + " " + e.getCause());
            return null;
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void startBalancing() {
        URL url = null;
        StartBalancer startBalancer=new StartBalancer();
        HttpURLConnection connection = null;
        try {
            url = new URL(API_URL + "balancer/start");
         connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/xml");
                connection.setRequestProperty("Accept", "application/json");

                //write message body
                OutputStream os = connection.getOutputStream();
                JAXBContext jaxbContext = JAXBContext.newInstance(StartBalancer.class);
                jaxbContext.createMarshaller().marshal(startBalancer, os);
                StringWriter stringWriter = new StringWriter();
                jaxbContext.createMarshaller().marshal(startBalancer, stringWriter);
                
                RuntimeLogger.logger.info(stringWriter.toString());
                os.flush();
                os.close();
                InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    RuntimeLogger.logger.error(line);
                }
            }
        }catch (Exception e) {
            RuntimeLogger.logger.error("DBalancer error " + e.getMessage() + " " + e.getCause());
        
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
    }
}
