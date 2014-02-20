package be.gcroes.thesis.docproc.task;

import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailTask extends AsyncTask {

    private static Logger logger = LoggerFactory.getLogger(MailTask.class);
    
    public final static String QUEUE_NAME = "mail";

    public MailTask() {
        super(QUEUE_NAME);
    }

    @Override
    protected void fillInputMap(HashMap<String, Object> vars,
            DelegateExecution execution) {
        vars.put("currentTask", execution.getVariable("currentTask"));
        vars.put("doMail", execution.getVariable("doMail"));
        vars.put("mailTo", execution.getVariable("mailTo"));
        vars.put("mailSubject", execution.getVariable("mailSubject"));
        vars.put("mailBody", execution.getVariable("mailBody"));
        logger.info("calling {} worker", QUEUE_NAME);
    }

}
