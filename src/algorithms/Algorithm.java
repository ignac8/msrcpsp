package algorithms;

import problem.Schedule;
import solver.Solver;

public abstract class Algorithm {

    protected String prefix;

    public abstract Solver prepareSolver(Schedule schedule, String filename);

    public String getPrefix() {
        return prefix;
    }
}
