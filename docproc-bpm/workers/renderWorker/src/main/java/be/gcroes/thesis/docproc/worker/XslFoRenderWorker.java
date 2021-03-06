package be.gcroes.thesis.docproc.worker;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import javax.persistence.EntityTransaction;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueMessageSender;
import be.gcroes.thesis.docproc.messaging.QueueWorker;
import be.gcroes.thesis.docproc.task.ClassPathURIResolver;

public class XslFoRenderWorker extends QueueWorker{
    
    private static Logger logger = LoggerFactory
            .getLogger(XslFoRenderWorker.class);
    
    public static final String QUEUE_NAME = "render";
    private QueueMessageSender qSender;
    
    public XslFoRenderWorker() throws IOException {
        super(QUEUE_NAME);
        qSender = new QueueMessageSender("mail");
        logger.info("XSLFORENDERWORKER CREATED");
    }

    @Override
    protected void doWork(Job job, Task task) {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        FopFactory fopFactory = FopFactory.newInstance();
        String currentTemplate = task.getFilledTemplate();
        try {
            Templates templates = tFactory.newTemplates(new StreamSource(
                    new StringReader(currentTemplate)));
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            OutputStream out = new BufferedOutputStream(boas);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            foUserAgent.setURIResolver(new ClassPathURIResolver());
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent,
                    out);

            Transformer transformer = templates.newTransformer();
            transformer.transform(new StreamSource(new StringReader(
                    "<invoice></invoice>")),
                    new SAXResult(fop.getDefaultHandler()));
            out.close();
            
            task.setResult(boas.toByteArray());
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            task = em.merge(task);
            tx.commit();
            logger.info("Rendered XSL.");
        } catch (FOPException | TransformerException | IOException e) {
            e.printStackTrace();
        }
        
        try{
        	qSender.send(job, task);
        }catch(IOException ioe){
        	logger.warn("Could not send message on mail queue");
        	ioe.printStackTrace();
        }
    }
}
