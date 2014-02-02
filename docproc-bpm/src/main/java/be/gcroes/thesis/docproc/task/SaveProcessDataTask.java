package be.gcroes.thesis.docproc.task;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import be.gcroes.thesis.docproc.entity.EntityManagerUtil;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;

public class SaveProcessDataTask implements JavaDelegate {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        EntityManagerFactory emf = EntityManagerUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();
        String template = (String) execution.getVariable("template");
        List<String> filepaths = (List<String>) execution.getVariable("filepaths");
        String data = (String) execution.getVariable("data");
        List<HashMap<String, String>> params = (List<HashMap<String, String>>)execution.getVariable("tasks");
        String zipLoc = (String) execution.getVariable("zipLoc");
        String initiator = (String) execution.getVariable("initiator");
        
        Job job = new Job();
        job.setActivitiJobId(execution.getProcessInstanceId());
        job.setResult(zipLoc);
        job.setInputdata(data);
        job.setTemplate(template);
        job.setUser(initiator);
        for(int i=0; i<params.size();i++){
            Task t = new Task(job, filepaths.get(i), params.get(i));
            job.addTask(t);
        }
        
        em.persist(job);
        
        tx.commit();
    }
}
