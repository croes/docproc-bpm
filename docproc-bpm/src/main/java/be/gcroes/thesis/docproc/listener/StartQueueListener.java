package be.gcroes.thesis.docproc.listener;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;

import be.gcroes.thesis.docproc.messaging.QueueConsumer;

public class StartQueueListener implements ServletContextListener{
    
    public static final String INC_QUEUE_NAME = "docproc-inc";
    private Thread consumerThread = null;

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            consumerThread.interrupt();
        } catch (Exception ex) {
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
       if( !(consumerThread == null) || !(consumerThread.isAlive()) ){
           try {
            consumerThread = new Thread(new QueueConsumer(INC_QUEUE_NAME) {
                
                @Override
                protected void doWork(Map<String, String> map) {
                    String instanceId = map.get("instanceId");
                    String activityId = map.get("activityId");
                    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
                    RuntimeService runtimeService = processEngine.getRuntimeService();
                    Execution ex = runtimeService.createExecutionQuery()
                            .processInstanceId(instanceId)
                            .activityId(activityId)
                            .singleResult();
                    if(ex == null){
                        System.out.println("Could not find activiti execution with ID: " + instanceId);
                    }
                    String exid = ex.getId();
                    for(Entry<String, String> e : map.entrySet()){
                        runtimeService.setVariable(instanceId, e.getKey(), e.getValue());
                    }
                    runtimeService.signal(exid);
                }
      });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    }
}
