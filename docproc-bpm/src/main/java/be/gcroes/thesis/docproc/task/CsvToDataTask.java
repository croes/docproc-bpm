package be.gcroes.thesis.docproc.task;
import java.util.HashMap;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.messaging.Producer;


public class CsvToDataTask implements JavaDelegate {
    
    private static Logger logger = LoggerFactory.getLogger(CsvToDataTask.class);
    private static String QUEUE_NAME = "csv-to-data";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Producer p = new Producer(QUEUE_NAME);
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("data", (String)execution.getVariable("data"));
        vars.put("instanceId", execution.getProcessInstanceId());
        vars.put("activityId", execution.getCurrentActivityId());
        p.sendMessage(vars);
        p.close();
    }

}
