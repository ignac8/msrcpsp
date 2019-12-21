package it.zerko.msrcpsp.solver;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.operator.Operator;
import lombok.Getter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
    private Duration timeTaken;
    private Optional<Schedule> bestSchedule;
    private List<Result> results;

    public Solver(Schedule defaultSchedule, int populationSize, List<Operator> operators, int passLimit, Duration timeLimit) {
        this.schedules = IntStream.range(0, populationSize).mapToObj(i -> defaultSchedule).collect(Collectors.toList());
        this.operators = operators;
        this.passCounter = 0;
        this.passLimit = passLimit;
        this.timeStart = LocalDateTime.now();
        this.timeLimit = timeLimit;
        this.timeTaken = Duration.ZERO;
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
        timeTaken = Duration.between(timeStart, LocalDateTime.now());
    }

    private void calculateFitness() {
        schedules.forEach(Schedule::calculateFitness);
        Schedule schedule = schedules.stream().sorted().findFirst().get();
        if (bestSchedule.isEmpty() || schedule.getFitness() < bestSchedule.get().getFitness()) {
            bestSchedule = Optional.of(new Schedule(schedule));
        }
        results.add(new Result(schedules));
    }

    public JFreeChart toGraph() {
        XYSeries popMin = new XYSeries("Min");
        XYSeries popAvg = new XYSeries("Avg");
        XYSeries popMax = new XYSeries("Max");
        IntStream.range(0, results.size()).forEach(counter -> popMin.add(counter, results.get(counter).getMin()));
        IntStream.range(0, results.size()).forEach(counter -> popAvg.add(counter, results.get(counter).getAvg()));
        IntStream.range(0, results.size()).forEach(counter -> popMax.add(counter, results.get(counter).getMax()));
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(popMin);
        dataset.addSeries(popAvg);
        dataset.addSeries(popMax);
        return ChartFactory.createXYLineChart(
                "Harmonogramowanie",
                "Pokolenie",
                "Ocena",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
    }

}
