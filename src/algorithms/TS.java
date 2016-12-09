package algorithms;

import problem.Schedule;
import solver.Solver;
import solver.operators.Operator;
import solver.operators.TabooSearch;

import java.util.ArrayList;
import java.util.List;

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