package be.gcroes.thesis.docproc.messaging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;

public class QueueMessageSender{
    
    private static Logger logger = LoggerFactory
            .getLogger(QueueMessageSender.class);
    
    protected Producer producer;

    public QueueMessageSender(String queueName){
        try {
			this.producer = new Producer(queueName);
		} catch (IOException e) {
			logger.warn("Could not create Producer on queue {}", queueName);
			e.printStackTrace();
		}
    }

    public void send(Job job, Task task) throws IOException {
        int jobId = job.getId();
        int taskId = -1;
        if(task != null){
        	taskId = task.getId();
        }
        logger.info("sending message on queue {} with job id {} and task id {}", producer.endPointName, jobId, taskId);
        producer.sendMessage(String.format("job[%d]+task[%d]", jobId, taskId));
    }
    
    public void close(){
    	try {
			producer.close();
		} catch (IOException e) {
			logger.warn("Could not close producer on queue {}", producer.endPointName);
			e.printStackTrace();
		}
    }

}
