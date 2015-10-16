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
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import java.io.IOException;
import org.slf4j.Logger;

public class SalsaConfiguration {

    static final Logger logger = EngineLogger.logger;
    static final String CURRENT_DIR = System.getProperty("user.dir");

    static {
        // try to create working folder
        try {
            logger.debug("Current dir: " + CURRENT_DIR);
            (new File(getPioneerWorkingDir())).mkdirs();
            (new File(getServiceStorageDir())).mkdirs();
        } catch (Exception ex) {
            logger.error("Error occured when configuring salsa engine: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String getSALSA_CENTER_IP() {
        return getGenericParameter("SALSA_CENTER_IP", SystemFunctions.getEth0IPAddress());
    }

    private static String getSALSA_CENTER_PORT() {
        return getGenericParameter("SALSA_CENTER_PORT", SystemFunctions.getPort());
    }

    public static String getRepoPrefix() {
        return getGenericParameter("SALSA_REPO", "http://localhost:8080/salsa/upload/files");
    }

    // working dir of the pioneer
    public static String getPioneerWorkingDir() {
        return getGenericParameter("SALSA_PIONEER_WORKING_DIR", "/tmp/pioneer-workspace");
    }

    // variable file should sit beside the pioneer artifact
    public static String getSalsaVariableFile() {
        return "salsa.variables";
    }

    public static String getSalsaCenterEndpoint() {
        return "http://" + getSALSA_CENTER_IP() + ":" + getSALSA_CENTER_PORT() + "/salsa-engine";
    }

    public static String getSalsaCenterEndpointLocalhost() {
        return "http://localhost:" + getSALSA_CENTER_PORT() + "/salsa-engine";
    }

    public static String getServiceStorageDir() {
        return createFolderIfNotExisted(CURRENT_DIR + "/services");
    }

    public static String getArtifactStorage() {
        return createFolderIfNotExisted(CURRENT_DIR + "/artifacts");
    }

    public static String getToscaTemplateStorage() {
        return createFolderIfNotExisted(CURRENT_DIR + "/tosca_templates");
    }

    public static String getCloudProviderDescriptionDir() {
        return createFolderIfNotExisted(CURRENT_DIR + "/cloudDescriptions");
    }

    public static String getSalsaVersion() {
        String[] versionFile = {SalsaConfiguration.class.getResource("/version.txt").getFile()};
        return getGenericParameterFromFile(versionFile, "version", "unknown");
    }

    public static String getBuildTime() {
        String[] versionFile = {SalsaConfiguration.class.getResource("/version.txt").getFile()};
        return getGenericParameterFromFile(versionFile, "build.date", "unknown") + " UTC";
    }


    // return folderName
    private static String createFolderIfNotExisted(String folder) {
        File f = new File(folder);
        f.mkdirs();
        if (f.exists()) {
            return folder;
        }
        return "/tmp";
    }


    public static String getGenericParameter(String key, String theDefault) {
        String fileNames[] = {CURRENT_DIR + "/salsa.engine.properties"};
        return getGenericParameterFromFile(fileNames, key, theDefault);
    }

    public static String getConfigurationFile() {
        return CURRENT_DIR + "/salsa.engine.properties";
    }

    public static String getGenericParameterFromFile(String[] fileNames, String key, String theDefault) {
        Properties prop = new Properties();
        for (String file : fileNames) {
            File f = new File(file);
            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
                prop.load(new FileReader(f));
                String param = prop.getProperty(key);
                if (param != null) {
                    logger.debug("Read a property in {} and use {}={}", file, key, param);
                    return param;
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        logger.debug("Read a property and use the default {}={}", key, theDefault);
        return theDefault;
    }

}
