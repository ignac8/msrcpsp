package it.zerko.msrcpsp.operator.search;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@AllArgsConstructor
public class LocalSearch extends Operator {

    private int searchSize;

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::search)
                .collect(Collectors.toList());
    }

    private Schedule search(Schedule schedule) {
        return Stream.concat(
                IntStream.range(0, searchSize).mapToObj(i -> schedule.getResourceNeighbour()),
                IntStream.range(0, searchSize).mapToObj(i -> schedule.getOrderNeighbour()))
                .peek(Schedule::calculateFitness)
                .min(Comparator.naturalOrder())
                .get();
    }
}
