package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

public class ResourceMutation extends Mutation {

    public ResourceMutation(double chance) {
        super(chance);
    }

    @Override
    protected void mutation(Schedule schedule) {
        schedule.getTasks()
                .stream()
                .filter(task -> RandomUtils.nextDouble(0, 1) < chance)
                .forEach(schedule::assignRandomResourceToTask);
    }
}