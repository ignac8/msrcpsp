package algorithms;

import problem.Schedule;
import solver.Solver;
import solver.operators.Operator;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {

    protected String prefix;
    protected List<Operator> preOperators;
    protected List<Operator> operators;
    protected List<Operator> postOperators;
    protected long timeLimit = 1000 * 1000;
    protected int populationSize;
    protected int passLimit;

    public Solver prepareSolver(Schedule schedule, String filename) {
        preOperators = new ArrayList<>();
        operators = new ArrayList<>();
        postOperators = new ArrayList<>();
        prepareOperators(schedule);
        return new Solver(schedule, populationSize, preOperators, operators, postOperators, passLimit, timeLimit, filename + "_" + prefix + "_");
    }

    protected abstract void prepareOperators(Schedule schedule);

    public String getPrefix() {
        return prefix;
    }
}