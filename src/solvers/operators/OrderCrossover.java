package solvers.operators;

import problem.Schedule;
import problem.Task;

import java.util.List;

import static java.lang.Math.min;
import static java.util.Collections.swap;
import static org.apache.commons.lang3.RandomUtils.nextInt;

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
        int randomInt = nextInt(0, min(firstTasks.size(), secondTasks.size()));
        temp(firstTasks, secondTasksCopy, randomInt);
        temp(secondTasks, firstTasksCopy, randomInt);
    }

    private void temp(List<Task> tasks, List<Task> tasksCopy, int randomInt) {
        for (int outerCounter = 0; outerCounter < randomInt; outerCounter++) {
            if (tasks.get(outerCounter).getTaskId() != tasksCopy.get(outerCounter).getTaskId()) {
                int foundPos = -1;
                for (int innerCounter = 0; outerCounter < tasks.size() && foundPos == -1; innerCounter++) {
                    if (tasks.get(innerCounter).getTaskId() == tasksCopy.get(outerCounter).getTaskId()) {
                        foundPos = innerCounter;
                    }
                }
                swap(tasks, outerCounter, foundPos);
            }
        }
    }
}
