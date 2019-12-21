package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderCrossover extends Crossover {

    public OrderCrossover(double chance) {
        super(chance);
    }

    @Override
    protected void crossover(Schedule firstSchedule, Schedule secondSchedule) {
        Schedule firstScheduleCopy = new Schedule(firstSchedule);
        Schedule secondScheduleCopy = new Schedule(secondSchedule);
        int randomInt = RandomUtils.nextInt(0, Math.min(firstSchedule.getTasks().size(), secondSchedule.getTasks().size()));
        firstSchedule.setTasks(swap(firstSchedule, secondScheduleCopy, randomInt));
        secondSchedule.setTasks(swap(secondSchedule, firstScheduleCopy, randomInt));
    }

    private List<Task> swap(Schedule schedule, Schedule scheduleCopy, int crossoverLimit) {
        List<Task> firstPart = IntStream.range(0, crossoverLimit)
                .mapToObj(i -> schedule.getTasks().get(i))
                .collect(Collectors.toList());
        List<Integer> tasksIds = firstPart.stream()
                .map(Task::getTaskId)
                .collect(Collectors.toList());
        List<Task> secondPart = IntStream.range(0, schedule.getTasks().size())
                .mapToObj(i -> scheduleCopy.getTasks().get(i))
                .filter(task -> !tasksIds.contains(task.getTaskId()))
                .map(Task::getTaskId)
                .map(schedule::getTaskWithId)
                .collect(Collectors.toList());
        firstPart.addAll(secondPart);
        return firstPart;
    }
}
