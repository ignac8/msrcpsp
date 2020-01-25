package it.zerko.msrcpsp.initializer;

import it.zerko.msrcpsp.problem.Schedule;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Initializer {

    public List<Schedule> initialize(List<String> dataset, int count) {
        List<Schedule> schedules = IntStream.range(0, count)
                .mapToObj(i -> new Schedule(dataset))
                .peek(Schedule::assignRandomResourcesToTasks)
                .peek(Schedule::calculateFitness)
                .sorted()
                .collect(Collectors.toList());
        Collections.shuffle(schedules);
        return schedules;
    }
}
