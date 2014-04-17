import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTest {

    private static Logger logger = LoggerFactory.getLogger(ProcessTest.class);

    //@Rule
    //public ActivitiRule activitiRule = new ActivitiRule();

    @Test
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
            e.printStackTrace();
        }

        variables.put("template", template);
        variables.put("data", data);

        variables.put("doMail", "true");
        variables.put("mailTo", "${email}");
        variables.put("mailSubject", "Docproc invoice");
        variables
                .put("mailBody",
                        "Dear ${name},\n \nYour document has been processed.\n\nKind regards,\nTeam Docproc");

        variables.put("startAfter", null);
        variables.put("finishBefore", null);
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey("docproc", variables);
        assertNotNull(processInstance);
        List<Execution> exes = processEngine.getRuntimeService().createExecutionQuery().list();
        logger.info("EXECUTIONS:");
        for(Execution ex : exes){
            logger.info("execution {}", ex.getId());
        }
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HistoricProcessInstance histProcessInstance = processEngine
                .getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        assertTrue(histProcessInstance != null);
        assertTrue(histProcessInstance.getEndTime() != null);
        logger.info("Test complete");
    }

    @Test
    public void testLDAPLogin() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        IdentityService is = processEngine.getIdentityService();
        assertTrue(is.checkPassword("activiti", "Test1234"));
        assertTrue(is.checkPassword("activiti-admin", "Test1234"));
        assertFalse(is.checkPassword("activiti", "wrongpasword"));
        assertFalse(is.checkPassword("activiti-admin", "wrongpasword"));

        List<Group> adminsGroups = is.createGroupQuery()
                .groupMember("activiti-admin").list();
        assertNotNull(adminsGroups);
        boolean adminInAdmins = false, adminInUsers = false;
        assertEquals(2, adminsGroups.size());
        for (Group adminGroup : adminsGroups) {
            String id = adminGroup.getId();
            if (id.equals(("admin"))) {
                adminInAdmins = true;
            }
            if (id.equals(("user"))) {
                adminInUsers = true;
            }
        }
        assertTrue(adminInAdmins);
        assertTrue(adminInUsers);
        List<Group> usersGroups = is.createGroupQuery().groupMember("activiti")
                .list();
        assertNotNull(usersGroups);
        assertEquals(1, usersGroups.size());
        assertEquals("user", usersGroups.get(0).getId());
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