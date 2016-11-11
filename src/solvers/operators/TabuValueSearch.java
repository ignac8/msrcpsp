package solvers.operators;

import com.google.common.collect.EvictingQueue;
import org.apache.commons.math3.util.Precision;
import problem.Schedule;

import java.util.Iterator;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class TabuValueSearch extends LocalSearch implements Operator {

    private EvictingQueue<Double> tabu;
    private double eps;

    public TabuValueSearch(int size, double eps) {
        this.tabu = EvictingQueue.create(size);
        this.eps = eps;
    }

    @Override
    protected Schedule choose(List<Schedule> neighbours, Schedule previousSchedule) {
        shuffle(neighbours);
        sort(neighbours);
        Schedule chosenSchedule = null;
        for (int counter = 0; counter < neighbours.size() && chosenSchedule == null; counter++) {
            Schedule proposedSchedule = neighbours.get(counter);
            boolean found = false;
            Iterator<Double> iterator = tabu.iterator();
            while (iterator.hasNext() && !found) {
                Double tabuSchedule = iterator.next();
                if (Precision.compareTo(tabuSchedule, proposedSchedule.getFitness(), eps) == 0) {
                    found = true;
                }
            }
            if (!found) {
                chosenSchedule = proposedSchedule;
                tabu.add(chosenSchedule.getFitness());
            }
        }
        if (chosenSchedule == null) {
            chosenSchedule = previousSchedule;
            tabu.poll();
        }
        return chosenSchedule;
    }
}