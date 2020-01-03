package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.initializer.Initializer;
import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;

import java.util.List;

public abstract class Algorithm {

    protected int populationSize;
    protected int passLimit;

    public Algorithm(int populationSize, int passLimit) {
        this.populationSize = populationSize;
        this.passLimit = passLimit;
    }

    public Solver prepareSolver(List<String> lines) {
        List<Operator> operators = prepareOperator();
        List<Schedule> schedules = new Initializer().initialize(lines, populationSize);
        return new Solver(schedules, operators, passLimit);
    }

    protected abstract List<Operator> prepareOperator();
}
