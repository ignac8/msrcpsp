package algorithms;

import problem.Schedule;
import solver.Solver;
import solver.operators.Operator;
import solver.operators.TabooSearch;

import java.util.ArrayList;
import java.util.List;

public class TS implements Algorithm {

    @Override
    public Solver prepareSolver(Schedule schedule, String filename) {
        List<Operator> operators = new ArrayList<>();
        int populationSize = 1;
        int passLimit = 10000;
        long timeLimit = 1000 * 1000;
        int neighbourSize = 10;
        int tabooSize = 100;
        operators.add(new TabooSearch(neighbourSize, tabooSize));
        return new Solver(schedule, populationSize, operators, passLimit, timeLimit, filename + "_");
    }
}
