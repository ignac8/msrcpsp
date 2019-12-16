package it.zerko.msrcpsp.solver;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.operator.Operator;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class Solver implements Runnable {

    private List<Schedule> schedules;
    private List<Operator> operators;
    private int passCounter;
    private int passLimit;
    private LocalDateTime timeStart;
    private Duration timeLimit;
    private Optional<Schedule> bestSchedule;
    private List<Result> results;

    public Solver(Schedule defaultSchedule, int populationSize, List<Operator> operators, int passLimit, Duration timeLimit) {
        this.schedules = IntStream.range(0, populationSize).mapToObj(i -> defaultSchedule).collect(Collectors.toList());
        this.operators = operators;
        this.passCounter = 0;
        this.passLimit = passLimit;
        this.timeStart = LocalDateTime.now();
        this.timeLimit = timeLimit;
        this.bestSchedule = Optional.empty();
        this.results = new LinkedList<>();
    }

    public void run() {
        schedules.forEach(schedule -> Collections.shuffle(schedule.getTasks()));
        schedules.forEach(Schedule::assignRandomResourcesToTasks);
        calculateFitness();
        while (!(Duration.between(timeStart, LocalDateTime.now()).compareTo(timeLimit) > 0 || passCounter++ > passLimit)) {
            operators.forEach(operator -> schedules = operator.modify(schedules));
            calculateFitness();
        }
    }

    private void calculateFitness() {
        schedules.forEach(Schedule::calculateFitness);
        Schedule schedule = schedules.stream().sorted().findFirst().get();
        if (bestSchedule.isEmpty() || schedule.getFitness() < bestSchedule.get().getFitness()) {
            bestSchedule = Optional.of(new Schedule(schedule));
        }
        results.add(new Result(schedules));
    }

}
