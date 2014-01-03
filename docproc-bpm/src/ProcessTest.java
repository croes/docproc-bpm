import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTest extends ActivitiTestCase {

    private static Logger logger = LoggerFactory.getLogger(ProcessTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule(
            "/activiti-test.cfg.xml");

    @Test
    @Deployment(resources = { "bpmn/docproc.bpmn" })
    public void testProcess() {
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
        variables.put("endBefore", null);

        RuntimeService runtimeService = activitiRule.getRuntimeService();
        assertNotNull(runtimeService);
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("docproc");//, variables);
        assertNotNull(processInstance);
        // Verify that we started a new process instance
        logger.info("Number of process instances: "
                + runtimeService.createProcessInstanceQuery().count());
    }

    public String getConfigurationResource() {
        return "/activiti-test.cfg.xml";
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
