package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueMessageSender;
import be.gcroes.thesis.docproc.messaging.QueueWorker;

public class TemplateToXslWorker extends QueueWorker {
    
    private static Logger logger = LoggerFactory.getLogger(TemplateToXslWorker.class);
    
    public static final String QUEUE_NAME = "template-to-xsl";
    private QueueMessageSender qSender = new QueueMessageSender("render");

    public TemplateToXslWorker() throws IOException {
        super(QUEUE_NAME);
    }

    @Override
    protected void doWork(Job job, Task task) {
    	String template = job.getTemplate();
        try{
            VelocityContext context = new VelocityContext();

            for (Entry<String, String> entry : task.getParams().entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
            StringWriter sw = new StringWriter();
            
            Velocity.evaluate(context, sw, "logtag", template);
            task.setFilledTemplate(sw.toString());
            sw.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        em.merge(task);
        
        try {
			qSender.send(job, task);
		} catch (IOException e) {
			logger.warn("Could not place message on render queue.");
			e.printStackTrace();
		}
        
        logger.info("Processed template.");
    }
}
