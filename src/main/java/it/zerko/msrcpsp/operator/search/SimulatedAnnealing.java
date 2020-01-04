package it.zerko.msrcpsp.operator.search;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimulatedAnnealing extends Operator {

    private double maxTemp;
    private double currentTemp;
    private int passLimit;
    private int searchSize;

    public SimulatedAnnealing(double maxTemp, int passLimit, int searchSize) {
        this.maxTemp = maxTemp;
        this.currentTemp = maxTemp;
        this.passLimit = passLimit;
        this.searchSize = searchSize;
    }

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        currentTemp -= maxTemp / passLimit;
        return schedules.stream()
                .map(this::search)
                .collect(Collectors.toList());
    }

    private Schedule search(Schedule schedule) {
        return Stream.concat(
                IntStream.range(0, searchSize).mapToObj(i -> schedule.getResourceNeighbour()),
                IntStream.range(0, searchSize).mapToObj(i -> schedule.getOrderNeighbour()))
                .peek(Schedule::calculateFitness)
                .map(neighbour -> Map.entry(neighbour,
                        neighbour.getFitness() + RandomUtils.nextDouble(0, Math.max(currentTemp, Math.ulp(0)))))
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                .get()
                .getKey();
    }
}
