package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;

import java.util.List;

public abstract class Mutation extends Operator {
    protected double chance;

    public Mutation(double chance) {
        this.chance = chance;
    }

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        schedules.forEach(this::mutation);
        return schedules;
    }

    protected abstract void mutation(Schedule schedule);
}
