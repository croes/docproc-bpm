import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadActivitiTest {
    
    private static Logger logger = LoggerFactory.getLogger(MultiThreadActivitiTest.class);
    
    @Test
    public void testMultiThread(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        if(processEngine.getRepositoryService().createDeploymentQuery().processDefinitionKey("testMulti").count() == 0){
            processEngine.getRepositoryService()
            .createDeployment()
            .addClasspathResource("bpmn/multithread.bpmn")
            .deploy();
        }
        HashMap<String, Object> vars = new HashMap<String, Object>();
        vars.put("testVar", "hello world");
        
        List<Execution> exes = processEngine.getRuntimeService().createExecutionQuery().processDefinitionKey("testMulti").list(); 
        for(Execution ex : exes){
            processEngine.getRuntimeService().deleteProcessInstance(ex.getProcessInstanceId(), "test cleanup");
        }
        
        ProcessInstance processInstance = processEngine.getRuntimeService()
                     .startProcessInstanceByKey("testMulti", vars);
        
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        HistoricProcessInstance hpi = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        
        assertTrue(hpi != null);
        assertTrue(hpi.getEndTime() != null);
    }   
}