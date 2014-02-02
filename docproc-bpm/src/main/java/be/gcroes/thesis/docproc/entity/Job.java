package be.gcroes.thesis.docproc.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Jobs")
public class Job implements Serializable {

    private static final long serialVersionUID = -1644990722029309133L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int id;
    
    @Column(name="activiti_instance_id", columnDefinition="VARCHAR(64)")
    private String activitiJobId;
    
    @Column(name="result", columnDefinition="TEXT")
    private String result;
    
    @Column(name="template", columnDefinition="TEXT")
    private String template;
    
    @Column(name="inputdata", columnDefinition="TEXT")
    private String inputdata;
    
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getInputdata() {
        return inputdata;
    }

    public void setInputdata(String inputdata) {
        this.inputdata = inputdata;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @OneToMany(mappedBy="job", cascade=CascadeType.ALL)
    private List<Task> tasks = new ArrayList<Task>();
    
    public Job(){
       
    }
    
    public Job(String activitiJobId){
        this.activitiJobId = activitiJobId;
    }

    public String getActivitiJobId() {
        return activitiJobId;
    }

    public void setActivitiJobId(String activitiJobId) {
        this.activitiJobId = activitiJobId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public void addTask(Task task){
        tasks.add(task);
        if(task.getJob() != this){
            task.setJob(this);
        }
    }
    
    public void removeTask(Task task){
        tasks.remove(task);
    }
    
    public void addTasks(List<Task> tasks){
        tasks.addAll(tasks);
    }

    public int getId() {
        return id;
    }
    
}
