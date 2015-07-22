/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar.EnforcementPluginCELAR;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPI;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Georgiana
 */
public class DiskActionTest {
    private Node cloudService ;
    private EnforcementPluginCELAR enforcementPluginCELAR;
    public DiskActionTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
          cloudService = new Node();
        cloudService.setId("myCloudServ");
        cloudService.setNodeType(NodeType.CLOUD_SERVICE);
        Node servTop = new Node();
        servTop.setId("servTop");
        servTop.setNodeType(NodeType.SERVICE_TOPOLOGY);
        SimpleRelationship relationship = new SimpleRelationship();
        relationship.setId("rel1");
        relationship.setSourceElement(cloudService.getId());
        relationship.setTargetElement(servTop.getId());
        relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
        cloudService.addNode(servTop, relationship);

        Node node = new Node();

        node.setId("gatk_ir");
        node.setNodeType(NodeType.SERVICE_UNIT);
        relationship = new SimpleRelationship();
        relationship.setId("rel");
        relationship.setSourceElement(servTop.getId());
        relationship.setTargetElement(node.getId());
        relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
        servTop.addNode(node, relationship);
        
        
//        Node vm1 = new Node ();
//        vm1.setId("0.0.0.0");
//        vm1.setNodeType(NodeType.VIRTUAL_MACHINE);
//        relationship=new SimpleRelationship();
//        relationship.setId("h1");
//        relationship.setSourceElement(node.getId());
//        relationship.setTargetElement(vm1.getId());
//        relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
//        node.addNode(vm1, relationship);
        
        
         Node vm2 = new Node ();
        vm2.setId("83.212.116.21");
        vm2.setNodeType(NodeType.VIRTUAL_MACHINE);
        relationship=new SimpleRelationship();
        relationship.setId("h2");
        relationship.setSourceElement(node.getId());
        relationship.setTargetElement(vm2.getId());
        relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
        node.addNode(vm2, relationship);
        
        
        
         enforcementPluginCELAR = new EnforcementPluginCELAR(cloudService);
        enforcementPluginCELAR.refreshElasticityActionsList();
        MonitoringAPIInterface monitoringAPI = new MonitoringAPI();
        monitoringAPI.setControlledService(cloudService);
        enforcementPluginCELAR.setMonitoringPlugin(monitoringAPI);
        
    }
    @Test
    public void test1CorrectInitiation(){
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        System.out.println(dependencyGraph.graphToString());
        for (String action : enforcementPluginCELAR.getElasticityCapabilities()) {
            System.out.println(action);

        }
    }
    @Test 
    public void test2AttachDisk(){
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        enforcementPluginCELAR.attachDisk(dependencyGraph.getNodeWithID("gatk_ir"));
    }
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
