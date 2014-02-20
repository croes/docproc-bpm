package be.gcroes.thesis.docproc.messaging;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;

import be.gcroes.thesis.docproc.messaging.ResultMap;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;


/**
 * The endpoint that consumes messages off of the queue. Happens to be runnable.
 * @author syntx
 *
 */
public abstract class QueueConsumer extends EndPoint implements Runnable, Consumer{
    
    public static final String INC_QUEUE_NAME = "docproc-inc";
    private Producer responder;
    
    public QueueConsumer(String endPointName) throws IOException{
        super(endPointName);      
        responder = new Producer(INC_QUEUE_NAME);
    }
    
    public void run() {
        try {
            //start consuming messages. Auto acknowledge messages.
            channel.basicConsume(endPointName, true,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when consumer is registered.
     */
    public void handleConsumeOk(String consumerTag) {
        System.out.println("Consumer "+consumerTag +" registered");     
    }

    /**
     * Called when new message is available.
     */
    @SuppressWarnings("unchecked")
    public void handleDelivery(String consumerTag, Envelope env,
            BasicProperties props, byte[] body) throws IOException {
        Map<String, Object> map = (HashMap<String, Object>)SerializationUtils.deserialize(body);
        doWork(map);
    }

    protected abstract void doWork(Map<String, Object> map);
    
    protected void returnResultMap(ResultMap results){
        try {
            responder.sendMessage(results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {
        try {
            responder.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}