package solvers;

import problem.Schedule;
import problem.Task;
import solvers.operators.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.shuffle;
import static utils.FileUtils.saveGraphToFile;
import static utils.FileUtils.saveScheduleToFile;

public class Solver implements Callable<Schedule> {

    private List<Schedule> schedules;
    private List<Operator> operators;
    private int passCounter;
    private int passLimit;
    private long timeStart;
    private long timeLimit;
    private Schedule bestSchedule;
    private List<Result> results;
    private String filename;

    public Solver(Schedule defaultSchedule, int populationSize, List<Operator> operators, int passLimit, long timeLimit, String filename) {
        schedules = new ArrayList<>(populationSize);
        results = new ArrayList<>();
        for (int counter = 0; counter < populationSize; counter++) {
            Schedule clonedSchedule = new Schedule(defaultSchedule);
            schedules.add(clonedSchedule);
        }
        this.operators = operators;
        passCounter = 0;
        timeStart = currentTimeMillis();
        this.passLimit = passLimit;
        this.timeLimit = timeLimit;
        this.filename = filename;
    }

    public Schedule call() {
        initialize();
        while (!done()) {
            for (Operator operator : operators) {
                schedules = operator.call(schedules);
            }
            calculate();
        }
        graph();
        save();
        return bestSchedule;
    }

    private void initialize() {
        for (Schedule schedule : schedules) {
            List<Task> tasks = schedule.getTasks();
            shuffle(tasks);
            for (Task task : tasks) {
                schedule.assignRandomResourceToTask(task);
            }
        }
        calculate();
    }

    private void calculate() {
        for (Schedule schedule : schedules) {
            schedule.calculate();
            if (bestSchedule == null || schedule.getFitness() < bestSchedule.getFitness()) {
                bestSchedule = new Schedule(schedule);
            }
        }
        results.add(new Result(schedules));
    }

    private boolean done() {
        return currentTimeMillis() - timeStart > timeLimit || passCounter++ > passLimit;
    }

    private void graph() {
        saveGraphToFile(results, filename);
    }

    private void save() {
        saveScheduleToFile(bestSchedule, filename);
    }
}
