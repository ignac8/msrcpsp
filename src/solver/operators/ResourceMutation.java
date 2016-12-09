package solver.operators;

import problem.Schedule;
import problem.Task;

import java.util.List;

import static org.apache.commons.lang3.RandomUtils.nextDouble;

public class ResourceMutation extends Mutation {

    public ResourceMutation(int callCount, double chance) {
        super(callCount, chance);
    }

    @Override
    protected void mutation(Schedule schedule) {
        List<Task> tasks = schedule.getTasks();
        for (Task task : tasks) {
            if (nextDouble(0, 1) < chance) {
                schedule.assignRandomResourceToTask(task);
            }
        }
    }
}