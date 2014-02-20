package be.gcroes.thesis.docproc.task;

import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XslFoRenderTask extends AsyncTask {
    
    public static final String QUEUE_NAME = "xsl-fo-render";
    
    public XslFoRenderTask() {
        super(QUEUE_NAME);
    }

    private static Logger logger = LoggerFactory.getLogger(XslFoRenderTask.class);
    
    @Override
    protected void fillInputMap(HashMap<String, Object> vars,
            DelegateExecution execution) {
       vars.put("currentTemplate", execution.getVariable("currentTemplate"));
       vars.put("currentTask", execution.getVariable("currentTask"));
       vars.put("loopCounter", execution.getVariable("loopCounter"));
       logger.info("xslforendertask execution id {}", execution.getId());
    }
}
