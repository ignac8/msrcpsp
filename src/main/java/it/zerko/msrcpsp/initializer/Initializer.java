package it.zerko.msrcpsp.initializer;

import it.zerko.msrcpsp.problem.Schedule;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Initializer {

    public List<Schedule> initialize(List<String> lines, int count, int multiplier) {
        List<Schedule> schedules = IntStream.range(0, count * multiplier)
                .mapToObj(i -> new Schedule(lines))
                .peek(Schedule::assignRandomResourcesToTasks)
                .peek(Schedule::calculateFitness)
                .sorted()
                .limit(count)
                .collect(Collectors.toList());
        Collections.shuffle(schedules);
        return schedules;
    }
}
