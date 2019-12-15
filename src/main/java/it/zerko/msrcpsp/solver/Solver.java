package it.zerko.msrcpsp.solver;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;
import it.zerko.msrcpsp.solver.operator.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static it.zerko.msrcpsp.util.FileUtils.saveGraphToFile;
import static it.zerko.msrcpsp.util.FileUtils.saveScheduleToFile;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.shuffle;

public class Solver implements Callable<Schedule> {

    private List<Schedule> schedules;
    private List<Operator> preOperators;
    private List<Operator> operators;
    private List<Operator> postOperators;
    private int passCounter;
    private int passLimit;
    private long timeStart;
    private long timeLimit;
    private Schedule bestSchedule;
    private List<Result> results;
    private String filename;

    public Solver(Schedule defaultSchedule, int populationSize, List<Operator> preOperators, List<Operator> operators, List<Operator> postOperators, int passLimit, long timeLimit, String filename) {
        schedules = new ArrayList<>(populationSize);
        results = new ArrayList<>();
        for (int counter = 0; counter < populationSize; counter++) {
            Schedule clonedSchedule = new Schedule(defaultSchedule);
            schedules.add(clonedSchedule);
        }
        this.preOperators = preOperators;
        this.operators = operators;
        this.postOperators = postOperators;
        passCounter = 0;
        timeStart = currentTimeMillis();
        this.passLimit = passLimit;
        this.timeLimit = timeLimit;
        this.filename = filename;
    }

    public Schedule call() {
        initialize();
        for (Operator operator : preOperators) {
            for (int counter = 0; counter < operator.getCallCount(); counter++) {
                schedules = operator.call(schedules);
                calculate();
            }
        }
        while (!done()) {
            for (Operator operator : operators) {
                for (int counter = 0; counter < operator.getCallCount(); counter++) {
                    schedules = operator.call(schedules);
                }
            }
            calculate();
        }
        for (Operator operator : postOperators) {
            for (int counter = 0; counter < operator.getCallCount(); counter++) {
                schedules = operator.call(schedules);
                calculate();
            }
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
