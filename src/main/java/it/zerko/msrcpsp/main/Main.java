package it.zerko.msrcpsp.main;

import it.zerko.msrcpsp.algorithm.Algorithm;
import it.zerko.msrcpsp.algorithm.GeneticAlgorithm;
import it.zerko.msrcpsp.algorithm.GreedyBuilderAlgorithm;
import it.zerko.msrcpsp.algorithm.LocalSearchAlgorithm;
import it.zerko.msrcpsp.algorithm.SimulatedAnnealingAlgorithm;
import it.zerko.msrcpsp.io.InputOutputHelper;
import it.zerko.msrcpsp.solver.Solver;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        int geneticPopulationSize = 100;
        int geneticPassLimit = 10000;
        int tournamentSize = 5;
        double crossoverChance = 1;
        double mutationChance = 0.5;
        int localSearchPassLimit = 100000;
        int simulatedAnnealingPassLimit = 1000000;
        double maxTemp = 250000;
        int greedyBuilderPopulationSize = 100;
        int solverCount = 10;
        List<Algorithm> algorithms = List.of(
                new GeneticAlgorithm(geneticPopulationSize, geneticPassLimit, tournamentSize, crossoverChance,
                        mutationChance),
                new SimulatedAnnealingAlgorithm(simulatedAnnealingPassLimit, maxTemp),
                new LocalSearchAlgorithm(localSearchPassLimit),
                new GreedyBuilderAlgorithm(greedyBuilderPopulationSize));
        InputOutputHelper inputOutputHelper = new InputOutputHelper();
        LocalDateTime timeStart = LocalDateTime.now();
        List<String> datasets = inputOutputHelper.getDatasets();
        Collections.shuffle(datasets);
        datasets.forEach(dataset -> runForDataset(dataset, algorithms, inputOutputHelper, solverCount,
                timeStart));
        System.out.println(Duration.between(timeStart, LocalDateTime.now()));
    }

    public void runForDataset(String dataset, List<Algorithm> algorithms, InputOutputHelper inputOutputHelper,
                              int solverCount, LocalDateTime timeStart) {
        AtomicInteger counter = new AtomicInteger();
        List<Run> runs = algorithms.stream()
                .map(algorithm -> multiply(algorithm, solverCount))
                .flatMap(Collection::stream)
                .map(algorithm -> new Run(counter, algorithm, dataset, inputOutputHelper, timeStart))
                .collect(Collectors.toList());
        Collections.shuffle(runs);
        runs.parallelStream()
                .peek(run -> run.getSolver().solve())
                .peek(run -> inputOutputHelper.saveSolution(run.getSolver(), run.toString()))
                .peek(run -> inputOutputHelper.saveGraph(run.getSolver(), run.toString()))
                .peek(run -> inputOutputHelper.saveGantt(run.getSolver(), run.toString()))
                .collect(Collectors.groupingBy(Run::getAlgorithm))
                .forEach((algorithm, runsPerAlgorithm) -> inputOutputHelper.saveStatistics(
                        algorithm.getClass().getSimpleName(), dataset,
                        runsPerAlgorithm.stream().map(Run::getSolver).collect(Collectors.toList()))
                );
    }

    private List<Algorithm> multiply(Algorithm algorithm, int solverCount) {
        return IntStream.range(0, solverCount)
                .mapToObj(i -> algorithm)
                .collect(Collectors.toList());
    }

    @Getter
    public static class Run {
        private int counter;
        private Algorithm algorithm;
        private String dataset;
        private Solver solver;
        private LocalDateTime timeStart;

        public Run(AtomicInteger counter, Algorithm algorithm, String dataset, InputOutputHelper inputOutputHelper,
                   LocalDateTime timeStart) {
            this.counter = counter.getAndIncrement();
            this.algorithm = algorithm;
            this.dataset = dataset;
            this.solver = algorithm.prepareSolver(inputOutputHelper.readDataset(dataset));
            this.timeStart = timeStart;
        }

        @Override
        public String toString() {
            return String.format("%s_%s_%s_%d", timeStart.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")),
                    algorithm.getClass().getSimpleName(), dataset, counter);
        }
    }
}