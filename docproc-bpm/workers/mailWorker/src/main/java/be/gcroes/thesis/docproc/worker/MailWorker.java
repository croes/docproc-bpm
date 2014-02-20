package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

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

import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueConsumer;
import be.gcroes.thesis.docproc.messaging.ResultMap;
import be.gcroes.thesis.docproc.task.MailTask;

public class MailWorker extends QueueConsumer{
    
    private static Logger logger = LoggerFactory.getLogger(MailWorker.class);

    public MailWorker() throws IOException {
        super(MailTask.QUEUE_NAME);
    }

    @Override
    protected void doWork(Map<String, Object> map) {
        Task currentTask = (Task) map.get("currentTask");
        boolean mail = (Boolean)map.get("doMail");
        String mailTo = (String) map.get("mailTo");
        String mailSubject = (String) map.get("mailSubject");
        String mailBody= (String) map.get("mailBody");
        
        if(mail){
            //fill in to, subject and body templates
            VelocityContext context = new VelocityContext();
            
            for(Entry<String, String> entry : currentTask.getParams().entrySet()){
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
        ResultMap results = new ResultMap(map);
        returnResultMap(results);
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
    
    public static void main(String[] args) throws Exception{
        MailWorker worker = new MailWorker();
        Thread t = new Thread(worker);
        t.start();
    }

}
