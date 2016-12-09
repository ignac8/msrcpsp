package algorithms;

import problem.Schedule;
import solver.operators.SimulatedAnnealing;

public class SA extends Algorithm {

    public SA() {
        prefix = "SA";
        populationSize = 1;
        passLimit = 100000;
    }

    @Override
    protected void prepareOperators(Schedule schedule) {
        operators.add(new SimulatedAnnealing(1, 1, 0.99975, 1, schedule.minTime(), schedule.maxTime(), populationSize));
    }
}