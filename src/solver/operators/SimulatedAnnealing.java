package solver.operators;

import problem.Schedule;

import java.util.List;

import static java.lang.Math.exp;
import static java.util.Collections.shuffle;
import static org.apache.commons.lang3.RandomUtils.nextDouble;

public class SimulatedAnnealing extends LocalSearch {

    private double currTemp;
    private double decTemp;
    private double minFitness;
    private double hiddenTemp;

    public SimulatedAnnealing(int size, double decTemp, double modifier, int minTime, int maxTime) {
        super(size);
        this.decTemp = decTemp;
        this.hiddenTemp = (maxTime - minTime) * modifier;
        this.currTemp = hiddenTemp;
        //this.currTemp = maxTime - minTime;
        //System.out.println(currTemp);
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
        //currTemp = currTemp - decTemp;
        double currentFitness = chosenSchedule.getFitness();
        if (minFitness == 0 || currentFitness < minFitness) {
            minFitness = currentFitness;
        }
        hiddenTemp *= decTemp;
        currTemp = hiddenTemp * (1 + (currentFitness - minFitness) / currentFitness);
        if (currTemp < 0) currTemp = 0;
        //System.out.println(currTemp);
        return chosenSchedule;
    }
}
