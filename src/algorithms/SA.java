package algorithms;

import problem.Schedule;
import solver.Solver;
import solver.operators.Operator;
import solver.operators.SimulatedAnnealing;

import java.util.ArrayList;
import java.util.List;

public class SA extends Algorithm {

    public SA() {
        prefix = "SA";
    }

    @Override
    public Solver prepareSolver(Schedule schedule, String filename) {
        List<Operator> preOperators = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        List<Operator> postOperators = new ArrayList<>();
        int populationSize = 1;
        int passLimit = 100000;
        long timeLimit = 1000 * 1000;
        int neighbourSize = 1;
        double decTemp = 0.99975;
        double modifier = 1;
        operators.add(new SimulatedAnnealing(1, neighbourSize, decTemp, modifier, schedule.minTime(), schedule.maxTime(), populationSize));
        return new Solver(schedule, populationSize, preOperators, operators, postOperators, passLimit, timeLimit, filename + "_" + prefix + "_");
    }
}
