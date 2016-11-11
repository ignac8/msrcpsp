package solvers.operators;

import problem.Schedule;

import java.util.List;

public abstract class Mutation implements Operator {
    protected double chance;

    public Mutation(double chance) {
        this.chance = chance;
    }

    @Override
    public List<Schedule> run(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            mutation(schedule);
        }
        return schedules;
    }

    protected abstract void mutation(Schedule schedule);
}
