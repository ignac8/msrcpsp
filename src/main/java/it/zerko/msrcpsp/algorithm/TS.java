package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.operator.TabooSearch;

public class TS extends Algorithm {

    public TS() {
        prefix = "TS";
        populationSize = 1;
        passLimit = 10000;
    }

    @Override
    protected void prepareOperators(Schedule schedule) {
        operators.add(new TabooSearch(1, 10, 1000));
    }
}