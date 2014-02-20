package be.gcroes.thesis.docproc.task;

import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadTask extends AsyncTask {
    
    public static final String QUEUE_NAME = "multi-test";
    
    private static Logger logger = LoggerFactory.getLogger(MultiThreadTask.class);

    public MultiThreadTask() {
        super(QUEUE_NAME);
    }

    @Override
    protected void fillInputMap(HashMap<String, Object> vars,
            DelegateExecution execution) {
        vars.put("testVar", execution.getVariable("testVar"));
        logger.info("Calling worker thread...");
    }

}
