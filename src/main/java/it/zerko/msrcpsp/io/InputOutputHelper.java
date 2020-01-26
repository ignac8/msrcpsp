package it.zerko.msrcpsp.io;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;
import it.zerko.msrcpsp.statistic.RunsStatistic;
import lombok.SneakyThrows;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Min;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InputOutputHelper {

    private String datasetsDirectory = "datasets";
    private String resultsDirectory = "results";
    private String solutionsDirectory = "solutions";
    private String graphsDirectory = "graphs";
    private String ganttsDirectory = "gantts";

    @SneakyThrows
    public List<String> getDatasets() {
        Set<String> solvedDatasets = Files.exists(Paths.get(resultsDirectory, "results.txt")) ?
                Files.readAllLines(Paths.get(resultsDirectory, "results.txt"))
                        .stream()
                        .map(line -> line.split(", "))
                        .map(line -> line[1])
                        .map(dataset -> dataset.replace("Dat: ", ""))
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
        return Files.walk(Paths.get(datasetsDirectory))
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(path -> path.endsWith(".def"))
                .map(path -> path.replace(".def", ""))
                .filter(path -> !solvedDatasets.contains(path))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public List<String> readDataset(String filename) {
        return Files.readAllLines(Paths.get(String.format(datasetsDirectory + "/%s.def", filename)));
    }

    @SneakyThrows
    public List<String> readDataset(Path path) {
        return Files.readAllLines(path);
    }

    @SneakyThrows
    public void saveSolution(Solver solver, String filename) {
        Files.createDirectories(Paths.get(String.format("%s/%s", resultsDirectory, solutionsDirectory)));
        Files.write(Paths.get(String.format("%s/%s/%s.sol", resultsDirectory, solutionsDirectory, filename)),
                solver.getBestSchedule().get().toSolution(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    public void saveSolution(Solver solver, Path path) {
        Files.write(path, solver.getBestSchedule().get().toSolution(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    public Path prepareTempFile(String prefix, String suffix) {
        return Files.createTempFile(prefix, suffix);
    }

    @SneakyThrows
    public void saveGraph(Solver solver, String filename) {
        Files.createDirectories(Paths.get(String.format("%s/%s", resultsDirectory, graphsDirectory)));
        ImageIO.write(solver.toGraph().createBufferedImage(1200, 600), "png",
                Paths.get(String.format("%s/%s/%s.png", resultsDirectory, graphsDirectory, filename)).toFile());
    }

    @SneakyThrows
    public void saveGantt(Solver solver, String filename) {
        Files.createDirectories(Paths.get(String.format("%s/%s", resultsDirectory, ganttsDirectory)));
        ImageIO.write(solver.getBestSchedule().get().toGraph()
                        .createBufferedImage(2000, 1000), "png",
                Paths.get(String.format("%s/%s/%s.png", resultsDirectory, ganttsDirectory, filename)).toFile());
    }

    @SneakyThrows
    public void saveStatistics(String algorithm, String dataset, List<Solver> solvers) {
        double[] fitnesses = solvers
                .stream()
                .map(Solver::getBestSchedule)
                .map(Optional::get)
                .map(Schedule::getFitness)
                .mapToDouble(i -> i)
                .toArray();
        Files.createDirectories(Paths.get(String.format("%s", resultsDirectory)));
        String statistics = new RunsStatistic(algorithm, dataset, new Min().evaluate(fitnesses),
                new Mean().evaluate(fitnesses), new StandardDeviation().evaluate(fitnesses)).toString();
        Files.write(Paths.get(String.format("%s/%s", resultsDirectory, "results.txt")), List.of(statistics),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    }

    @SneakyThrows
    public String loadResults(Path path) {
        List<String> markdownTable = new LinkedList<>();
        List<RunsStatistic> runsStatistics = Files.readAllLines(path).stream().map(RunsStatistic::new).collect(Collectors.toList());
        markdownTable.add(String.join("|", List.of("Algorithm", "Dataset", "Minimum", "Average", "Standard deviation")));
        markdownTable.add(IntStream.range(0, 5).mapToObj(i -> "---").collect(Collectors.joining("|")));
        markdownTable.addAll(runsStatistics.stream()
                .collect(Collectors.groupingBy(RunsStatistic::getAlgorithm))
                .entrySet()
                .stream()
                .map(entry -> new RunsStatistic(entry.getKey(), "average",
                        entry.getValue().stream().mapToDouble(RunsStatistic::getMinimum).average().getAsDouble(),
                        entry.getValue().stream().mapToDouble(RunsStatistic::getAverage).average().getAsDouble(),
                        entry.getValue().stream().mapToDouble(RunsStatistic::getStandardDeviation).average().getAsDouble()))
                .map(RunsStatistic::toMarkdownString)
                .collect(Collectors.toList()));
        markdownTable.addAll(runsStatistics.stream()
                .map(RunsStatistic::toMarkdownString)
                .collect(Collectors.toList()));
        return String.join("\n", markdownTable);
    }
}
