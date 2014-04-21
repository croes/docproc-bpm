package be.gcroes.thesis.docproc.worker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueWorker;

public class ZipWorker extends QueueWorker {
    
    private static Logger logger = LoggerFactory.getLogger(ZipWorker.class);
    
    public static final String QUEUE_NAME = "zip";

    public ZipWorker() throws IOException {
        super(QUEUE_NAME);
    }

    @Override
    protected void doWork(Job job, Task task) {
    	List<Task> tasks = job.getTasks();
        try {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            byte[] buffer = new byte[1024];
            for(Task currentTask : tasks){
                ZipEntry ze = new ZipEntry("task-" + currentTask.getId());
                zos.putNextEntry(ze);
                ByteArrayInputStream bais = new ByteArrayInputStream(currentTask.getResult());
                int len;
                while((len = bais.read(buffer)) > 0){
                    zos.write(buffer, 0, len);
                }
                bais.close();
            }
            zos.closeEntry();
            zos.close();
            job.setResult(baos.toByteArray());
            job.setEndTime(new DateTime());
            em.merge(job);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("zipped {} files", tasks.size());
    }

}
