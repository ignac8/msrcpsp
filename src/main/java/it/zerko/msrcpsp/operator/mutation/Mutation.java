package it.zerko.msrcpsp.operator.mutation;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

public abstract class Mutation extends Operator {
    protected double chance;

    public Mutation(double chance) {
        this.chance = chance;
    }

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        schedules.stream()
                .filter(schedule -> RandomUtils.nextDouble(0, 1) < chance)
                .forEach(this::mutation);
        return schedules;
    }

    protected abstract void mutation(Schedule schedule);
}
