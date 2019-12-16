package it.zerko.msrcpsp.problem;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class Task {

    private int taskId;
    private int duration;
    private Skill requiredSkill;
    private Optional<Integer> endTime;

    public Task(int taskId, int duration, Skill requiredSkill) {
        this.taskId = taskId;
        this.duration = duration;
        this.requiredSkill = requiredSkill;
        this.endTime = Optional.empty();
    }

    public Task(Task copiedTask) {
        this.taskId = copiedTask.getTaskId();
        this.duration = copiedTask.getDuration();
        this.requiredSkill = copiedTask.getRequiredSkill();
        this.endTime = copiedTask.getEndTime().or(Optional::empty);
    }


}
