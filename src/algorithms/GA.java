package algorithms;

import problem.Schedule;
import solver.Solver;
import solver.operators.Operator;
import solver.operators.OrderCrossover;
import solver.operators.OrderMutation;
import solver.operators.ResourceCrossover;
import solver.operators.ResourceMutation;
import solver.operators.TournamentSelection;

import java.util.ArrayList;
import java.util.List;

public class GA implements Algorithm {

    @Override
    public Solver prepareSolver(Schedule schedule, String filename, int counter) {
        List<Operator> operators = new ArrayList<>();
        int populationSize = 100;
        int passLimit = 1000;
        long timeLimit = 1000 * 1000;
        operators.add(new TournamentSelection(10));
        operators.add(new OrderCrossover(0.85));
        operators.add(new ResourceCrossover(0.85));
        operators.add(new OrderMutation(0.005));
        operators.add(new ResourceMutation(0.005));
        return new Solver(schedule, populationSize, operators, passLimit, timeLimit, filename + "_" + counter + "_");
    }
}
