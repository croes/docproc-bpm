package be.gcroes.thesis.docproc.task;

import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.messaging.Producer;

public abstract class AsyncTask implements JavaDelegate{
    
    private static Logger logger = LoggerFactory
            .getLogger(AsyncTask.class);
    
    protected String queueName;

    public AsyncTask(String queueName){
        this.queueName = queueName;
    }
    
    protected abstract void fillInputMap(HashMap<String, Object> vars, DelegateExecution execution);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Producer p = new Producer(queueName);
        HashMap<String, Object> vars = new HashMap<String, Object>();
        fillInputMap(vars, execution);
        vars.put("instanceId", execution.getProcessInstanceId());
        vars.put("executionId", execution.getId());
        logger.info("sending message on queue {} with execution id {} and process id {}", queueName, execution.getId(), execution.getProcessInstanceId());
        p.sendMessage(vars);
        p.close();
    }

}
