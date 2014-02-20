package be.gcroes.thesis.docproc.task;
import java.util.HashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsvToDataTask extends AsyncTask {
    
    private static Logger logger = LoggerFactory.getLogger(CsvToDataTask.class);
    
    public static final String QUEUE_NAME = "csv-to-data";

    public CsvToDataTask() {
        super(QUEUE_NAME);
    }

    @Override
    protected void fillInputMap(HashMap<String, Object> vars,
            DelegateExecution execution) {
       vars.put("data", execution.getVariable("data"));
       logger.info("calling {} worker", QUEUE_NAME);
    }

}
