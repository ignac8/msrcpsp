package it.zerko.msrcpsp.problem;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class Schedule implements Comparable<Schedule> {

    private List<Task> tasks;
    private List<Resource> resources;
    private int totalTime;
    private int fitness;
    private Map<Task, List<Task>> preconditionsForTasks;
    private Map<Task, List<Resource>> permittedResources;
    private Map<Task, Resource> assignedResources;

    public Schedule(Schedule copiedSchedule) {
        tasks = copiedSchedule.getTasks().stream().map(Task::new).collect(Collectors.toList());
        resources = copiedSchedule.getResources().stream().map(Resource::new).collect(Collectors.toList());
        totalTime = copiedSchedule.getTotalTime();
        fitness = copiedSchedule.getFitness();
        preconditionsForTasks = new HashMap<>();
        copiedSchedule.getPreconditionsForTasks().forEach((task, preconditions) -> preconditionsForTasks.put(getTaskWithId(task.getTaskId()),
                preconditions.stream().map(task1 -> getTaskWithId(task1.getTaskId())).collect(Collectors.toList())));
        permittedResources = new HashMap<>();
        copiedSchedule.getPermittedResources().forEach((task, resources) -> permittedResources.put(getTaskWithId(task.getTaskId()),
                this.resources.stream().map(resource -> getResourceWithId(resource.getResourceId())).collect(Collectors.toList())));
        assignedResources = new HashMap<>();
        copiedSchedule.getAssignedResources().forEach((task, resource) -> assignedResources.put(getTaskWithId(task.getTaskId()), getResourceWithId(resource.getResourceId())));
    }

    public Schedule(List<String> lines) {
        LineType lineType = LineType.UNKNOWN;
        resources = new ArrayList<>();
        tasks = new ArrayList<>();
        preconditionsForTasks = new HashMap<>();
        permittedResources = new HashMap<>();
        assignedResources = new HashMap<>();
        for (String line : lines) {
            if (line.startsWith("ResourceID")) {
                lineType = LineType.RESOURCE;
            } else if (line.startsWith("TaskID")) {
                lineType = LineType.TASK;
            } else if (line.startsWith("===")) {
                lineType = LineType.UNKNOWN;
            } else {
                String[] split = line.split("[Q:\\s]+");
                switch (lineType) {
                    case RESOURCE:
                        int resourceId = Integer.parseInt(split[0]);
                        List<Skill> skills = IntStream.iterate(2, counter -> counter < split.length, counter -> counter + 2)
                                .mapToObj(counter -> new Skill(split[counter], Integer.parseInt(split[counter + 1])))
                                .collect(Collectors.toList());
                        resources.add(new Resource(resourceId, skills));
                        break;
                    case TASK:
                        int taskId = Integer.parseInt(split[0]);
                        int duration = Integer.parseInt(split[1]);
                        Skill requiredSkill = new Skill(split[2], Integer.parseInt(split[3]));
                        Task task = new Task(taskId, duration, requiredSkill);
                        tasks.add(task);
                        preconditionsForTasks.put(task, Arrays.stream(split, 4, split.length)
                                .map(Integer::parseInt)
                                .map(this::getTaskWithId)
                                .collect(Collectors.toList()));
                        permittedResources.put(task, resources.stream()
                                .filter(resource -> resource.canSolve(task))
                                .collect(Collectors.toList()));
                        break;
                    case UNKNOWN:
                        break;
                }
            }
        }
    }

    public void calculateFitness() {
        tasks.forEach(task -> task.setEndTime(Optional.empty()));
        resources.forEach(value -> value.setFreeTime(0));
        tasks.forEach(this::calculateFitness);
        fitness = resources.stream().mapToInt(Resource::getFreeTime).max().getAsInt();
    }

    private void calculateFitness(Task task) {
        preconditionsForTasks.get(task).forEach(this::calculateFitness);
        if (task.getEndTime().isEmpty()) {
            int startTime = Math.max(
                    assignedResources
                            .get(task)
                            .getFreeTime(),
                    preconditionsForTasks.get(task)
                            .stream()
                            .map(Task::getEndTime)
                            .map(Optional::get)
                            .mapToInt(Integer::intValue)
                            .max()
                            .orElse(0));
            task.setEndTime(Optional.of(startTime + task.getDuration()));
            assignedResources.get(task).setFreeTime(startTime + task.getDuration());
        }
    }

    public void assignRandomResourcesToTasks() {
        tasks.forEach(this::assignRandomResourceToTask);
    }

    public void assignRandomResourceToTask(Task task) {
        List<Resource> permittedResources = this.permittedResources.get(task);
        assignedResources.put(task, permittedResources.get(RandomUtils.nextInt(0, permittedResources.size())));
    }

    public Resource getResourceWithId(int resourceId) {
        return resources.stream()
                .filter(resource -> resource.getResourceId() == resourceId)
                .findFirst()
                .get();
    }

    public Task getTaskWithId(int taskId) {
        return tasks.stream()
                .filter(task -> task.getTaskId() == taskId)
                .findFirst()
                .get();
    }

    public List<String> toSolution() {
        Map<Integer, List<Task>> map = new LinkedHashMap<>();
        tasks.forEach(task -> {
            int startTime = task.getEndTime().get() - task.getDuration();
            List<Task> list = map.getOrDefault(startTime, new ArrayList<>());
            list.add(task);
            map.put(startTime, list);
        });
        return map.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(this::startTimeToString)
                .collect(Collectors.toList());
    }

    private String startTimeToString(Map.Entry<Integer, List<Task>> entry) {
        return String.format("%d %s",
                entry.getKey(),
                entry.getValue()
                        .stream()
                        .map(task -> String.format("%d-%d",
                                getAssignedResources().get(task).getResourceId(),
                                task.getTaskId()))
                        .collect(Collectors.joining(" ")));
    }

    @Override
    public int compareTo(Schedule schedule) {
        return Double.compare(this.fitness, schedule.getFitness());
    }

    private enum LineType {
        RESOURCE, TASK, UNKNOWN
    }
}
