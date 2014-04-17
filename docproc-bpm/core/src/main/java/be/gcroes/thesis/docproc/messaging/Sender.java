package be.gcroes.thesis.docproc.messaging;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;


public class Sender {
    
    private static final String TASK_QUEUE_NAME = "test_queue";
    
    public static void sendMessage(String queueName, byte[] message) throws IOException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);


        channel.basicPublish( "", TASK_QUEUE_NAME, 
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message);
        System.out.println(" [x] Sent '" + new String(message) + "'");

        channel.close();
        connection.close();
    }

    public static void main(String[] argv) 
                        throws java.io.IOException {
       sendMessage(TASK_QUEUE_NAME, "test test test".getBytes());
    }
    
    
}
