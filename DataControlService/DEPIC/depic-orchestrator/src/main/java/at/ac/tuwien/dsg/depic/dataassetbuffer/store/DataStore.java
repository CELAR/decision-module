/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
  E184.  This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
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
package at.ac.tuwien.dsg.depic.dataassetbuffer.store;

import at.ac.tuwien.dsg.depic.common.entity.eda.dataasset.DataAsset;
import at.ac.tuwien.dsg.depic.common.entity.runtime.DataPartitionRequest;


public interface DataStore {

    public  void saveDataAsset(String daXML, String dafName, String partitionID);

    public void removeDataAsset(String dafName);

    public String getDataAssetXML(String dafName, String partitionID);

    public String copyDataAssetRepo(DataPartitionRequest request);

    public String getDataPartitionRepo(DataPartitionRequest request);

    public String getNoOfPartitionRepo(DataPartitionRequest request);

    public void saveDataPartitionRepo(DataAsset dataAssetPartition);

    public void insertDataPartitionRepo(DataAsset dataAssetPartition);
}
