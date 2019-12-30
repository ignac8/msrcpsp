package it.zerko.msrcpsp;

import io.DefinitionReader;
import io.SolutionReader;
import it.zerko.msrcpsp.initializer.Initializer;
import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.crossover.OrderCrossover;
import it.zerko.msrcpsp.operator.crossover.ResourceCrossover;
import it.zerko.msrcpsp.operator.mutation.OrderMutation;
import it.zerko.msrcpsp.operator.mutation.ResourceMutation;
import it.zerko.msrcpsp.operator.selection.TournamentSelection;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;
import org.testng.Assert;
import org.testng.annotations.Test;
import validators.CompleteValidator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;

public class ExternalScheduleValidatorTests {

    @Test
    public void externalScheduleValidatorTest() throws Exception {
        Path definition = Paths.get("datasets\\200_40_133_15.def");
        Path solution = Files.createTempFile("externalScheduleValidatorTest", ".sol");
        int populationSize = 10;
        int passLimit = 100;
        int initializerMultiplier = 1;
        int tournamentSize = 10;
        double crossoverChance = 1;
        double mutationChance = 0.005;
        List<Operator> operators = List.of(
                new TournamentSelection(tournamentSize),
                new OrderCrossover(crossoverChance),
                new ResourceCrossover(crossoverChance),
                new OrderMutation(mutationChance),
                new ResourceMutation(mutationChance));
        List<String> lines = Files.readAllLines(definition);
        List<Schedule> schedules = new Initializer().initialize(lines, populationSize, initializerMultiplier);
        Solver solver = new Solver(schedules, operators, passLimit, Duration.ofHours(1));
        solver.solve();
        Files.write(solution,
                solver.getBestSchedule().get().toSolution(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        model.Schedule schedule = DefinitionReader.read(definition.toString());
        SolutionReader.read(schedule, solution.toString());
        String result = new CompleteValidator().validate(schedule);
        Assert.assertEquals(result, "OK OK OK OK ");
    }
}