import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class MyUnitTest {
    
    private static Logger logger = LoggerFactory.getLogger(MyUnitTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = { "bpmn/docproc.bpmn" })
    public void testDocproc() {
        logger.info("Testing docproc");
        
        Map<String, Object> variables = new HashMap<String, Object>();
        String template = "emptytemplate";
        String data = "emptydata";
        try {
            template = readFile(new File(getClass().getClassLoader()
                    .getResource("data/invoice-template.xsl").getFile()));
            data = readFile(new File(getClass().getClassLoader()
                    .getResource("data/data.csv").getFile()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        variables.put("userName", "Batman");
        variables.put("template", template);
        variables.put("data", data);
        variables.put("startAfter", null);
        variables.put("finishBefore", null);

        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("docproc", variables);
        assertNotNull(processInstance);
        
        logger.info("Test complete");
    }
    
    public String readFile(File file) throws IOException {
        Reader reader = new FileReader(file);
        StringBuilder sb = new StringBuilder();
        char buffer[] = new char[16384]; // read 16k blocks
        int len; // how much content was read?
        while ((len = reader.read(buffer)) > 0) {
            sb.append(buffer, 0, len);
        }
        reader.close();
        return sb.toString();
    }

}