package solver.operators;

import problem.Schedule;
import problem.Task;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.swap;
import static org.apache.commons.lang3.RandomUtils.nextDouble;

public class OrderMutation extends Mutation {

    public OrderMutation(int callCount, double chance) {
        super(callCount, chance);
    }

    @Override
    protected void mutation(Schedule schedule) {
        List<Task> tasks = schedule.getTasks();
        List<Integer> order = new ArrayList<>();
        for (int counter = 0; counter < tasks.size(); counter++) {
            order.add(counter);
        }
        shuffle(order);
        for (int counter = 0; counter < tasks.size() - 1; counter += 2) {
            if (nextDouble(0, 1) < chance) {
                swap(tasks, order.get(counter), order.get(counter + 1));
            }
        }
    }
}