package be.gcroes.thesis.docproc.task;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;


public class CsvToDataTask implements JavaDelegate {
    
    private static Logger logger = LoggerFactory.getLogger(CsvToDataTask.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String csv = (String) execution.getVariable("data");
        List<HashMap<String, String>> tasks = new ArrayList<HashMap<String, String>>();
        CSVReader reader = new CSVReader(new StringReader(csv), ';');
        String[] nextLine;
        String[] headers = reader.readNext();
        while((nextLine = reader.readNext()) != null){
            HashMap<String, String> taskParams = new HashMap<String, String>();
            for(int i=0; i<nextLine.length; i++){
                taskParams.put(headers[i], nextLine[i]);
                logger.info("Read param '{}' with value '{}'", headers[i], nextLine[i]);
            }
            tasks.add(taskParams);
        }
        reader.close();
        execution.setVariable("tasks", tasks);
        logger.info("csv-to-task: Read {} tasks.", tasks.size());
    }

}
