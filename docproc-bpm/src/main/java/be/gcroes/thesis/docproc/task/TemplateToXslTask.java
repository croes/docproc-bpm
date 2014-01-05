package be.gcroes.thesis.docproc.task;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TemplateToXslTask implements JavaDelegate{
    
    private static Logger logger = LoggerFactory.getLogger(TemplateToXslTask.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        @SuppressWarnings("unchecked")
        HashMap<String, String> params = (HashMap<String, String>)execution.getVariable("currentTask");
        
        VelocityContext context = new VelocityContext();
        
        for(Entry<String, String> entry : params.entrySet()){
            context.put(entry.getKey(), entry.getValue());
        }
        
        String template = (String) execution.getVariable("template");
        StringWriter sw = new StringWriter();
        
        Velocity.evaluate(context, sw, "logtag", template);
        
        sw.close();
       
        @SuppressWarnings("unchecked")
        List<String> templates = (List<String>)execution.getVariable("templates");
        if(templates == null){
            templates = new ArrayList<String>();
            execution.setVariable("templates", templates);
        }
        templates.add(sw.toString());
    }
    
}
