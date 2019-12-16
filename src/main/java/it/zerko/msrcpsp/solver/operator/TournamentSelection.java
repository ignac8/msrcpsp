package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TournamentSelection extends Operator {

    private int tournamentSize;

    public TournamentSelection(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        return schedules.stream()
                .map(schedule -> select(schedules))
                .collect(Collectors.toList());
    }

    private Schedule select(List<Schedule> schedules) {
        return new Schedule(IntStream.range(0, tournamentSize)
                .map(i -> RandomUtils.nextInt(0, schedules.size()))
                .mapToObj(schedules::get)
                .max(Comparator.naturalOrder())
                .get());
    }
}
