package it.zerko.msrcpsp.main;

import it.zerko.msrcpsp.algorithm.Algorithm;
import it.zerko.msrcpsp.algorithm.GeneticAlgorithm;
import it.zerko.msrcpsp.algorithm.LocalSearchAlgorithm;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Min;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
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
        int solverCount = 10;
        int populationSize = 100;
        int passLimit = 1000;
        int initializerMultiplier = 1;
        int tournamentSize = 5;
        double crossoverChance = 1;
        double mutationChance = 1;
        int searchSize = 1;
        List<String> datasets = List.of(
                "100_5_20_9_D3",
                "200_40_133_15"
        );
        List<Algorithm> algorithms = List.of(
                new GeneticAlgorithm(tournamentSize, crossoverChance, mutationChance),
                new LocalSearchAlgorithm(searchSize)
        );
        InputOutputHelper inputOutputHelper = new InputOutputHelper();
        LocalDateTime timeStart = LocalDateTime.now();
        AtomicInteger counter = new AtomicInteger();
        List<Run> runs = algorithms.stream()
                .map(algorithm -> prepareCategories(algorithm, datasets))
                .flatMap(Collection::stream)
                .map(category -> multiplyCategories(category, solverCount))
                .flatMap(Collection::stream)
                .map(category -> prepareRun(inputOutputHelper, populationSize, passLimit, initializerMultiplier,
                        category, counter, timeStart))
                .collect(Collectors.toList());
        runs.parallelStream()
                .peek(run -> run.getSolver().solve())
                .peek(inputOutputHelper::saveSolution)
                .peek(inputOutputHelper::saveGraph)
                .peek(inputOutputHelper::saveGantt)
                .collect(Collectors.groupingBy(Run::getCategory))
                .entrySet()
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

    private Run prepareRun(InputOutputHelper inputOutputHelper, int populationSize, int passLimit,
                           int initializerMultiplier, Category category,
                           AtomicInteger runCounter, LocalDateTime timeStart) {
        return new Run(runCounter.getAndIncrement(), category, category.getAlgorithm().prepareSolver(
                inputOutputHelper.readDataset(category.getDataset()), populationSize, initializerMultiplier,
                passLimit), timeStart);
    }

}