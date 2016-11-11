package solvers;

import problem.Schedule;
import problem.Task;
import solvers.operators.Operator;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.shuffle;
import static utils.FileUtils.saveGraphToFile;
import static utils.FileUtils.saveScheduleToFile;

public class Solver {

    List<Schedule> schedules;
    List<Operator> operators;
    int passCounter;
    int passLimit;
    long timeStart;
    long timeLimit;
    Schedule bestSchedule;
    List<Result> results;

    public Solver(Schedule defaultSchedule, int populationSize, List<Operator> operators, int passLimit, long timeLimit) {
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
    }

    public Schedule run() {
        initialize();
        while (!done()) {
            for (Operator operator : operators) {
                schedules = operator.run(schedules);
            }
            calculate();
        }
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

    public void graph(String filename) {
        saveGraphToFile(results, filename);
    }

    public void save(String filename) {
        saveScheduleToFile(bestSchedule, filename);
    }
}
