package problem;

import java.util.ArrayList;
import java.util.List;

public class Resource {

    private int resourceId;
    private double salary;
    private int freeTime;

    private List<Skill> skills = new ArrayList<>();

    public Resource(int resourceId, double salary) {
        this.resourceId = resourceId;
        this.salary = salary;
    }

    public Resource(int resourceId, double salary, List<Skill> skills) {
        this.resourceId = resourceId;
        this.salary = salary;
        this.skills = skills;
    }

    public Resource(Resource copiedResource) {
        this.resourceId = copiedResource.getResourceId();
        this.salary = copiedResource.getSalary();
        this.freeTime = copiedResource.getFreeTime();
        this.skills.addAll(copiedResource.getSkills());
    }

    public void addSkill(String name, int level) {
        skills.add(new Skill(name, level));
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getFreeTime() {
        return freeTime;
    }

    public void setFreeTime(int freeTime) {
        this.freeTime = freeTime;
    }
}
