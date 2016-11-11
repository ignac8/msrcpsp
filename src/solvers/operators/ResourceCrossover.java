package solvers.operators;

import problem.Resource;
import problem.Schedule;
import problem.Task;

import java.util.List;

import static java.lang.Math.min;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class ResourceCrossover extends Crossover {

    public ResourceCrossover(double chance) {
        super(chance);
    }

    @Override
    protected void crossover(Schedule firstSchedule, Schedule secondSchedule) {
        List<Task> firstTasks = firstSchedule.getTasks();
        List<Task> secondTasks = secondSchedule.getTasks();
        int randomInt = nextInt(0, min(firstTasks.size(), secondTasks.size()));
        for (int outerCounter = 0; outerCounter < randomInt; outerCounter++) {
            Task firstTask = firstTasks.get(outerCounter);
            Resource firstResource = firstTask.getResource();
            Task secondTask = secondSchedule.getTaskWithId(firstTask.getTaskId());
            Resource secondResource = secondTask.getResource();
            firstTask.setResource(firstSchedule.getResourceWithId(secondResource.getResourceId()));
            secondTask.setResource(secondSchedule.getResourceWithId(firstResource.getResourceId()));
        }
    }
}