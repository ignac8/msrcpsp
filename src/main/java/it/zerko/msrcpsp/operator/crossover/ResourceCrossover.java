package it.zerko.msrcpsp.operator.crossover;

import it.zerko.msrcpsp.problem.Resource;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.stream.IntStream;

public class ResourceCrossover extends Crossover {

    public ResourceCrossover(double chance) {
        super(chance);
    }

    @Override
    protected void crossover(Schedule firstSchedule, Schedule secondSchedule) {
        List<Task> firstTasks = firstSchedule.getTasks();
        List<Task> secondTasks = secondSchedule.getTasks();
        int randomInt = RandomUtils.nextInt(0, Math.min(firstTasks.size(), secondTasks.size()));
        IntStream.range(0, randomInt).forEach(outerCounter -> swapResources(firstSchedule, secondSchedule, firstTasks, outerCounter));
    }

    private void swapResources(Schedule firstSchedule, Schedule secondSchedule, List<Task> firstTasks, int position) {
        Task firstTask = firstTasks.get(position);
        Resource firstResource = firstSchedule.getAssignedResources().get(firstTask);
        Task secondTask = secondSchedule.getTaskWithId(firstTask.getTaskId());
        Resource secondResource = secondSchedule.getAssignedResources().get(secondTask);
        firstSchedule.getAssignedResources().put(firstTask, firstSchedule.getResourceWithId(secondResource.getResourceId()));
        secondSchedule.getAssignedResources().put(secondTask, secondSchedule.getResourceWithId(firstResource.getResourceId()));
    }
}