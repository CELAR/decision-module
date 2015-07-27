/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.depic.orchestrator.registry;


/**
 *
 * @author Jun
 */
public class ElasticServiceMonitor implements Runnable {

    private Thread t;
    private String threadName;
  

    public ElasticServiceMonitor(String name) {
        threadName = name;

    }

    public void run() {

        do {

            System.out.println("Updateing Elasticity Services ...");
            ElasticServiceRegistry.removeNonExistingSerivce();
            
            
            try {
                Thread.sleep(10000);

            } catch (InterruptedException ex) {

            }
        } while (true);
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
    
   



}
