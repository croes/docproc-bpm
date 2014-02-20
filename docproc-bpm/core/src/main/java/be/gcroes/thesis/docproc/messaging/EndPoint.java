package be.gcroes.thesis.docproc.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class EndPoint {
    
    private static Logger logger = LoggerFactory.getLogger(EndPoint.class);
    
    protected Channel channel;
    protected Connection connection;
    protected String endPointName;
    
    public EndPoint(String endpointName) throws IOException{
         Properties props = new Properties();
         InputStream stream = ClassLoader.getSystemResourceAsStream("worker.properties");
         if(stream == null){
             throw new IOException("could not load worker.properties");
         }
         props.load(stream);
         String brokerIp = props.getProperty("BROKER_IP", "localhost");
         String brokerPort = props.getProperty("BROKER_PORT", "5762");
         logger.info("Connecting to message broker on host {}:{}", brokerIp, brokerPort);
         this.endPointName = endpointName;
        
         //Create a connection factory
         ConnectionFactory factory = new ConnectionFactory();
        
         //hostname of your rabbitmq server
         factory.setHost(brokerIp);
         factory.setPort(Integer.parseInt(brokerPort));
         //getting a connection
         connection = factory.newConnection();
        
         //creating a channel
         channel = connection.createChannel();
        
         //declaring a queue for this channel. If queue does not exist,
         //it will be created on the server.
         channel.queueDeclare(endpointName, true, false, false, null);
    }
    
    
    /**
     * Close channel and connection. Not necessary as it happens implicitly any way. 
     * @throws IOException
     */
     public void close() throws IOException{
         this.channel.close();
         this.connection.close();
     }

}
