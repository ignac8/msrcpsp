package algorithms;

import problem.Schedule;
import solver.operators.OrderCrossover;
import solver.operators.OrderMutation;
import solver.operators.ResourceCrossover;
import solver.operators.ResourceMutation;
import solver.operators.SimulatedAnnealing;
import solver.operators.TournamentSelection;

public class GA_SA extends Algorithm {

    public GA_SA() {
        prefix = "GA_SA";
        populationSize = 100;
        passLimit = 1000;
    }

    @Override
    protected void prepareOperators(Schedule schedule) {
        operators.add(new TournamentSelection(1, 10));
        operators.add(new OrderCrossover(1, 0.9));
        operators.add(new ResourceCrossover(1, 0.9));
        operators.add(new SimulatedAnnealing(1, 10, 0.9, 10, schedule.minTime(), schedule.maxTime(), populationSize));
    }
}