package be.gcroes.thesis.docproc.task;
import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TemplateToXslTask extends AsyncTask{
    
    public static final String QUEUE_NAME = "template-to-xsl";
    private static Logger logger = LoggerFactory
            .getLogger(TemplateToXslTask.class);
    
    public TemplateToXslTask() {
        super(QUEUE_NAME);
    }
    
    @Override
    protected void fillInputMap(HashMap<String, Object> vars,
            DelegateExecution execution) {
        vars.put("currentTask", execution.getVariable("currentTask"));
        vars.put("template", execution.getVariable("template"));
        logger.info("calling {} worker", QUEUE_NAME);
    }
    
}
