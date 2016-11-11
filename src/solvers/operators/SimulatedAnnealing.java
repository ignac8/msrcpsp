package solvers.operators;

import org.apache.commons.lang3.RandomUtils;
import problem.Schedule;

import java.util.List;

import static java.lang.Math.exp;
import static java.util.Collections.shuffle;
import static org.apache.commons.lang3.RandomUtils.nextDouble;

public class SimulatedAnnealing extends LocalSearch {

    private double currTemp;
    private double decTemp;

    public SimulatedAnnealing(double decTemp, double maxTemp) {
        this.decTemp = decTemp;
        this.currTemp = maxTemp;
    }

    @Override
    protected Schedule choose(List<Schedule> neighbours, Schedule previousSchedule) {
        shuffle(neighbours);
        Schedule chosenSchedule = previousSchedule;
        for (int counter = 0; counter < neighbours.size() && currTemp > 0 /*&& chosenSchedule == null*/; counter++) {
            Schedule proposedSchedule = neighbours.get(counter);
//            if (proposedSchedule.getFitness() < chosenSchedule.getFitness()
//                    || nextDouble(0, 1) < exp((chosenSchedule.getFitness() - previousSchedule.getFitness()) / currTemp)) {
            double prob = exp((chosenSchedule.getFitness() - proposedSchedule.getFitness()) / currTemp);
            if (proposedSchedule.getFitness() < chosenSchedule.getFitness() || nextDouble(0, 1) < prob) {
                chosenSchedule = proposedSchedule;
            }
        }
        currTemp = currTemp > 0 ? currTemp - decTemp : 0;
        return chosenSchedule;
    }
}
