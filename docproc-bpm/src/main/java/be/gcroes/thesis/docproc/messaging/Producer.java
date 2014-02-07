package be.gcroes.thesis.docproc.messaging;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.SerializationUtils;


/**
 * The producer endpoint that writes to the queue.
 * @author syntx
 *
 */
public class Producer extends EndPoint{
    
    public Producer(String endPointName) throws IOException{
        super(endPointName);
    }

    public void sendMessage(Serializable object) throws IOException {
        channel.basicPublish("",endPointName, null, SerializationUtils.serialize(object));
    }
    
    public static void main(String[] args) throws IOException {
        Producer p = new Producer("csv-to-data");
        String data = readFileAsString("C:\\Users\\glenn\\git\\docproc-bpm\\docproc-bpm\\src\\main\\resources\\data\\data.csv");
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("data", data);
        p.sendMessage(vars);
        System.out.println("Sent data: " + data);
        p.close();
    }
    
    public static String readFileAsString(String filePath) throws java.io.IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line, results = "";
        while( ( line = reader.readLine() ) != null)
        {
            results += line;
            results += "\n";
        }
        reader.close();
        return results;
    }
}
