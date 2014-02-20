package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueConsumer;
import be.gcroes.thesis.docproc.messaging.ResultMap;
import be.gcroes.thesis.docproc.task.TemplateToXslTask;

public class TemplateToXslWorker extends QueueConsumer {
    
    private static Logger logger = LoggerFactory.getLogger(TemplateToXslWorker.class);

    public TemplateToXslWorker() throws IOException {
        super(TemplateToXslTask.QUEUE_NAME);
    }

    @Override
    protected void doWork(Map<String, Object> map) {
        String template = (String) map.get("template");
        Task task = (Task) map.get("currentTask");
        ResultMap results = new ResultMap(map);
        
        try{
            VelocityContext context = new VelocityContext();

            for (Entry<String, String> entry : task.getParams().entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
            StringWriter sw = new StringWriter();
            
            Velocity.evaluate(context, sw, "logtag", template);
            
            results.put("currentTemplate", sw.toString(), (String)map.get("executionId"));
            
            sw.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        logger.info("Processed template.");
        returnResultMap(results);
    }
    
    public static void main(String[] args) throws Exception{
        TemplateToXslWorker worker = new TemplateToXslWorker();
        Thread t = new Thread(worker);
        t.start();
    }
}
