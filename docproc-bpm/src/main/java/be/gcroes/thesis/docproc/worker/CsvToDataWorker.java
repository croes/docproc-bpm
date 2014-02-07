package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import be.gcroes.thesis.docproc.listener.StartQueueListener;
import be.gcroes.thesis.docproc.messaging.Producer;
import be.gcroes.thesis.docproc.messaging.QueueConsumer;

public class CsvToDataWorker extends QueueConsumer{
    
    private static Logger logger = LoggerFactory.getLogger(CsvToDataWorker.class);

    public CsvToDataWorker(String endPointName) throws IOException {
        super(endPointName);
    }

    @Override
    protected void doWork(Map<String, String> map) {
        String csv = map.get("data");
        List<HashMap<String, String>> tasks = new ArrayList<HashMap<String, String>>();
        CSVReader reader = new CSVReader(new StringReader(csv), ';');
        String[] nextLine;
        try {
            String[] headers = reader.readNext();
            while((nextLine = reader.readNext()) != null){
                HashMap<String, String> taskParams = new HashMap<String, String>();
                for(int i=0; i<nextLine.length; i++){
                    taskParams.put(headers[i], nextLine[i]);
                    //logger.info("Read param '{}' with value '{}'", headers[i], nextLine[i]);
                }
                tasks.add(taskParams);
            }
            reader.close();
        } catch (IOException e) {
            logger.error("IO error parsing CSV.");
            e.printStackTrace();
        }
       
        //execution.setVariable("tasks", tasks);
        //TODO: figure out return
        HashMap<String, Object> returnVars = new HashMap<String, Object>();
        returnVars.put("instanceId", map.get("instanceId"));
        returnVars.put("activityId", map.get("activityId"));
        returnVars.put("tasks", tasks);
        try {
            Producer p = new Producer(StartQueueListener.INC_QUEUE_NAME);
            p.sendMessage(returnVars);
            p.close();
        } catch (IOException e) {
            
        }
        
        logger.info("csv-to-task: Read {} tasks.", tasks.size());
    }
    
    public static void main(String[] args) throws Exception{
        CsvToDataWorker worker = new CsvToDataWorker("csv-to-data");
        Thread t = new Thread(worker);
        t.start();
    }
    
}
