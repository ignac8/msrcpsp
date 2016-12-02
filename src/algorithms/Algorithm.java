package algorithms;

import problem.Schedule;
import solver.Solver;

public interface Algorithm {
    Solver prepareSolver(Schedule schedule, String filename);
}
