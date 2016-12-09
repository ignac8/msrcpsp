package solver.operators;

import problem.Schedule;

import java.util.List;

public abstract class Operator {

    private int callCount;

    public Operator(int callCount) {
        this.callCount = callCount;
    }

    public int getCallCount() {
        return callCount;
    }

    public abstract List<Schedule> call(List<Schedule> schedules);
}
