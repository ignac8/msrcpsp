package solvers.operators;

import problem.Schedule;

import java.util.List;

public interface Operator {
    List<Schedule> call(List<Schedule> schedules);
}
