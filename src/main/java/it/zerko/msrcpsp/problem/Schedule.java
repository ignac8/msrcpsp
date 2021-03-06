package it.zerko.msrcpsp.problem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ToString
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
    private Map<Integer, Task> tasksWithIds;
    private Map<Integer, Resource> resourcesWithIds;

    public Schedule(Schedule copiedSchedule) {
        tasks = copiedSchedule.getTasks().stream().map(Task::new).collect(Collectors.toList());
        tasksWithIds = tasks.stream().collect(Collectors.toMap(Task::getTaskId, Function.identity()));
        resources = copiedSchedule.getResources().stream().map(Resource::new).collect(Collectors.toList());
        resourcesWithIds = resources.stream().collect(Collectors.toMap(Resource::getResourceId, Function.identity()));
        totalTime = copiedSchedule.getTotalTime();
        fitness = copiedSchedule.getFitness();
        preconditionsForTasks = new HashMap<>();
        copiedSchedule.getPreconditionsForTasks().forEach((task, preconditions) -> preconditionsForTasks.put(
                getTaskWithId(task.getTaskId()), preconditions.stream().map(precondition ->
                        getTaskWithId(precondition.getTaskId())).collect(Collectors.toList())));
        permittedResources = new HashMap<>();
        copiedSchedule.getPermittedResources().forEach((task, resources) -> permittedResources.put(
                getTaskWithId(task.getTaskId()), resources.stream().map(resource ->
                        getResourceWithId(resource.getResourceId())).collect(Collectors.toList())));
        assignedResources = new HashMap<>();
        copiedSchedule.getAssignedResources().forEach((task, resource) ->
                assignedResources.put(getTaskWithId(task.getTaskId()), getResourceWithId(resource.getResourceId())));
    }

    public Schedule(List<String> dataset) {
        LineType lineType = LineType.UNKNOWN;
        resources = new ArrayList<>();
        tasks = new ArrayList<>();
        tasksWithIds = new HashMap<>();
        resourcesWithIds = new HashMap<>();
        preconditionsForTasks = new HashMap<>();
        permittedResources = new HashMap<>();
        assignedResources = new HashMap<>();
        for (String line : dataset) {
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
                        List<Skill> skills = IntStream.iterate(2, counter -> counter < split.length,
                                counter -> counter + 2)
                                .mapToObj(counter -> new Skill(split[counter], Integer.parseInt(split[counter + 1])))
                                .collect(Collectors.toList());
                        Resource resource = new Resource(resourceId, skills);
                        resources.add(resource);
                        resourcesWithIds.put(resourceId, resource);
                        break;
                    case TASK:
                        int taskId = Integer.parseInt(split[0]);
                        int duration = Integer.parseInt(split[1]);
                        Skill requiredSkill = new Skill(split[2], Integer.parseInt(split[3]));
                        Task task = new Task(taskId, duration, requiredSkill);
                        tasks.add(task);
                        tasksWithIds.put(taskId, task);
                        preconditionsForTasks.put(task, Arrays.stream(split, 4, split.length)
                                .map(Integer::parseInt)
                                .map(this::getTaskWithId)
                                .collect(Collectors.toList()));
                        permittedResources.put(task, resources.stream()
                                .filter(possibleResource -> possibleResource.canSolve(task))
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

    public void calculateFitness(List<Task> tasks) {
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

    private void assignRandomResourceToTask(Task task) {
        List<Resource> permittedResourcesForTask = permittedResources.get(task);
        assignedResources.put(task, permittedResourcesForTask.get(RandomUtils.nextInt(0,
                permittedResourcesForTask.size())));
    }

    public Resource getResourceWithId(int resourceId) {
        return resourcesWithIds.get(resourceId);
    }

    public Task getTaskWithId(int taskId) {
        return tasksWithIds.get(taskId);
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

    public JFreeChart toGraph() {
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        TaskSeries taskSeries = new TaskSeries("Busy");
        assignedResources
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey,
                        Collectors.toList())))
                .entrySet()
                .stream()
                .map(this::getChartTaskSeries)
                .forEach(taskSeries::add);
        dataset.add(taskSeries);
        return ChartFactory.createGanttChart("Scheduling", "Resource", "Time",
                dataset);
    }

    private org.jfree.data.gantt.Task getChartTaskSeries(Map.Entry<Resource, List<Task>> entry) {
        Resource resource = entry.getKey();
        List<Task> tasks = entry.getValue();
        int tasksStartTime = tasks.stream()
                .mapToInt(task -> task.getEndTime().get() - task.getDuration())
                .min()
                .getAsInt();
        int tasksEndTime = tasks.stream()
                .mapToInt(task -> task.getEndTime().get())
                .max()
                .getAsInt();
        org.jfree.data.gantt.Task chartTask = new org.jfree.data.gantt.Task(String.format("Resource %d",
                resource.getResourceId()),
                Date.from(ZonedDateTime.now().plusDays(tasksStartTime).toInstant()),
                Date.from(ZonedDateTime.now().plusDays(tasksEndTime).toInstant()));
        tasks.stream()
                .map(task -> new org.jfree.data.gantt.Task(String.format("Task %d", task.getTaskId()),
                        Date.from(ZonedDateTime.now().plusDays(task.getEndTime().get() - task.getDuration()).toInstant()),
                        Date.from(ZonedDateTime.now().plusDays(task.getEndTime().get()).toInstant())))
                .forEach(chartTask::addSubtask);
        return chartTask;
    }

    public List<Schedule> getResourceNeighbours() {
        return tasks.stream()
                .map(this::getResourceNeighbours)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Schedule> getResourceNeighbours(Task task) {
        return permittedResources.get(task).stream()
                .map(resource -> getResourceNeighbour(task, resource))
                .collect(Collectors.toList());
    }

    private Schedule getResourceNeighbour(Task task, Resource resource) {
        Schedule schedule = new Schedule(this);
        schedule.mutateResource(task, resource);
        return schedule;
    }

    private void mutateResource(Task task, Resource resource) {
        getAssignedResources().put(
                getTaskWithId(task.getTaskId()),
                getResourceWithId(resource.getResourceId()));
    }

    public Schedule getResourceNeighbour() {
        Schedule schedule = new Schedule(this);
        schedule.mutateResource();
        return schedule;
    }

    public void mutateResource() {
        Task task = tasks.get(RandomUtils.nextInt(0, tasks.size()));
        Resource resource = permittedResources.get(task).get(RandomUtils.nextInt(0,
                permittedResources.get(task).size()));
        mutateResource(task, resource);

    }

    public List<Schedule> getOrderNeighbours() {
        return IntStream.range(0, tasks.size())
                .mapToObj(this::getOrderNeighbours)
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private Stream<Schedule> getOrderNeighbours(int firstPosition) {
        return IntStream.range(0, tasks.size())
                .mapToObj(secondPosition -> getOrderNeighbour(firstPosition, secondPosition));
    }

    private Schedule getOrderNeighbour(int firstPosition, int secondPosition) {
        Schedule schedule = new Schedule(this);
        schedule.mutateOrder(firstPosition, secondPosition);
        return schedule;
    }

    private void mutateOrder(int firstPosition, int secondPosition) {
        Collections.swap(tasks, firstPosition, secondPosition);
    }

    public Schedule getOrderNeighbour() {
        Schedule schedule = new Schedule(this);
        schedule.mutateOrder();
        return schedule;
    }

    public void mutateOrder() {
        int firstPosition = RandomUtils.nextInt(0, tasks.size());
        int secondPosition = RandomUtils.nextInt(0, tasks.size());
        mutateOrder(firstPosition, secondPosition);
    }

    @Override
    public int compareTo(Schedule schedule) {
        return Double.compare(this.fitness, schedule.getFitness());
    }

    private enum LineType {
        RESOURCE, TASK, UNKNOWN
    }
}
