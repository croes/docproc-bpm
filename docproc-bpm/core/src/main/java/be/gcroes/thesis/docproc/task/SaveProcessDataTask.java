package be.gcroes.thesis.docproc.task;

import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveProcessDataTask extends AsyncTask {
    
    public static final String QUEUE_NAME = "save-data";
    
    public SaveProcessDataTask() {
        super(QUEUE_NAME);
    }

    private static Logger logger = LoggerFactory
            .getLogger(SaveProcessDataTask.class);

    @Override
    protected void fillInputMap(HashMap<String, Object> vars,
            DelegateExecution execution) {
        vars.put("template", execution.getVariable("template"));
        vars.put("data", execution.getVariable("data"));
        vars.put("tasks", execution.getVariable("tasks"));
        vars.put("zipLoc", execution.getVariable("zipLoc"));
        vars.put("initiator", execution.getVariable("initiator"));
        logger.info("calling {} worker", QUEUE_NAME);
    }

}
