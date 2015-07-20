package at.ac.tuwien.dsg.celar.mela.analysisservice;

/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group
 * E184
 *
 * This work was partially supported by the European Commission in terms of the
 * CELAR FP7 project (FP7-ICT-2011-8 \#317790)
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
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import at.ac.tuwien.dsg.mela.analysisservice.api.ElasticityAnalysisService;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MonitoredElement;
import com.wordnik.swagger.annotations.Api;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.cxf.helpers.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Provider
@Path("/")
@Api(value = "/", description = "The ElasticityAnalysisService is the entry point for all elasticity related monitoring data")
public class SpaceAndPathwayAnalysisServiceAPI extends ElasticityAnalysisService {

    @Value("${cost.ip:localhost}")
    private String costServiceIP;

    /**
     *
     * @param serviceID service to scale in
     * @param monitoredElementID id of element to scale in
     * @param monitoredElementlevel level of element to scale in
     * @param unitInstanceID instance ID of the element to be scaled in 
     * example /SERVICE_1/cost/evaluate/costefficiency/scalein/EventProcessingUnit/VM/10.0.0.1/plain
     * @return the cost efficiency of scaling in the unit
     */
    @GET
    @Path("/{serviceID}/cost/evaluate/costefficiency/scalein/{monitoredElementID}/{monitoredElementLevel}/{unitInstanceID}/plain")
    @Produces("text/plain")
    public String getCostEfficiencyIfScalingIn(@PathParam("serviceID") String serviceID,
            @PathParam("monitoredElementID") String monitoredElementID,
            @PathParam("monitoredElementLevel") String monitoredElementlevel,
            //i.e., IP of VM to scale down
            @PathParam("unitInstanceID") String unitInstanceID
    ) {
        return redirectToCost("/" + serviceID + "/cost/evaluate/costefficiency/scalein/" + monitoredElementID + "/" + monitoredElementlevel + "/" + unitInstanceID + "/plain");
    }

    /**
     * Will evaluate all units of a particular instance
     *
     * @param serviceID service to scale in
     * @param monitoredElementID id of element to scale in
     * @param monitoredElementlevel level of element to scale in
     * @return a JSON dictionary of the form ip,efficiency,lifetime for all
     * instances of a unit
     */
    @GET
    @Path("/{serviceID}/cost/evaluate/costefficiency/scalein/{monitoredElementID}/{monitoredElementLevel}/plain")
    @Produces("text/plain")
    public String getCostEfficiencyIfScalingIn(@PathParam("serviceID") String serviceID,
            @PathParam("monitoredElementID") String monitoredElementID,
            @PathParam("monitoredElementLevel") String monitoredElementlevel
    ) {
        return redirectToCost("/" + serviceID + "/cost/evaluate/costefficiency/scalein/" + monitoredElementID + "/" + monitoredElementlevel + "/plain");
    }

    /**
     * Evaluated the cost for specific instances of a unit. instances names
     * should be separated by -
     *
     * @param serviceID service to scale in
     * @param monitoredElementID id of element to scale in
     * @param monitoredElementlevel level of element to scale in
     * example: /SERVICE_1/cost/evaluate/costefficiency/scalein/more/EventProcessingUnit/VM/10.0.0.1-10.0.0.5-10.0.0.6/plain
     * @return a JSON dictionary of the form ip,efficiency,lifetime for
     * specified instances of a unit
     */
    @GET
    @Path("/{serviceID}/cost/evaluate/costefficiency/scalein/more/{monitoredElementID}/{monitoredElementLevel}/{unitInstanceIDs}/plain")
    @Produces("text/plain")
    public String getCostEfficiencyIfScalingInForMoreIPs(@PathParam("serviceID") String serviceID,
            @PathParam("monitoredElementID") String monitoredElementID,
            @PathParam("monitoredElementLevel") String monitoredElementlevel,
            //i.e., IP of VM to scale down
            @PathParam("unitInstanceIDs") String unitInstanceIDs
    ) {
        return redirectToCost("/" + serviceID + "/cost/evaluate/costefficiency/scalein/more/" + monitoredElementID + "/" + monitoredElementlevel + "/" + unitInstanceIDs + "/plain");
    }

    /**
     *
     * @param serviceID service to scale in
     * @param monitoredElementID id of element to scale in
     * @param monitoredElementlevel level of element to scale in
     * @return the name of the instance of the
     * {monitoredElementID}/{monitoredElementLevel} which fulfills generic
     * lifetime cost efficiency requirements By lifetime means we disregard the
     * usage efficiency items, so if you have a VM billed per hour and per GB of
     * I/O, will only evaluate the lifetime
     */
    @GET
    @Path("/{serviceID}/cost/recommend/lifetime/scalein/{monitoredElementID}/{monitoredElementLevel}/plain")
    @Produces("text/plain")
    public String recommendUnitInstanceToScaleDownBasedOnLifetimePlainText(@PathParam("serviceID") String serviceID,
            @PathParam("monitoredElementID") String monitoredElementID,
            @PathParam("monitoredElementLevel") String monitoredElementlevel) {
        return redirectToCost("/" + serviceID + "/cost/recommend/lifetime/scalein/" + monitoredElementID + "/" + monitoredElementlevel + "/plain");
    }

    /**
     *
     * @param serviceID service to scale in
     * @param monitoredElementID id of element to scale in
     * @param monitoredElementlevel level of element to scale in
     * @return the name of the instance of the
     * {monitoredElementID}/{monitoredElementLevel} which fulfills generic
     * lifetime cost efficiency requirements If you have a VM billed per hour
     * and per GB of I/O, will consider all aspects
     */
    @GET
    @Path("/{serviceID}/cost/recommend/costefficiency/scalein/{monitoredElementID}/{monitoredElementLevel}/plain")
    @Produces("text/plain")
    public String recommendUnitInstanceToScaleDownBasedOnCostEfficiencyPlainText(@PathParam("serviceID") String serviceID,
            @PathParam("monitoredElementID") String monitoredElementID,
            @PathParam("monitoredElementLevel") String monitoredElementlevel) {
        return redirectToCost("/" + serviceID + "/cost/recommend/costefficiency/scalein/" + monitoredElementID + "/" + monitoredElementlevel + "/plain");
    }

//    @GET
//    @Path("/{serviceID}/cost/recommend/costefficiency/scalein/{monitoredElementID}/{monitoredElementLevel}/xml")
//    @Produces("application/xml")
//    public MonitoredElement recommendUnitInstanceToScaleDownBasedOnCostEfficiencyXML(@PathParam("serviceID") String serviceID,
//            @PathParam("monitoredElementID") String monitoredElementID,
//            @PathParam("monitoredElementLevel") String monitoredElementlevel) {
//        return costEvalManager.recommendUnitInstanceToScaleDownBasedOnCostEfficiency(serviceID, monitoredElementID, monitoredElementlevel);
//
//    }
    /**
     *
     * @param serviceID service to scale in
     * @param monitoredElementID id of element to scale in
     * @param monitoredElementlevel level of element to scale in
     * @param targetEfficiency desired cost efficiency
     * @return ID/NAME of unit instance to scale in By lifetime means we
     * disregard the usage efficiency items, so if you have a VM billed per hour
     * and per GB of I/O, will only evaluate the lifetime
     */
    @GET
    @Path("/{serviceID}/cost/recommend/lifetime/scalein/{monitoredElementID}/{monitoredElementLevel}/{targetEfficiency}/plain")
    @Produces("text/plain")
    public String recommendUnitInstanceToScaleDownBasedOnLifetime(@PathParam("serviceID") String serviceID,
            @PathParam("monitoredElementID") String monitoredElementID,
            @PathParam("monitoredElementLevel") String monitoredElementlevel, @PathParam("targetEfficiency") String targetEfficiency) {
        return redirectToCost("/" + serviceID + "/cost/recommend/lifetime/scalein/" + monitoredElementID + "/" + monitoredElementlevel + "/" + targetEfficiency + "/plain");
    }
    
    /**
     * Similar as above, except it evaluates all cost aspects
     * @param serviceID service to scale in
     * @param monitoredElementID id of element to scale in
     * @param monitoredElementlevel level of element to scale in
     * @param targetEfficiency desired cost efficiency
     * @return 
     */

    @GET
    @Path("/{serviceID}/cost/recommend/costefficiency/scalein/{monitoredElementID}/{monitoredElementLevel}/{targetEfficiency}/plain")
    @Produces("text/plain")
    public String recommendUnitInstanceToScaleDownBasedOnCostEfficiencyPlainText(@PathParam("serviceID") String serviceID,
            @PathParam("monitoredElementID") String monitoredElementID,
            @PathParam("monitoredElementLevel") String monitoredElementlevel, @PathParam("targetEfficiency") String targetEfficiency) {
        return redirectToCost("/" + serviceID + "/cost/recommend/costefficiency/scalein/" + monitoredElementID + "/" + monitoredElementlevel + "/" + targetEfficiency + "/plain");
    }

    private String redirectToCost(String path) {
        URL url = null;
        HttpURLConnection connection = null;
        String response = "";
        try {
            url = new URL("http://" + costServiceIP + ":8182" + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(SpaceAndPathwayAnalysisServiceAPI.class.getName()).log(Level.SEVERE, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            response = IOUtils.toString(inputStream, encoding);

        } catch (Exception e) {
            // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Logger.getLogger(SpaceAndPathwayAnalysisServiceAPI.class.getName()).log(Level.WARNING, "Trying to connect to MELA COST - failing ... . Retrying later");

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

}
