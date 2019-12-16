package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;

import java.util.List;

public abstract class Operator {

    public abstract List<Schedule> modify(List<Schedule> schedules);
}
