package be.gcroes.thesis.docproc.messaging;

import java.util.HashMap;
import java.util.Map;

public class ResultMap extends HashMap<String, Object>{
    
    public static final String META_SEP = "!";
    public static final String SET_TO_ARRAY_META_NAME = "ADD_TO_ARRAY";

    private static final long serialVersionUID = 3756327624834631132L;
    
    public ResultMap(Map<String, Object> map){
        super();
        put("instanceId", map.get("instanceId"));
        put("executionId", map.get("executionId"));
    }
    
    public void put(String key, Object value, String executionId){
        if(executionId != null)
            put(toExecutionForm(key, executionId), value);
        else
            put(key, value);
    }
    
    public static String extractMeta(String key) {
        String[] split = key.split(ResultMap.META_SEP);
        if(split.length > 1){
            return split[0];
        }
        return null;
    }
    
    public static String extractVarName(String key) {
        String[] split = key.split(ResultMap.META_SEP);
        if(split.length > 1){
            return split[1];
        }
        return key;
    }
    
    private String toExecutionForm(String key, String executionId){
        return executionId + META_SEP + key;
    }
    
    private String toIndexForm(String key, int index){
        return index + META_SEP + key;
    }
    
    public void addToActivitiArray(String arrayName, Object toAdd, String exid){
       setToActivitiArray(arrayName, toAdd, exid, -1);
    }
    
    public void addToActivitiArray(String arrayName, Object toAdd){
        addToActivitiArray(arrayName, toAdd, null);
    }
    
    public void setToActivitiArray(String arrayName, Object toSet, String exid, int index){
        @SuppressWarnings("unchecked")
        HashMap<String, Object> addToArrays = (HashMap<String, Object>)get(SET_TO_ARRAY_META_NAME);
        if(addToArrays == null){
            addToArrays = new HashMap<String, Object>();
        }
        addToArrays.put(toIndexForm(arrayName, index), toSet);
        put(SET_TO_ARRAY_META_NAME, addToArrays, exid);
    }

}
