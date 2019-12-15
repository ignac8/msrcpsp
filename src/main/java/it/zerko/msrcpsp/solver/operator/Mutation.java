package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;

import java.util.List;

public abstract class Mutation extends Operator {
    protected double chance;

    public Mutation(int callCount, double chance) {
        super(callCount);
        this.chance = chance;
    }

    @Override
    public List<Schedule> call(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            mutation(schedule);
        }
        return schedules;
    }

    protected abstract void mutation(Schedule schedule);
}
