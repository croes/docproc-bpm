package be.gcroes.thesis.docproc.task;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class XslFoRenderTask implements JavaDelegate {
    
    private static Logger logger = LoggerFactory.getLogger(TemplateToXslTask.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String currentTemplate = (String) execution.getVariable("currentTemplate");
        logger.info("Current template: {}", currentTemplate);
        
        TransformerFactory tFactory = TransformerFactory.newInstance();
        FopFactory fopFactory = FopFactory.newInstance();
        
        Templates templates = tFactory.newTemplates(new StreamSource(new StringReader(currentTemplate)));
        
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        OutputStream out = new BufferedOutputStream(boas);
        
        try {
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            foUserAgent.setURIResolver(new ClassPathURIResolver());
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF,
                            foUserAgent, out);

            Transformer transformer = templates.newTransformer();
            transformer.transform(new StreamSource(new StringReader(
                            "<invoice></invoice>")),
                            new SAXResult(fop.getDefaultHandler()));

        } finally {
            out.close();
        }
        
        File outFile = File.createTempFile("docproc", ".pdf", new File("C:\\docprocdocs"));
        FileOutputStream fos = new FileOutputStream(outFile);
        fos.write(boas.toByteArray());
        fos.close();
    }

}
