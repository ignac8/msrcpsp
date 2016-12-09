package algorithms;

import problem.Schedule;
import solver.operators.OrderCrossover;
import solver.operators.OrderMutation;
import solver.operators.ResourceCrossover;
import solver.operators.ResourceMutation;
import solver.operators.TabooSearch;
import solver.operators.TournamentSelection;

public class GA_TS extends Algorithm {

    public GA_TS() {
        prefix = "GA_TS";
        populationSize = 100;
        passLimit = 1000;
    }

    @Override
    protected void prepareOperators(Schedule schedule) {
        preOperators.add(new TabooSearch(250, 10, 1000));
        operators.add(new TournamentSelection(1, 10));
        operators.add(new OrderCrossover(1, 0.9));
        operators.add(new ResourceCrossover(1, 0.9));
        operators.add(new OrderMutation(1, 0.01));
        operators.add(new ResourceMutation(1, 0.01));
        postOperators.add(new TabooSearch(250, 10, 1000));
    }
}