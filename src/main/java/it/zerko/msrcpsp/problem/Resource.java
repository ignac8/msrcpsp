package it.zerko.msrcpsp.problem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
public class Resource {

    private int resourceId;
    private int freeTime;
    private List<Skill> skills;

    public Resource(int resourceId, List<Skill> skills) {
        this.resourceId = resourceId;
        this.skills = skills;
        this.freeTime = 0;
    }

    public Resource(Resource copiedResource) {
        this.resourceId = copiedResource.getResourceId();
        this.freeTime = copiedResource.getFreeTime();
        this.skills = new ArrayList<>(copiedResource.getSkills());
    }

    public boolean canSolve(Task task) {
        return skills.stream().anyMatch(task.getRequiredSkill()::isSufficient);
    }

}
