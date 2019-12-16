package it.zerko.msrcpsp;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;
import it.zerko.msrcpsp.solver.operator.Operator;
import it.zerko.msrcpsp.solver.operator.OrderCrossover;
import it.zerko.msrcpsp.solver.operator.OrderMutation;
import it.zerko.msrcpsp.solver.operator.ResourceCrossover;
import it.zerko.msrcpsp.solver.operator.ResourceMutation;
import it.zerko.msrcpsp.solver.operator.TournamentSelection;
import it.zerko.msrcpsp.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        Schedule defaultSchedule = new Schedule(Files.readAllLines(Paths.get("datasets/10_3_5_3.def")));
        Solver solver = new Solver(defaultSchedule, populationSize, operators, passLimit, Duration.ofMinutes(10));
        solver.run();
        FileUtils fileUtils = FileUtils.getInstance();
        fileUtils.saveScheduleToFile(solver.getBestSchedule().get(), "asd.txt");
        fileUtils.saveGraphToFile(solver.getResults(), "asd.png");
        int debug = 1;
    }
}