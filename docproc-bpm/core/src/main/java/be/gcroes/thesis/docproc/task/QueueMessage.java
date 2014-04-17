package be.gcroes.thesis.docproc.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.messaging.Producer;

public abstract class QueueMessage{
    
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

    public void send() throws Exception {
        Producer p = new Producer(queueName);
        logger.info("sending message on queue {} with job id {} and task id {}", queueName);
        p.sendMessage(String.format("job[{}]+task[{}]", jobId, taskId));
        p.close();
    }

}
