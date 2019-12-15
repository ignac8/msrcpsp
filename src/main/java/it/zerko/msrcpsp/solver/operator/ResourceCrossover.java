package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Resource;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;

import java.util.List;

import static java.lang.Math.min;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class ResourceCrossover extends Crossover {

    public ResourceCrossover(int callCount, double chance) {
        super(callCount, chance);
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