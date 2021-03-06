package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.QueueMessageSender;
import be.gcroes.thesis.docproc.messaging.QueueWorker;

public class CsvToDataWorker extends QueueWorker{
    
    private static Logger logger = LoggerFactory.getLogger(CsvToDataWorker.class);
    
    public final static String QUEUE_NAME = "csv-to-data";
    private QueueMessageSender qSender;

    public CsvToDataWorker() throws IOException {
        super(QUEUE_NAME);
        qSender = new QueueMessageSender("template-to-xsl");
        logger.info("CSVTODATAWORKER CREATED");
    }

	@Override
	protected void doWork(Job job, Task task) {
		 String csv = job.getInputdata();
	     List<Task> tasks = new ArrayList<Task>();
	     CSVReader reader = new CSVReader(new StringReader(csv), ';');
	     String[] nextLine;
	     try {
	    	 String[] headers = reader.readNext();
	         while((nextLine = reader.readNext()) != null){
	        	 Task newTask = new Task(job);
	             for(int i=0; i<nextLine.length; i++){
	                newTask.addParam(headers[i], nextLine[i]);
	                //logger.info("Read param '{}' with value '{}'", headers[i], nextLine[i]);
	             }
	             tasks.add(newTask);
	         }
	         reader.close();
	     } catch (IOException e) {
	    	 logger.error("IO error parsing CSV.");
	     	e.printStackTrace();
	     }
	     logger.info("csv-to-task: Read {} tasks.", tasks.size());
	     
	     persistAllTasks(tasks);
	     job.setStartTime(new DateTime());
	     job.setNbOfTasksLeft(tasks.size());
	     EntityTransaction tx = em.getTransaction();
	     tx.begin();
	     job = em.merge(job);
	     tx.commit();
	     for(Task currentTask : tasks){
	    	 try {
	    		qSender.send(job, currentTask);
			} catch (IOException e) {
				e.printStackTrace();
				logger.warn("Could not place task on template-to-xsl queue.");
			}
	     }
	     logger.info("sent {} messages on template-to-xsl queue", tasks.size());
	}

	private void persistAllTasks(List<Task> tasks) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try{
			for(Task task : tasks){
				em.persist(task);
			}
			tx.commit();
		}catch(Exception e){
			e.printStackTrace();
			tx.rollback();
		}
		return;
	}
    
}
