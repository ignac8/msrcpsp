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

public class GA extends Algorithm {

    public GA() {
        prefix = "GA";
    }

    @Override
    public Solver prepareSolver(Schedule schedule, String filename) {
        List<Operator> preOperators = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        List<Operator> postOperators = new ArrayList<>();
        int populationSize = 100;
        int passLimit = 1000;
        long timeLimit = 1000 * 1000;
        operators.add(new TournamentSelection(1, 10));
        operators.add(new OrderCrossover(1, 0.9));
        operators.add(new ResourceCrossover(1, 0.9));
        operators.add(new OrderMutation(1, 0.01));
        operators.add(new ResourceMutation(1, 0.01));
        return new Solver(schedule, populationSize, preOperators, operators, postOperators, passLimit, timeLimit, filename + "_" + prefix + "_");
    }
}
