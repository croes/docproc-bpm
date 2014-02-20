package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.messaging.QueueConsumer;
import be.gcroes.thesis.docproc.messaging.ResultMap;
import be.gcroes.thesis.docproc.task.MultiThreadTask;

public class MultiThreadWorker extends QueueConsumer {
    
    private static Logger logger = LoggerFactory.getLogger(MultiThreadWorker.class);

    public MultiThreadWorker() throws IOException {
        super(MultiThreadTask.QUEUE_NAME);
    }

    @Override
    protected void doWork(Map<String, Object> map) {
        logger.info("Worker testVar: {}", map.get("testVar"));
        returnResultMap(new ResultMap(map));
    }
    
    public static void main(String[] args) throws IOException {
        Thread worker = new Thread(new MultiThreadWorker());
        worker.start();
    }

}
