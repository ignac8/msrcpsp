package it.zerko.msrcpsp.solver;

import it.zerko.msrcpsp.problem.Schedule;
import lombok.Getter;

import java.util.List;

@Getter
public class Result {
    private double min;
    private double avg;
    private double max;

    public Result(List<Schedule> schedules) {
        min = schedules.stream().mapToDouble(Schedule::getFitness).min().getAsDouble();
        max = schedules.stream().mapToDouble(Schedule::getFitness).max().getAsDouble();
        avg = schedules.stream().mapToDouble(Schedule::getFitness).average().getAsDouble();
    }

}
