package it.zerko.msrcpsp.problem;

import java.util.ArrayList;
import java.util.List;

public class Task {

    private int taskId;
    private int duration;
    private String requiredSkillName;
    private int requiredSkillLevel;
    private int endTime;
    private List<Task> preconditions = new ArrayList<>();
    private Resource resource;

    public Task(int taskId, int duration, String requiredSkillName, int requiredSkillLevel) {
        this.taskId = taskId;
        this.duration = duration;
        this.requiredSkillName = requiredSkillName;
        this.requiredSkillLevel = requiredSkillLevel;
    }

    public Task(int taskId, int duration, String requiredSkillName, int requiredSkillLevel, List<Task> preconditions) {
        this.taskId = taskId;
        this.duration = duration;
        this.requiredSkillName = requiredSkillName;
        this.requiredSkillLevel = requiredSkillLevel;
        this.preconditions = preconditions;
    }

    public Task(Task copiedTask) {
        this.taskId = copiedTask.getTaskId();
        this.duration = copiedTask.getDuration();
        this.requiredSkillName = copiedTask.getRequiredSkillName();
        this.requiredSkillLevel = copiedTask.getRequiredSkillLevel();
        this.endTime = copiedTask.getEndTime();
    }

    public void addPrecondition(Task task) {
        preconditions.add(task);
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public List<Task> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(List<Task> preconditions) {
        this.preconditions = preconditions;
    }

    public String getRequiredSkillName() {
        return requiredSkillName;
    }

    public void setRequiredSkillName(String requiredSkillName) {
        this.requiredSkillName = requiredSkillName;
    }

    public int getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    public void setRequiredSkillLevel(int requiredSkillLevel) {
        this.requiredSkillLevel = requiredSkillLevel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public boolean checkIfResourceCanBeAssigned(Resource resource) {
        boolean found = false;
        List<Skill> skills = resource.getSkills();
        for (int counter = 0; counter < skills.size() && !found; counter++) {
            Skill skill = skills.get(counter);
            found = requiredSkillName.equals(skill.getName()) && requiredSkillLevel <= skill.getLevel();
        }
        return found;
    }

    public List<Resource> getPermittedResources(List<Resource> resources) {
        List<Resource> permittedResources = new ArrayList<>();
        for (Resource resource : resources) {
            if (checkIfResourceCanBeAssigned(resource)) {
                permittedResources.add(resource);
            }
        }
        return permittedResources;
    }
}
