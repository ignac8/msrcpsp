package it.zerko.msrcpsp.main;

import it.zerko.msrcpsp.algorithm.Algorithm;
import it.zerko.msrcpsp.algorithm.GeneticAlgorithm;
import it.zerko.msrcpsp.algorithm.GreedyBuilderAlgorithm;
import it.zerko.msrcpsp.algorithm.LocalSearchAlgorithm;
import it.zerko.msrcpsp.algorithm.SimulatedAnnealingAlgorithm;
import it.zerko.msrcpsp.io.InputOutputHelper;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Min;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        new Main().main();
    }

    public void main() {
        int geneticPopulationSize = 100;
        int geneticPassLimit = 1000;
        int tournamentSize = 5;
        double crossoverChance = 1;
        double mutationChance = 1;

        int localSearchPopulationSize = 100;
        int localSearchPassLimit = 1000;
        int localSearchSearchSize = 1;

        int simulatedAnnealingPopulationSize = 100;
        int simulatedAnnealingPassLimit = 1000;
        int simulatedAnnealingSearchSize = 1;
        double maxTemp = 500;

        int greedyBuilderPopulationSize = 10000;

        int solverCount = 1;
        List<String> datasets = List.of(
                "100_5_20_9_D3",
                "200_40_133_15"
        );
        List<Algorithm> algorithms = List.of(
                new GeneticAlgorithm(geneticPopulationSize, geneticPassLimit, tournamentSize,
                        crossoverChance, mutationChance),
                new LocalSearchAlgorithm(localSearchPopulationSize, localSearchPassLimit, localSearchSearchSize),
                new SimulatedAnnealingAlgorithm(simulatedAnnealingPopulationSize, simulatedAnnealingPassLimit,
                        maxTemp, simulatedAnnealingSearchSize),
                new GreedyBuilderAlgorithm(greedyBuilderPopulationSize)
        );
        InputOutputHelper inputOutputHelper = new InputOutputHelper();
        LocalDateTime timeStart = LocalDateTime.now();
        AtomicInteger counter = new AtomicInteger();
        List<Run> runs = algorithms.stream()
                .map(algorithm -> prepareCategories(algorithm, datasets))
                .flatMap(Collection::stream)
                .map(category -> multiplyCategories(category, solverCount))
                .flatMap(Collection::stream)
                .map(category -> new Run(counter.getAndIncrement(), category, category.getAlgorithm()
                        .prepareSolver(inputOutputHelper.readDataset(category.getDataset())), timeStart))
                .collect(Collectors.toList());
        runs.parallelStream()
                .peek(run -> run.getSolver().solve())
                .peek(run -> inputOutputHelper.saveSolution(run.getSolver(), run.toString()))
                .peek(run -> inputOutputHelper.saveGraph(run.getSolver(), run.toString()))
                .peek(run -> inputOutputHelper.saveGantt(run.getSolver(), run.toString()))
                .collect(Collectors.groupingBy(Run::getCategory))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getAlgorithm() + entry.getKey().getDataset()))
                .forEach(this::printStatistics);
        System.out.println(Duration.between(timeStart, LocalDateTime.now()));
    }

    private void printStatistics(Map.Entry<Category, List<Run>> entry) {
        Category category = entry.getKey();
        List<Run> runs = entry.getValue();
        double[] fitnesses = runs
                .stream()
                .map(Run::getSolver)
                .map(Solver::getBestSchedule)
                .map(Optional::get)
                .map(Schedule::getFitness)
                .mapToDouble(i -> i)
                .toArray();
        System.out.println(String.format("Alg: %s\nDat: %s\nMin: %s\nAvg: %s\nStd: %s\n",
                category.getAlgorithm().getClass().getSimpleName(), category.getDataset(),
                new Min().evaluate(fitnesses), new Mean().evaluate(fitnesses), new StandardDeviation().evaluate(fitnesses)));
    }

    private List<Category> multiplyCategories(Category category, int solverCount) {
        return IntStream.range(0, solverCount)
                .mapToObj(i -> category)
                .collect(Collectors.toList());
    }

    private List<Category> prepareCategories(Algorithm algorithm, List<String> datasets) {
        return datasets.stream()
                .map(dataset -> new Category(algorithm, dataset))
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class Category {
        private Algorithm algorithm;
        private String dataset;
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class Run {
        private int counter;
        private Category category;
        private Solver solver;
        private LocalDateTime timeStart;

        @Override
        public String toString() {
            return String.format("%s_%s_%s_%d", timeStart.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")),
                    category.getAlgorithm().getClass().getSimpleName(), category.getDataset(), counter);
        }
    }


}