package it.zerko.msrcpsp;

import io.DefinitionReader;
import io.SolutionReader;
import it.zerko.msrcpsp.algorithm.Algorithm;
import it.zerko.msrcpsp.algorithm.GeneticAlgorithm;
import it.zerko.msrcpsp.algorithm.GreedyBuilderAlgorithm;
import it.zerko.msrcpsp.algorithm.LocalSearchAlgorithm;
import it.zerko.msrcpsp.algorithm.SimulatedAnnealingAlgorithm;
import it.zerko.msrcpsp.io.InputOutputHelper;
import it.zerko.msrcpsp.solver.Solver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import validators.CompleteValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExternalScheduleValidatorTests {

    private Path definitionFile;
    private Path solutionFile;
    private List<String> dataset;
    private InputOutputHelper inputOutputHelper;

    @BeforeClass
    public void loadFiles() {
        inputOutputHelper = new InputOutputHelper();
        definitionFile = Paths.get("datasets\\100_5_20_9_D3.def");
        solutionFile = inputOutputHelper.prepareTempFile("externalScheduleValidatorTest", ".sol");
        dataset = inputOutputHelper.readDataset(definitionFile);
    }

    @Test(dataProvider = "dataProvider")
    public void externalScheduleValidatorTest(Algorithm algorithm) throws Exception {
        Solver solver = algorithm.prepareSolver(dataset);
        solver.solve();
        inputOutputHelper.saveSolution(solver, solutionFile);
        model.Schedule schedule = DefinitionReader.read(definitionFile.toString());
        SolutionReader.read(schedule, solutionFile.toString());
        String result = new CompleteValidator().validate(schedule);
        Assert.assertEquals(result, "OK OK OK OK ");
    }

    @DataProvider
    public static Object[][] dataProvider() {
        return new Object[][]{
                {new GeneticAlgorithm(10, 100, 5, 0.5, 0.5)},
                {new SimulatedAnnealingAlgorithm(1000, 100)},
                {new GreedyBuilderAlgorithm(10)},
                {new LocalSearchAlgorithm(1000)}
        };
    }
}