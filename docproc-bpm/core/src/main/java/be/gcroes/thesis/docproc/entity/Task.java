package be.gcroes.thesis.docproc.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name="tasks")
public class Task implements Serializable{
    
    private static final long serialVersionUID = -1360450508409978500L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="jobid")
    private Job job;
    
    @Column(name="task_result", columnDefinition="TEXT")
    private String result;
    
    @ElementCollection(fetch=FetchType.EAGER)
    @MapKeyColumn(name="param_key", columnDefinition="TEXT", insertable=false, updatable=false)
    @CollectionTable(name="task_param",
                    joinColumns=@JoinColumn(name="taskid"))
    @Column(name="param_value", columnDefinition="TEXT")
    private Map<String, String> params = new HashMap<String, String>();
    
    public Task(){}
    
    public Task(Job job, String result, Map<String, String> params){
        this.job = job;
        this.result = result;
        this.params = params;
        job.addTask(this);
    }
    
    public Task(Job job, String result){
        this.job = job;
        this.result = result;
        job.addTask(this);
    }
    
    public Task(Job job){
        this(job, "NO RESULT");
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public Map<String, String> getParams() {
        return params;
    }
    
    public Set<String> getParamKeys(){
        return params.keySet();
    }
    
    public Collection<String> getParamValues(){
        return params.values();
    }
    
    public void addParam(String key, String value){
        params.put(key, value);
    }
    
    public void removeParam(String key){
        params.remove(key);
    }
}
