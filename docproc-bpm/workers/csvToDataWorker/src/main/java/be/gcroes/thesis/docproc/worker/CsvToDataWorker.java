package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueConsumer;
import be.gcroes.thesis.docproc.messaging.ResultMap;
import be.gcroes.thesis.docproc.task.CsvToDataTask;

public class CsvToDataWorker extends QueueConsumer{
    
    private static Logger logger = LoggerFactory.getLogger(CsvToDataWorker.class);

    public CsvToDataWorker() throws IOException {
        super(CsvToDataTask.QUEUE_NAME);
    }

    @Override
    protected void doWork(Map<String, Object> map) {
        String csv = (String)map.get("data");
        List<Task> tasks = new ArrayList<Task>();
        CSVReader reader = new CSVReader(new StringReader(csv), ';');
        String[] nextLine;
        try {
            String[] headers = reader.readNext();
            while((nextLine = reader.readNext()) != null){
                Task task = new Task();
                for(int i=0; i<nextLine.length; i++){
                    task.addParam(headers[i], nextLine[i]);
                    //logger.info("Read param '{}' with value '{}'", headers[i], nextLine[i]);
                }
                tasks.add(task);
            }
            reader.close();
        } catch (IOException e) {
            logger.error("IO error parsing CSV.");
            e.printStackTrace();
        }
        logger.info("csv-to-task: Read {} tasks.", tasks.size());
        
        ResultMap returnVars = new ResultMap(map);
        returnVars.put("tasks", tasks);
        returnResultMap(returnVars);
    }
    
    public static void main(String[] args) throws Exception{
        CsvToDataWorker worker = new CsvToDataWorker();
        Thread t = new Thread(worker);
        t.start();
    }
    
}
