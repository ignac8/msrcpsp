package solver.operators;

import problem.Schedule;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.exp;
import static java.util.Collections.shuffle;
import static org.apache.commons.lang3.RandomUtils.nextDouble;

public class SimulatedAnnealing extends LocalSearch {

    private List<Double> currTemps;
    private double decTemp;
    private List<Double> minFitnesses;
    private List<Double> hiddenTemps;

    public SimulatedAnnealing(int neighbourSize, double decTemp, double modifier, int minTime, int maxTime, int populationSize) {
        super(neighbourSize);
        this.decTemp = decTemp;
        currTemps = new ArrayList<>();
        minFitnesses = new ArrayList<>();
        hiddenTemps = new ArrayList<>();
        Double hiddenTemp = (maxTime - minTime) * modifier;
        for (int counter = 0; counter < populationSize; counter++) {
            hiddenTemps.add(hiddenTemp);
            currTemps.add(hiddenTemp);
            minFitnesses.add(0.0);
        }
    }

    @Override
    protected List<Schedule> choose(List<List<Schedule>> listOfNeighbours, List<Schedule> previousSchedules) {
        List<Schedule> result = new ArrayList<>();
        for (int outerCounter = 0; outerCounter < listOfNeighbours.size(); outerCounter++) {
            Double currTemp = currTemps.get(outerCounter);
            Double minFitness = minFitnesses.get(outerCounter);
            Double hiddenTemp = hiddenTemps.get(outerCounter);
            List<Schedule> neighbours = listOfNeighbours.get(outerCounter);
            Schedule previousSchedule = previousSchedules.get(outerCounter);
            shuffle(neighbours);
            Schedule chosenSchedule = previousSchedule;
            for (int counter = 0; counter < neighbours.size() && currTemp > 0; counter++) {
                Schedule proposedSchedule = neighbours.get(counter);
                double prob = exp((chosenSchedule.getFitness() - proposedSchedule.getFitness()) / currTemp);
                if (proposedSchedule.getFitness() < chosenSchedule.getFitness() || nextDouble(0, 1) < prob) {
                    chosenSchedule = proposedSchedule;
                }
            }
            double currentFitness = chosenSchedule.getFitness();
            if (minFitness == 0 || currentFitness < minFitness) {
                minFitness = currentFitness;
            }
            hiddenTemp *= decTemp;
            currTemp = hiddenTemp * (1 + (currentFitness - minFitness) / currentFitness);
            if (currTemp < 0) currTemp = 0.0;
            result.add(chosenSchedule);
            currTemps.set(outerCounter, currTemp);
            minFitnesses.set(outerCounter, minFitness);
            hiddenTemps.set(outerCounter, hiddenTemp);
        }
        return result;
    }
}