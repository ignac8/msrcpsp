package it.zerko.msrcpsp.problem;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.compare;
import static java.lang.Math.floorMod;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.swap;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class Schedule implements Comparable<Schedule> {

    private String readable;
    private List<Task> tasks;
    private List<Resource> resources;
    private int totalTime;
    private double totalSalary;
    private double fitness;

    public Schedule(Schedule copiedSchedule) {
        this.totalTime = copiedSchedule.getTotalTime();
        this.totalSalary = copiedSchedule.getTotalSalary();
        this.fitness = copiedSchedule.getFitness();
        this.resources = copiedSchedule.getResources();
        this.resources = new ArrayList<>(copiedSchedule.getResources().size());
        for (Resource copiedResource : copiedSchedule.getResources()) {
            this.resources.add(new Resource(copiedResource));
        }
        this.tasks = new ArrayList<>(copiedSchedule.getTasks().size());
        for (Task task : copiedSchedule.getTasks()) {
            this.tasks.add(new Task(task));
        }
        for (int counter = 0; counter < this.tasks.size(); counter++) {
            Task copiedTask = copiedSchedule.getTasks().get(counter);
            Resource copiedResource = copiedTask.getResource();
            Task thisTask = this.tasks.get(counter);
            if (copiedResource != null) {
                int copiedResourceId = copiedResource.getResourceId();

                if (copiedResourceId != 0) {
                    //Resource thisResource = this.resources.get(copiedResourceId);
                    Resource thisResource = this.getResourceWithId(copiedResourceId);
                    thisTask.setResource(thisResource);
                }
            }
            for (Task copiedPrecondition : copiedTask.getPreconditions()) {
                int copiedResourceId = copiedSchedule.getTasks().indexOf(copiedPrecondition);
                thisTask.addPrecondition(this.tasks.get(copiedResourceId));
            }
        }
    }

    public Schedule() {
    }

    public Schedule(List<Task> tasks, List<Resource> resources) {
        this.tasks = tasks;
        this.resources = resources;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public String getReadable() {
        return readable;
    }

    public void setReadable(String readable) {
        this.readable = readable;
    }

    public void calculate() {
        for (Task task : tasks) {
            task.setEndTime(0);
        }
        for (Resource resource : resources) {
            resource.setFreeTime(0);
        }
        for (Task task : tasks) {
            calculate(task);
        }
//        readable = "";
//        for (Task task : tasks) {
//            readable += task.getTaskId() + ":" + task.getResource().getResourceId() + " ";
//        }
        fitness();
    }

    private void calculate(Task task) {
        for (Task precondition : task.getPreconditions()) {
            calculate(precondition);
        }
        if (task.getEndTime() == 0) {
            int startTime = task.getResource().getFreeTime();
            for (Task precondition : task.getPreconditions()) {
                startTime = max(startTime, precondition.getEndTime());
            }
            task.setEndTime(startTime + task.getDuration());
            task.getResource().setFreeTime(startTime + task.getDuration());
        }
    }

    private void fitness() {
        //totalSalary = 0;
        totalTime = 0;
        /*for (Task task : tasks) {
            totalSalary += task.getDuration() * task.getResource().getSalary();
            totalTime = max(totalTime, task.getEndTime());
        }*/
        for (Resource resource : resources) {
            totalTime = max(totalTime, resource.getFreeTime());
        }
        fitness = totalTime;
        /*
        double weight = 0.5;
        int maxTime = 0;
        for (Task task : tasks) {
            maxTime += task.getDuration();
        }
        double minSalary = Double.MAX_VALUE;
        double maxSalary = Double.MIN_VALUE;
        for (Resource resource : resources) {
            minSalary = min(minSalary, resource.getSalary());
            maxSalary = max(maxSalary, resource.getSalary());
        }
        fitness = weight * totalTime / maxTime + (1 - weight) * totalSalary / (totalTime * (maxSalary - minSalary));
        */
    }

    public void assignRandomResourceToTask(Task task) {
        List<Resource> listOfPermittedResources = task.getPermittedResources(resources);
        Resource randomResource = listOfPermittedResources.get(nextInt(0, listOfPermittedResources.size()));
        task.setResource(randomResource);
    }

    public Resource getResourceWithId(int resourceId) {
        Resource foundResource = null;
        for (int counter = 0; counter < resources.size() && foundResource == null; counter++) {
            Resource possibleResource = resources.get(counter);
            if (possibleResource.getResourceId() == resourceId) {
                foundResource = possibleResource;
            }
        }
        return foundResource;
    }

    public Task getTaskWithId(int taskId) {
        Task foundTask = null;
        for (int counter = 0; counter < tasks.size() && foundTask == null; counter++) {
            Task possibleTask = tasks.get(counter);
            if (possibleTask.getTaskId() == taskId) {
                foundTask = possibleTask;
            }
        }
        return foundTask;
    }

    public List<Schedule> generateNeighbours() {
        List<Schedule> schedules = new ArrayList<>();
        schedules = generateNeighboursByOrder(schedules);
        schedules = generateNeighboursByResource(schedules);
        return schedules;
    }

    public List<Schedule> generateRandomNeighbours(int size) {
        List<Schedule> schedules = new ArrayList<>();
        for (int counter = 0; counter < size; counter++) {
            Schedule schedule = nextBoolean() ? generateRandomNeighbourByOrder() : generateRandomNeighbourByResource();
            schedules.add(schedule);
        }
        return schedules;
    }

    private List<Schedule> generateNeighboursByResource(List<Schedule> schedules) {
        for (int counter = 0; counter < tasks.size(); counter++) {
            Schedule prevSchedule = new Schedule(this);
            Schedule nextSchedule = new Schedule(this);
            List<Task> prevTasks = prevSchedule.getTasks();
            List<Task> nextTasks = nextSchedule.getTasks();
            Task task = tasks.get(counter);
            Task prevTask = prevTasks.get(counter);
            Task nextTask = nextTasks.get(counter);
            List<Resource> prevResources = prevSchedule.getResources();
            List<Resource> nextResources = nextSchedule.getResources();
            List<Resource> permittedResources = task.getPermittedResources(this.resources);
            List<Resource> prevPermittedResources = prevTask.getPermittedResources(prevResources);
            List<Resource> nextPermittedResources = nextTask.getPermittedResources(nextResources);
            int id = permittedResources.indexOf(task.getResource());
            Resource prevResource = prevPermittedResources.get((id - 1) % prevPermittedResources.size());
            Resource nextResource = nextPermittedResources.get((id + 1) % nextPermittedResources.size());
            prevTask.setResource(prevResource);
            nextTask.setResource(nextResource);
            schedules.add(prevSchedule);
            schedules.add(nextSchedule);
        }
        return schedules;
    }

    private Schedule generateRandomNeighbourByResource() {
        Schedule clonedSchedule = new Schedule(this);
        List<Task> clonedTasks = clonedSchedule.getTasks();
        int randomTaskId = nextInt(0, tasks.size());
        Task task = tasks.get(randomTaskId);
        Task clonedTask = clonedTasks.get(randomTaskId);
        List<Resource> clonedResources = clonedSchedule.getResources();
        List<Resource> permittedResources = task.getPermittedResources(this.resources);
        List<Resource> clonedPermittedResources = clonedTask.getPermittedResources(clonedResources);
        int resourceId = permittedResources.indexOf(task.getResource());
        int shift = nextBoolean() ? -1 : 1;
        //int kurczak = (resourceId + shift) % clonedPermittedResources.size();
        int kurczak = floorMod(resourceId + shift, clonedPermittedResources.size());
        Resource clonedResource = clonedPermittedResources.get(kurczak);
        clonedTask.setResource(clonedResource);
        return clonedSchedule;
    }

    private List<Schedule> generateNeighboursByOrder(List<Schedule> schedules) {
        for (int counter = 0; counter < tasks.size(); counter++) {
            Schedule clonedSchedule = new Schedule(this);
            swap(clonedSchedule.getTasks(), counter % tasks.size(), (counter + 1) % tasks.size());
            schedules.add(clonedSchedule);
        }
        return schedules;
    }

    private Schedule generateRandomNeighbourByOrder() {
        Schedule clonedSchedule = new Schedule(this);
        int randomIndex = nextInt(0, tasks.size());
        swap(clonedSchedule.getTasks(), randomIndex % tasks.size(), (randomIndex + 1) % tasks.size());
        return clonedSchedule;
    }

    public boolean isSame(Schedule otherSchedule) {
        boolean same = true;
        List<Task> otherTasks = otherSchedule.getTasks();
        for (int counter = 0; counter < this.tasks.size() && same; counter++) {
            Task thisTask = this.tasks.get(counter);
            Task otherTask = otherTasks.get(counter);
            same = thisTask.getTaskId() != otherTask.getTaskId() && thisTask.getResource().getResourceId() != otherTask.getResource().getResourceId();
        }
        return same;
    }

    public int maxTime() {
        int sumDuration = 0;
        for (Task task : tasks) {
            sumDuration += task.getDuration();
        }
        return sumDuration;
    }

    public int minTime() {
        int maxDuration = Integer.MIN_VALUE;
        for (Task task : tasks) {
            maxDuration = max(maxDuration, task.getDuration());
        }
        return maxDuration;
    }

    public double maxCost() {
        double maxSalary = Double.MIN_VALUE;
        int timeNeeded = 0;
        for (Resource resource : resources) {
            maxSalary = max(maxSalary, resource.getSalary());
        }
        for (Task task : tasks) {
            timeNeeded += task.getDuration();
        }
        return maxSalary * timeNeeded();
    }

    public double minCost() {
        double minSalary = Double.MAX_VALUE;
        for (Resource resource : resources) {
            minSalary = min(minSalary, resource.getSalary());
        }
        return minSalary * timeNeeded();
    }

    public int timeNeeded() {
        int timeNeeded = 0;
        for (Task task : tasks) {
            timeNeeded += task.getDuration();
        }
        return timeNeeded;
    }

    @Override
    public int compareTo(Schedule schedule) {
        return compare(this.fitness, schedule.getFitness());
    }
}
