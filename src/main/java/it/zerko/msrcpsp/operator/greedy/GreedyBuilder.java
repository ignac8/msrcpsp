package it.zerko.msrcpsp.operator.greedy;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Resource;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GreedyBuilder extends Operator {

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::greedyBuild)
                .collect(Collectors.toList());
    }

    private Schedule greedyBuild(Schedule schedule) {
        schedule = new Schedule(schedule);
        List<Task> unusedTasks = schedule.getTasks();
        Collections.shuffle(unusedTasks);
        List<Task> tasks = new ArrayList<>();
        while (!unusedTasks.isEmpty()) {
            Schedule finalSchedule = schedule;
            List<SchedulePiece> schedulePieces = unusedTasks.stream()
                    .filter(task -> checkIfScheduleHasPreconditions(unusedTasks, finalSchedule, task))
                    .map(task -> generateSchedulePieces(finalSchedule, task))
                    .flatMap(Collection::stream)
                    .peek(schedulePiece -> calculateFitness(tasks, finalSchedule, schedulePiece))
                    .collect(Collectors.toList());
            Collections.shuffle(schedulePieces);
            SchedulePiece schedulePiece = schedulePieces.stream()
                    .min(Comparator.comparingInt(SchedulePiece::getFitness))
                    .get();
            tasks.add(schedulePiece.getTask());
            unusedTasks.remove(schedulePiece.getTask());
            schedule.getAssignedResources().put(schedulePiece.getTask(), schedulePiece.getResource());
        }
        schedule.setTasks(tasks);
        return schedule;
    }

    private void calculateFitness(List<Task> tasks, Schedule schedule, SchedulePiece schedulePiece) {
        List<Task> newTasks = new ArrayList<>(tasks);
        newTasks.add(schedulePiece.getTask());
        schedule.getAssignedResources().put(schedulePiece.getTask(), schedulePiece.getResource());
        schedule.calculateFitness(newTasks);
        schedulePiece.setFitness(schedule.getFitness());
    }

    private boolean checkIfScheduleHasPreconditions(List<Task> unusedTasks, Schedule schedule, Task task) {
        return schedule.getPreconditionsForTasks()
                .get(task)
                .stream()
                .noneMatch(unusedTasks::contains);
    }

    private List<SchedulePiece> generateSchedulePieces(Schedule schedule, Task task) {
        return schedule.getPermittedResources()
                .get(task)
                .stream()
                .map(resource -> new SchedulePiece(task, resource))
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    @ToString
    private static class SchedulePiece {
        private Task task;
        private Resource resource;
        private int fitness;

        public SchedulePiece(Task task, Resource resource) {
            this.task = task;
            this.resource = resource;
        }
    }
}
