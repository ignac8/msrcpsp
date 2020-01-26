package it.zerko.msrcpsp.statistic;

import it.zerko.msrcpsp.problem.Schedule;
import lombok.Getter;

import java.util.List;

@Getter
public class GenerationStatistic {
    private double minimum;
    private double average;
    private double maximum;

    public GenerationStatistic(List<Schedule> schedules) {
        minimum = schedules.stream().mapToDouble(Schedule::getFitness).min().getAsDouble();
        average = schedules.stream().mapToDouble(Schedule::getFitness).average().getAsDouble();
        maximum = schedules.stream().mapToDouble(Schedule::getFitness).max().getAsDouble();
    }

}
