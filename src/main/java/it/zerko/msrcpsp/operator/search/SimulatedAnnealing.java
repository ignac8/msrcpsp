package it.zerko.msrcpsp.operator.search;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SimulatedAnnealing extends Operator {

    private int passCounter;
    private double maxTemp;

    public SimulatedAnnealing(double maxTemp) {
        this.passCounter = 0;
        this.maxTemp = maxTemp;
    }

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        double currentTemp = maxTemp / ++passCounter;
        return schedules.stream()
                .map(schedule -> search(schedule, currentTemp))
                .collect(Collectors.toList());
    }

    private Schedule search(Schedule schedule, double currentTemp) {
        Schedule neighbour = RandomUtils.nextBoolean() ? schedule.getResourceNeighbour() : schedule.getOrderNeighbour();
        neighbour.calculateFitness();
        return neighbour.getFitness() < schedule.getFitness() ||
                RandomUtils.nextDouble(0, 1) < Math.exp(-(neighbour.getFitness() - schedule.getFitness()) / currentTemp)
                ? neighbour : schedule;
    }
}
