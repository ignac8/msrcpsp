package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.operator.OrderCrossover;
import it.zerko.msrcpsp.solver.operator.OrderMutation;
import it.zerko.msrcpsp.solver.operator.ResourceCrossover;
import it.zerko.msrcpsp.solver.operator.ResourceMutation;
import it.zerko.msrcpsp.solver.operator.TournamentSelection;

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