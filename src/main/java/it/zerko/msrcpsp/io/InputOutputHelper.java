package it.zerko.msrcpsp.io;

import it.zerko.msrcpsp.solver.Solver;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class InputOutputHelper {

    private String datasetsDirectory = "datasets";
    private String resultsDirectory = "results";
    private String solutionsDirectory = "solutions";
    private String graphsDirectory = "graphs";
    private String ganttsDirectory = "gantts";

    @SneakyThrows
    public List<String> readDataset(String filename) {
        return Files.readAllLines(Paths.get(String.format(datasetsDirectory + "\\%s.def", filename)));
    }

    @SneakyThrows
    public void saveSolution(Solver solver, String filename) {
        Files.createDirectories(Paths.get(String.format("%s\\%s", resultsDirectory, solutionsDirectory)));
        Files.write(Paths.get(String.format("%s\\%s\\%s.sol", resultsDirectory, solutionsDirectory, filename)),
                solver.getBestSchedule().get().toSolution(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    public void saveGraph(Solver solver, String filename) {
        Files.createDirectories(Paths.get(String.format("%s\\%s", resultsDirectory, graphsDirectory)));
        ImageIO.write(solver.toGraph().createBufferedImage(1200, 600), "png",
                Paths.get(String.format("%s\\%s\\%s.png", resultsDirectory, graphsDirectory, filename)).toFile());
    }

    @SneakyThrows
    public void saveGantt(Solver solver, String filename) {
        Files.createDirectories(Paths.get(String.format("%s\\%s", resultsDirectory, ganttsDirectory)));
        ImageIO.write(solver.getBestSchedule().get().toGraph()
                        .createBufferedImage(2000, 1000), "png",
                Paths.get(String.format("%s\\%s\\%s.png", resultsDirectory, ganttsDirectory, filename)).toFile());
    }
}
