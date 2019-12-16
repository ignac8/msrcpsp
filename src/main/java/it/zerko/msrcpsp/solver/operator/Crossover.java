package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.stream.IntStream;

public abstract class Crossover extends Operator {
    private double chance;

    public Crossover(double chance) {
        this.chance = chance;
    }

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        IntStream.iterate(0, counter -> counter < schedules.size() - 1, counter -> counter + 2)
                .filter(counter -> RandomUtils.nextDouble(0, 1) < chance)
                .forEach(counter -> crossover(schedules.get(counter), schedules.get(counter + 1)));
        return schedules;
    }

    protected abstract void crossover(Schedule firstSchedule, Schedule secondSchedule);
}
