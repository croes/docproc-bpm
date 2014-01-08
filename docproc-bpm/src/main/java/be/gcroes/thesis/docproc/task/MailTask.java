package be.gcroes.thesis.docproc.task;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class MailTask implements JavaDelegate{

    @SuppressWarnings("unchecked")
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, String> currentTask = (HashMap<String, String>) execution.getVariable("currentTask");
        System.out.println("mail execution var: " + ((String) execution.getVariable("doMail")));
        boolean mail = Boolean.parseBoolean(((String) execution.getVariable("doMail")));
        String mailTo = (String) execution.getVariable("mailTo");
        String mailSubject = (String) execution.getVariable("mailSubject");
        String mailBody= (String) execution.getVariable("mailBody");
        
        System.out.println("mail var:" + currentTask);
        
        if(mail){
            //fill in to, subject and body templates
            VelocityContext context = new VelocityContext();
            
            for(Entry<String, String> entry : currentTask.entrySet()){
                context.put(entry.getKey(), entry.getValue());
            }
            mailTo = fillTemplate(context, mailTo);
            mailSubject = fillTemplate(context, mailSubject);
            mailBody = fillTemplate(context, mailBody);
            //and send mail
            Email email = new SimpleEmail();
            email.setHostName("127.0.0.1");
            email.setSmtpPort(25);
            email.setFrom("no-reply@docproc.com");
            email.addTo(mailTo);
            email.setSSL(false);
            email.setMsg(mailBody);
            email.send();
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
