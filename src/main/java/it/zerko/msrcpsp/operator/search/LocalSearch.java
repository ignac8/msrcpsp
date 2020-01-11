package it.zerko.msrcpsp.operator.search;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalSearch extends Operator {

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::search)
                .collect(Collectors.toList());
    }

    private Schedule search(Schedule schedule) {
        Schedule neighbour = RandomUtils.nextBoolean() ? schedule.getResourceNeighbour() : schedule.getOrderNeighbour();
        neighbour.calculateFitness();
        return Stream.of(schedule, neighbour).min(Comparator.naturalOrder()).get();
    }
}
