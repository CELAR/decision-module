/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.quelle.elasticityQuantification.engines;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.requirements.RequirementsResolutionResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import junit.framework.TestCase;

/**
 *
 * @author daniel-tuwien
 */
public class RequirementsMatchingEngineTest extends TestCase {

    public RequirementsMatchingEngineTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        Properties props = System.getProperties();
        props.setProperty("LOG_DIR", "/tmp");
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }

    /**
     * Test of matchServiceUnit method, of class RequirementsMatchingEngine.
     */
    public void testAnalyzeServiceUnitMatching() throws IOException {
        List<CloudProvider> cloudProviders = new ArrayList<>();
        try {
            JAXBContext context = JAXBContext.newInstance(CloudProvider.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            CloudProvider provider = (CloudProvider) unmarshaller.unmarshal(new File("./default/amazonDescription.xml"));
            cloudProviders.add(provider);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        MultiLevelRequirements requirements = null;
        try {
            JAXBContext context = JAXBContext.newInstance(MultiLevelRequirements.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            requirements = (MultiLevelRequirements) unmarshaller.unmarshal(new File("./default/requirements.xml"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert !cloudProviders.isEmpty() : "Error: Cloud Providers not unmarshalled from XML";
        assert requirements != null : "Error: Requirements not unmarshalled from XML";

        RequirementsMatchingEngine requirementsMatchingEngine = new RequirementsMatchingEngine();
        RequirementsResolutionResult resolutionResult = requirementsMatchingEngine.analyzeMultiLevelRequirements(cloudProviders, requirements);

    }
}
