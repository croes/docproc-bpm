package be.gcroes.thesis.docproc.worker;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;

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

import be.gcroes.thesis.docproc.config.Config;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueWorker;
import be.gcroes.thesis.docproc.messaging.ResultMap;
import be.gcroes.thesis.docproc.task.ClassPathURIResolver;
import be.gcroes.thesis.docproc.task.XslFoRenderTask;

public class XslFoRenderWorker extends QueueWorker{
    
    private static Logger logger = LoggerFactory
            .getLogger(XslFoRenderWorker.class);
    
    public XslFoRenderWorker() throws IOException {
        super(XslFoRenderTask.QUEUE_NAME);
    }

    @Override
    protected void doWork(Map<String, Object> map) {
        String currentTemplate = (String) map.get("currentTemplate");
        Task task = (Task) map.get("currentTask");
        ResultMap results = new ResultMap(map);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        FopFactory fopFactory = FopFactory.newInstance();

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
            
            File outputdir = new File(Config.OUTPUT_DIR);
            if (!outputdir.exists()) {outputdir.mkdirs();}
            File outFile = File.createTempFile("docproc", ".pdf", outputdir);
            FileOutputStream fos = new FileOutputStream(outFile);
            //write filepath to task result here
            task.setResult(outFile.getAbsolutePath());
            results.setToActivitiArray("tasks", task, (String)map.get("instanceId"), (Integer)map.get("loopCounter"));
            fos.close();
            logger.info("Rendered XSL.");
            returnResultMap(results);
        } catch (FOPException | TransformerException | IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    public static void main(String[] args) throws IOException {
        XslFoRenderWorker xslworker = new XslFoRenderWorker();
        Thread t = new Thread(xslworker);
        t.start();
    }
}
