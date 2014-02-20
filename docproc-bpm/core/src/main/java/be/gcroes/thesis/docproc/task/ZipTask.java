package be.gcroes.thesis.docproc.task;

import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipTask extends AsyncTask {
   
    private static Logger logger = LoggerFactory.getLogger(ZipTask.class);
    
    public static final String QUEUE_NAME = "zip";
    
    public ZipTask() {
        super(QUEUE_NAME);
    }

    @Override
    protected void fillInputMap(HashMap<String, Object> vars,
            DelegateExecution execution) {
        vars.put("tasks", execution.getVariable("tasks"));
        logger.info("calling {} worker", QUEUE_NAME);
    }

}
