package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.operator.OrderCrossover;
import it.zerko.msrcpsp.solver.operator.ResourceCrossover;
import it.zerko.msrcpsp.solver.operator.SimulatedAnnealing;
import it.zerko.msrcpsp.solver.operator.TournamentSelection;

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