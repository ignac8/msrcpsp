package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;
import org.apache.commons.lang3.RandomUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class OrderCrossover extends Crossover {

    public OrderCrossover(double chance) {
        super(chance);
    }

    @Override
    protected void crossover(Schedule firstSchedule, Schedule secondSchedule) {
        List<Task> firstTasks = firstSchedule.getTasks();
        List<Task> secondTasks = secondSchedule.getTasks();
        List<Task> firstTasksCopy = new Schedule(firstSchedule).getTasks();
        List<Task> secondTasksCopy = new Schedule(secondSchedule).getTasks();
        int randomInt = RandomUtils.nextInt(0, Math.min(firstTasks.size(), secondTasks.size()));
        swaps(firstTasks, secondTasksCopy, randomInt);
        swaps(secondTasks, firstTasksCopy, randomInt);
    }

    private void swaps(List<Task> tasks, List<Task> tasksCopy, int crossoverLimit) {
        IntStream.range(0, crossoverLimit).forEach(position -> swap(tasks, tasksCopy, position));
    }

    private void swap(List<Task> tasks, List<Task> tasksCopy, int positionInCopy) {
        int position = IntStream.range(0, tasks.size())
                .filter(innerCounter -> tasks.get(innerCounter).getTaskId() == tasksCopy.get(positionInCopy).getTaskId())
                .findFirst().getAsInt();
        Collections.swap(tasks, positionInCopy, position);
    }
}
