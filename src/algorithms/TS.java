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
    }

    @Override
    public Solver prepareSolver(Schedule schedule, String filename) {
        List<Operator> preOperators = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();
        List<Operator> postOperators = new ArrayList<>();
        int populationSize = 1;
        int passLimit = 10000;
        long timeLimit = 1000 * 1000;
        int neighbourSize = 10;
        int tabooSize = 1000;
        operators.add(new TabooSearch(1, neighbourSize, tabooSize));
        return new Solver(schedule, populationSize, preOperators, operators, postOperators, passLimit, timeLimit, filename + "_" + prefix + "_");
    }
}
