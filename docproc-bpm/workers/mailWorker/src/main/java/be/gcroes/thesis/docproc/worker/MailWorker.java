package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.persistence.EntityTransaction;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueMessageSender;
import be.gcroes.thesis.docproc.messaging.QueueWorker;

public class MailWorker extends QueueWorker{
    
    private static Logger logger = LoggerFactory.getLogger(MailWorker.class);
    
    public static final String QUEUE_NAME = "mail";
    private QueueMessageSender qSender;

    public MailWorker() throws IOException {
        super(QUEUE_NAME);
        qSender = new QueueMessageSender("zip");
        logger.info("MAILWORKER CREATED");
    }

    @Override
    protected void doWork(Job job, Task task) {
        boolean mail = true;
        String mailTo = "${email}";
        String mailSubject = "A docproc document has been generated";
        String mailBody= "Download your docproc document here: http://127.0.0.1/download?jobid="
        					+ job.getId() +"&taskid="+task.getId();
        
        if(mail){
            //fill in to, subject and body templates
            VelocityContext context = new VelocityContext();
            
            for(Entry<String, String> entry : task.getParams().entrySet()){
                context.put(entry.getKey(), entry.getValue());
            }
            mailTo = fillTemplate(context, mailTo);
            mailSubject = fillTemplate(context, mailSubject);
            mailBody = fillTemplate(context, mailBody);
            //and send mail
            Email email = new SimpleEmail();
            email.setHostName("127.0.0.1");
            email.setSmtpPort(25);
            try {
                email.setFrom("no-reply@docproc.com");
                email.addTo(mailTo);
                email.setSSL(false);
                email.setMsg(mailBody);
                email.send();
                logger.info("Sent mail to {} with body {} {}", email.getToAddresses().get(0));
            } catch (EmailException e) {
                e.printStackTrace();
            }
        }
        decreaseJobCount(job);
        if(job.getNbOfTasksLeft() <= 0){
        	try{
            	qSender.send(job, task);
            	logger.info("Sent message on zip queue.");
            }catch(IOException ioe){
            	logger.warn("Could not send message on zip queue");
            }
        }
        
    }
    
    private void decreaseJobCount(Job job) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try{
			job.decrementTasksLeft();
			em.merge(job);
			tx.commit();
		}catch(Exception e){
			tx.rollback();
			logger.warn("Could not decrement job count!");
		}
	}

	private String fillTemplate(VelocityContext context, String template){
        StringWriter sw = new StringWriter();
        try {
            Velocity.evaluate(context, sw, "logtag", template);
            sw.close();
        } catch (ParseErrorException | MethodInvocationException
                | ResourceNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

}
