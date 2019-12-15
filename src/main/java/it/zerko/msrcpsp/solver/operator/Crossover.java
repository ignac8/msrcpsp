package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;

import java.util.List;

import static org.apache.commons.lang3.RandomUtils.nextDouble;

public abstract class Crossover extends Operator {
    private double chance;

    public Crossover(int callCount, double chance) {
        super(callCount);
        this.chance = chance;
    }

    @Override
    public List<Schedule> call(List<Schedule> schedules) {
        for (int counter = 0; counter < schedules.size() - 1; counter += 2) {
            Schedule firstSchedule = schedules.get(counter);
            Schedule secondSchedule = schedules.get(counter + 1);
            if (nextDouble(0, 1) < chance) {
                crossover(firstSchedule, secondSchedule);
            }
        }
        return schedules;
    }

    protected abstract void crossover(Schedule firstSchedule, Schedule secondSchedule);
}
