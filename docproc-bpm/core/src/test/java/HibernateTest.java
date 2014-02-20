import static org.fest.assertions.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.gcroes.thesis.docproc.entity.EntityManagerUtil;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;


public class HibernateTest{
    
    private EntityManager em;
    
    @Before
    public void beforeEach()
    {
       em = EntityManagerUtil.getEntityManagerFactory().createEntityManager();
    }

    @After
    public void afterEach()
    {
       em.close();
    }

    @Test
    public void testPersist()
    {
       EntityTransaction transaction = em.getTransaction();
       transaction.begin();
       
       Job job1 = new Job();
       job1.setActivitiJobId("TESTID");
       job1.setUser("Testuser");
       job1.setInputdata("test;1;2;3;4");
       job1.setTemplate("\\testtemplate \\one \\two \\three");
       Task t1 = new Task(job1, "Finished");
       Task t2 = new Task(job1);

       // IDs start as null
       assertEquals(0, job1.getId());
       assertEquals(0, t1.getId());
       assertEquals(0, t2.getId());

       em.persist(job1);
       
       Task t3 = new Task(job1);
       t3.addParam("arg0", "test");
       t3.addParam("arg1", "testing");

       transaction.commit();

       System.out.println("Job 1");
       System.out.println("Generated ID is: " + job1.getId());
       
       List<Job> jobs = (List<Job>)em.createQuery("SELECT j FROM Job j WHERE j.id = " + job1.getId()).getResultList();
       Job job = jobs.get(0);
       assertThat(job.getTasks()).hasSize(3);
       assertThat(job.getTasks()).contains(t1, t2, t3);
       assertThat(job.getTasks().get(2)).isSameAs(t3);
       assertThat(job.getTasks().get(2).getParamKeys()).contains("arg0", "arg1");
       assertThat(job.getTasks().get(2).getParamValues()).contains("test", "testing");
       assertThat(job.getTasks().get(2).getParams().get("arg0")).isNotNull();
       assertThat(job.getTasks().get(2).getParams().get("arg0")).isEqualTo("test");
       
    }
 }
