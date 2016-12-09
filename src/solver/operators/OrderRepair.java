package solver.operators;

import problem.Schedule;
import problem.Task;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class OrderRepair extends Operator {

    public OrderRepair(int callCount) {
        super(callCount);
    }

    @Override
    public List<Schedule> call(List<Schedule> schedules) {
        List<Schedule> newSchedules = new ArrayList<>();
        for (Schedule schedule : schedules) {
            List<Task> tasks = schedule.getTasks();
            int counter = 0;
            while (counter < tasks.size()) {
                Task task = tasks.get(counter);
                int maxId = -1;
                for (Task precondition : task.getPreconditions()) {
                    maxId = max(maxId, tasks.indexOf(precondition));
                }
                if (maxId > counter) {
                    tasks.remove(task);
                    tasks.add(maxId, task);
                } else {
                    counter++;
                }
            }
            newSchedules.add(schedule);
        }
        return newSchedules;
    }
}
