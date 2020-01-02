package it.zerko.msrcpsp.main;

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
    public void saveSolution(Run solver) {
        Files.createDirectories(Paths.get(String.format("%s\\%s", resultsDirectory, solutionsDirectory)));
        Files.write(Paths.get(String.format("%s\\%s\\%s.sol", resultsDirectory, solutionsDirectory, solver)),
                solver.getSolver().getBestSchedule().get().toSolution(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    public void saveGraph(Run solver) {
        Files.createDirectories(Paths.get(String.format("%s\\%s", resultsDirectory, graphsDirectory)));
        ImageIO.write(solver.getSolver().toGraph().createBufferedImage(1200, 600), "png",
                Paths.get(String.format("%s\\%s\\%s.png", resultsDirectory, graphsDirectory, solver)).toFile());
    }

    @SneakyThrows
    public void saveGantt(Run solver) {
        Files.createDirectories(Paths.get(String.format("%s\\%s", resultsDirectory, ganttsDirectory)));
        ImageIO.write(solver.getSolver().getBestSchedule().get().toGraph()
                        .createBufferedImage(2000, 1000), "png",
                Paths.get(String.format("%s\\%s\\%s.png", resultsDirectory, ganttsDirectory, solver)).toFile());
    }
}
