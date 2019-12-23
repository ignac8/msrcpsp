package it.zerko.msrcpsp;

import it.zerko.msrcpsp.initializer.Initializer;
import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.crossover.OrderCrossover;
import it.zerko.msrcpsp.operator.crossover.ResourceCrossover;
import it.zerko.msrcpsp.operator.mutation.OrderMutation;
import it.zerko.msrcpsp.operator.mutation.ResourceMutation;
import it.zerko.msrcpsp.operator.selection.TournamentSelection;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {
        new Main().main();
    }

    public void main() throws IOException {
        int solverCount = 100;
        int populationSize = 100;
        int passLimit = 1000;
        int initializerMultiplier = 1;
        int tournamentSize = 10;
        double crossoverChance = 1;
        double mutationChance = 0.02;
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        Files.createDirectories(Paths.get("results\\gantts"));
        Files.createDirectories(Paths.get("results\\graphs"));
        Files.createDirectories(Paths.get("results\\solutions"));
        LocalDateTime timeStart = LocalDateTime.now();
        List<Solver> solvers = IntStream.range(0, solverCount)
                .parallel()
                .mapToObj(i -> Map.entry(i, prepareSolver(populationSize, passLimit, initializerMultiplier,
                        tournamentSize, crossoverChance, mutationChance)))
                .peek(entry -> entry.getValue().solve())
                .peek(entry -> saveSolution(entry.getKey(), entry.getValue(), date))
                .peek(entry -> saveGraph(entry.getKey(), entry.getValue(), date))
                .peek(entry -> saveGantt(entry.getKey(), entry.getValue(), date))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        System.out.println(Duration.between(timeStart, LocalDateTime.now()));
    }

    @SneakyThrows
    private void saveSolution(int counter, Solver solver, String date) {
        Files.write(Paths.get(String.format("results\\solutions\\%s_%d.sol", date, counter)),
                solver.getBestSchedule().get().toSolution(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    private void saveGraph(int counter, Solver solver, String date) {
        ImageIO.write(solver.toGraph().createBufferedImage(1200, 600), "png",
                Paths.get(String.format("results\\graphs\\%s_%d.png", date, counter)).toFile());
    }

    @SneakyThrows
    private void saveGantt(int counter, Solver solver, String date) {
        ImageIO.write(solver.getBestSchedule().get().toGraph().createBufferedImage(2000, 1000), "png",
                Paths.get(String.format("results\\gantts\\%s_%d.png", date, counter)).toFile());
    }

    @SneakyThrows
    private Solver getFuture(Future<Solver> solverFuture) {
        return solverFuture.get();
    }

    @SneakyThrows
    private Solver prepareSolver(int populationSize, int passLimit, int initializerMultiplier,
                                 int tournamentSize, double crossoverChance, double mutationChance) {
        List<Operator> operators = List.of(
                new TournamentSelection(tournamentSize),
                new OrderCrossover(crossoverChance),
                new ResourceCrossover(crossoverChance),
                new OrderMutation(mutationChance),
                new ResourceMutation(mutationChance));
        List<String> lines = Files.readAllLines(Paths.get("datasets/100_5_20_9_D3.def"));
        List<Schedule> schedules = new Initializer().initialize(lines, populationSize, initializerMultiplier);
        return new Solver(schedules, operators, passLimit, Duration.ofMinutes(1));
    }

}