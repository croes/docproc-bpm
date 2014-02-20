package be.gcroes.thesis.docproc.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.messaging.QueueConsumer;
import be.gcroes.thesis.docproc.messaging.ResultMap;

public class IncomingQueueWorker extends QueueConsumer{
    
    private static Logger logger = LoggerFactory
            .getLogger(IncomingQueueWorker.class);
    
    public static final String INC_QUEUE_NAME = "docproc-inc";
    public static final int MAX_RETRIES = 5;
    public static final long RETRY_TIME = 2000;
    
    public IncomingQueueWorker() throws IOException {
        super(INC_QUEUE_NAME);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doWork(Map<String, Object> map) {
        String instanceId = (String)map.get("instanceId");
        String exid = (String)map.get("executionId");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        List<Execution> exes = runtimeService.createExecutionQuery().list();
//        logger.info("EXECUTIONS:");
//        for(Execution ex : exes){
//            logger.info("execution {}", ex.getId());
//        }
        Execution execution = findExecution(exid, runtimeService);
        if(execution == null){
            logger.error("ERROR: Could not find execution with id {}, giving up after {} attempts.", exid, MAX_RETRIES);
            return;
        }
        logger.info("Found execution: {}", execution.getId());
        for(Entry<String, Object> e : map.entrySet()){
            String exId = ResultMap.extractMeta(e.getKey());
            String varName = ResultMap.extractVarName(e.getKey());
            if(exId == null){
                exId = instanceId;
            }
            if(varName.equals(ResultMap.SET_TO_ARRAY_META_NAME)){
                HashMap<String, Object> addToArray = (HashMap<String, Object>) e.getValue();
                for(Entry<String, Object> entry : addToArray.entrySet()){
                    Object value = entry.getValue();
                    String arrayName = ResultMap.extractVarName(entry.getKey());
                    String indexStr = ResultMap.extractMeta(entry.getKey());
                    List<Object> activitiArray = (List<Object>)runtimeService.getVariable(exId, arrayName);
                    if(activitiArray == null){
                        activitiArray = new ArrayList<Object>();
                    }
                    if(indexStr == null){
                        activitiArray.add(addToArray.get(arrayName));
                    }else{
                        activitiArray.set(Integer.parseInt(indexStr), value);
                    }
                    runtimeService.setVariable(exId, arrayName, activitiArray);
                }
            }
            else{
                try{
                    if(! (varName.equals("instanceId") || varName.equals("executionId"))){
                        logger.debug("setting variable {} to value {}", varName, e.getValue());
                        runtimeService.setVariable(exId, varName, e.getValue());
                    }
                }
                catch(ActivitiObjectNotFoundException aonfe){
                    aonfe.printStackTrace();
                    logger.info("error occured when setting variable {} to value {} in execution {}",varName, e.getValue(), exId);
                }
            }
        }
        logger.info("Signaling execution {}", exid);
        runtimeService.signal(exid);
    }

    private Execution findExecution(String exid, RuntimeService runtimeService) {
        int retry = 1;
        Execution execution = null;
        while(execution == null && retry < MAX_RETRIES){
            if(retry > 1){
                try {
                    logger.info("Did not find execution.");
                    Thread.sleep(RETRY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            logger.info("Fetching execution: attempt {}", retry);
            execution = runtimeService.createExecutionQuery().executionId(exid).singleResult();
            retry++;
        }
        return execution;
    }

    public static void main(String[] args) throws IOException {
        IncomingQueueWorker inc = new IncomingQueueWorker();
        Thread t = new Thread(inc);
        t.start();
    }
}
