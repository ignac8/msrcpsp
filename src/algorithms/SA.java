package algorithms;

import problem.Schedule;
import solver.Solver;
import solver.operators.Operator;
import solver.operators.OrderCrossover;
import solver.operators.OrderMutation;
import solver.operators.ResourceCrossover;
import solver.operators.ResourceMutation;
import solver.operators.SimulatedAnnealing;
import solver.operators.TournamentSelection;

import java.util.ArrayList;
import java.util.List;

public class SA extends Algorithm {

    public SA() {
        prefix = "SA";
        populationSize = 1;
        passLimit = 100000;
    }

    @Override
    protected void prepareOperators(Schedule schedule) {
        operators.add(new SimulatedAnnealing(1, 1, 0.99975, 1, schedule.minTime(), schedule.maxTime(), populationSize));
    }
}