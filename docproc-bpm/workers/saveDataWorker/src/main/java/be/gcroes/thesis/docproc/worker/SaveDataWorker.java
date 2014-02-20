package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.EntityManagerUtil;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueConsumer;
import be.gcroes.thesis.docproc.messaging.ResultMap;
import be.gcroes.thesis.docproc.task.SaveProcessDataTask;

public class SaveDataWorker extends QueueConsumer {
    
    private static Logger logger = LoggerFactory
            .getLogger(SaveDataWorker.class);
    
    public SaveDataWorker() throws IOException {
        super(SaveProcessDataTask.QUEUE_NAME);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doWork(Map<String, Object> map) {
        logger.info("Saving process data...");
        EntityManagerFactory emf = EntityManagerUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();
        String template = (String) map.get("template");
        String data = (String) map.get("data");
        List<Task> tasks = (List<Task>)map.get("tasks");
        String zipLoc = (String) map.get("zipLoc");
        String initiator = (String) map.get("initiator");
        
        Job job = new Job();
        job.setActivitiJobId((String)map.get("instanceId"));
        job.setResult(zipLoc);
        job.setInputdata(data);
        job.setTemplate(template);
        job.setUser(initiator);
        for(int i=0; i < tasks.size(); i++){
            job.addTask(tasks.get(i));
        }
        
        em.persist(job);
        
        tx.commit();
        
        returnResultMap(new ResultMap(map));
    }
    
    public static void main(String[] args) throws IOException {
        SaveDataWorker worker = new SaveDataWorker();
        Thread t = new Thread(worker);
        t.start();
    }

}
