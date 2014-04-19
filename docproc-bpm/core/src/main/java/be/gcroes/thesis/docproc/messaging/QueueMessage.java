package be.gcroes.thesis.docproc.messaging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;

public class QueueMessage{
    
    private static Logger logger = LoggerFactory
            .getLogger(QueueMessage.class);
    
    protected String queueName;
    private int jobId;
    private int taskId;

    public QueueMessage(String queueName, int jobId, int taskId){
        this.queueName = queueName;
        this.jobId = jobId;
        this.taskId = taskId;
    }
    
    public QueueMessage(String queueName, Task task){
    	this.queueName = queueName;
    	this.jobId = task.getJob().getId();
    	this.taskId = task.getId();
    }
    
    public QueueMessage(String queueName, Job job){
    	this.queueName = queueName;
    	this.jobId = job.getId();
    	this.taskId = -1;
    }

    public void send() throws IOException {
        Producer p = new Producer(queueName);
        logger.info("sending message on queue {} with job id {} and task id {}", queueName);
        p.sendMessage(String.format("job[{}]+task[{}]", jobId, taskId));
        p.close();
    }

}
