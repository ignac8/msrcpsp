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
        populationSize = 100;
        passLimit = 1000;
    }

    @Override
    protected void prepareOperators(Schedule schedule) {
        operators.add(new TournamentSelection(1, 10));
        operators.add(new OrderCrossover(1, 0.9));
        operators.add(new ResourceCrossover(1, 0.9));
        operators.add(new OrderMutation(1, 0.01));
        operators.add(new ResourceMutation(1, 0.01));
    }
}