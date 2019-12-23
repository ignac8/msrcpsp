package it.zerko.msrcpsp.operator.mutation;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;
import org.apache.commons.lang3.RandomUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderMutation extends Mutation {

    public OrderMutation(double chance) {
        super(chance);
    }

    @Override
    protected void mutation(Schedule schedule) {
        List<Task> tasks = schedule.getTasks();
        List<Integer> order = IntStream.range(0, tasks.size()).boxed().collect(Collectors.toList());
        Collections.shuffle(order);
        IntStream.iterate(0, counter -> counter < tasks.size() - 1, counter -> counter + 2)
                .filter(counter -> RandomUtils.nextDouble(0, 1) < chance)
                .forEach(counter -> Collections.swap(tasks, order.get(counter), order.get(counter + 1)));
    }
}