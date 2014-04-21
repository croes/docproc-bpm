package be.gcroes.thesis.docproc.messaging;
import java.io.IOException;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import be.gcroes.thesis.docproc.entity.EntityManagerUtil;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public abstract class QueueWorker extends EndPoint implements Runnable, Consumer{
    
    protected EntityManager em;
    
    public QueueWorker(String endPointName) throws IOException{
        super(endPointName);      
        em = EntityManagerUtil.getEntityManagerFactory().createEntityManager();
    }
    
    public void run() {
        try {
            //start consuming messages. Auto acknowledge messages.
            channel.basicConsume(endPointName, true,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConsumeOk(String consumerTag) {
        System.out.println("Consumer "+consumerTag +" registered");     
    }

    public void handleDelivery(String consumerTag, Envelope env,
            BasicProperties props, byte[] body) throws IOException {
        String message = new String(body);
        Job job = findJobFromMessage(message);
        Task task = findTaskFromMessage(message);
        doWork(job, task);
    }

    private Job findJobFromMessage(String message) {
    	Pattern pattern = Pattern.compile("job\\[(\\d*)\\]\\+");
    	Matcher matcher = pattern.matcher(message);
    	matcher.find();
		String jobid = matcher.group(1);
    	Job job = em.find(Job.class, Integer.parseInt(jobid));
		return job;
	}

	private Task findTaskFromMessage(String message) {
		Pattern pattern = Pattern.compile("task\\[(.*)\\]");
    	Matcher matcher = pattern.matcher(message);
    	matcher.find();
		String taskid = matcher.group(1);
    	Task task = em.find(Task.class, Integer.parseInt(taskid));
		return task;
	}

	protected abstract void doWork(Job job, Task task);
    

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}
}