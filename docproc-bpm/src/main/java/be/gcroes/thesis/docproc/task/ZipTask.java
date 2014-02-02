package be.gcroes.thesis.docproc.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import be.gcroes.thesis.docproc.config.Config;

public class ZipTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        @SuppressWarnings("unchecked")
        List<String> fileList = (List<String>) execution.getVariable("filepaths");
        String outputdir = Config.OUTPUT_DIR;
        
        SimpleDateFormat sdf = new SimpleDateFormat("'archive'-ddMMyy-hhmmss.SSS.'zip'");
        String zipFilepath = "" + outputdir + "\\" + sdf.format(new Date());
        FileOutputStream fos = new FileOutputStream(zipFilepath);
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
        
        execution.setVariable("zipLoc", zipFilepath);
    }

}
