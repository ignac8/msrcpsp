package it.zerko.msrcpsp;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;
import it.zerko.msrcpsp.solver.operator.Operator;
import it.zerko.msrcpsp.solver.operator.OrderCrossover;
import it.zerko.msrcpsp.solver.operator.OrderMutation;
import it.zerko.msrcpsp.solver.operator.ResourceCrossover;
import it.zerko.msrcpsp.solver.operator.ResourceMutation;
import it.zerko.msrcpsp.solver.operator.TournamentSelection;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String... args) throws IOException {
        int populationSize = 100;
        int passLimit = 1000;
        List<Operator> operators = List.of(
                new TournamentSelection(10),
                new OrderCrossover(0.9),
                new ResourceCrossover(0.9),
                new OrderMutation(0.01),
                new ResourceMutation(0.01));
        Schedule defaultSchedule = new Schedule(Files.readAllLines(Paths.get("datasets/100_5_20_9_D3.def")));
        Solver solver = new Solver(defaultSchedule, populationSize, operators, passLimit, Duration.ofMinutes(1));
        solver.run();
        Files.write(Paths.get("test.sol"),
                solver.getBestSchedule().get().toSolution(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
        ImageIO.write(solver.toGraph().createBufferedImage(1200, 600),
                "png",
                Paths.get("test.png").toFile());
        int debug = 1;

        List<String> scheduleInText = Files.readAllLines(Paths.get("..\\datasets\\100_5_20_9_D3.def"));
        String asd = String.join("\n", scheduleInText);

    }
}