package be.gcroes.thesis.docproc.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.config.Config;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueConsumer;
import be.gcroes.thesis.docproc.messaging.ResultMap;
import be.gcroes.thesis.docproc.task.ZipTask;

public class ZipWorker extends QueueConsumer {
    
    private static Logger logger = LoggerFactory.getLogger(ZipTask.class);

    public ZipWorker() throws IOException {
        super(ZipTask.QUEUE_NAME);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doWork(Map<String, Object> map) {
        List<String> fileList = new ArrayList<String>();
        List<Task> tasks = (List<Task>) map.get("tasks");
        for(Task t : tasks){
            fileList.add(t.getResult());
        }
        String outputdir = Config.OUTPUT_DIR;
        SimpleDateFormat sdf = new SimpleDateFormat("'archive'-ddMMyy-hhmmss.SSS.'zip'");
        String zipFilepath = "" + outputdir + "\\" + sdf.format(new Date());
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(zipFilepath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            byte[] buffer = new byte[1024];
            for(String filename : fileList){
                File currFile = new File(filename);
                ZipEntry ze = new ZipEntry(currFile.getName());
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(currFile);
                int len;
                while((len = in.read(buffer)) > 0){
                    zos.write(buffer, 0, len);
                }
                in.close();
            }
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("zipped {} files", fileList.size());
        ResultMap results = new ResultMap(map);
        results.put("zipLoc", zipFilepath);
        returnResultMap(results);
    }
    
    public static void main(String[] args) throws IOException {
        ZipWorker worker = new ZipWorker();
        Thread t = new Thread(worker);
        t.start();
    }

}
