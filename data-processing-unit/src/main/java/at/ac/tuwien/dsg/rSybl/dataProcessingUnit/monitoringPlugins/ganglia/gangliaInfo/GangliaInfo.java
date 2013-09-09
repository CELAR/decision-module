/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.                 This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.ganglia.gangliaInfo;

import javax.xml.bind.annotation.*;
import java.util.Collection;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GANGLIA_XML")
public class GangliaInfo {

    @XmlElement(name = "CLUSTER")
    private Collection<GangliaClusterInfo> clusters;

    @XmlAttribute(name = "VERSION")
    private String version;

    @XmlAttribute(name = "SOURCE")
    private String source;


    public Collection<GangliaClusterInfo> getClusters() {
        return clusters;
    }

    public void setClusters(Collection<GangliaClusterInfo> clusters) {
        this.clusters = clusters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
