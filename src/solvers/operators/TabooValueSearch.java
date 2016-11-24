package solvers.operators;

import com.google.common.collect.EvictingQueue;
import org.apache.commons.math3.util.Precision;
import problem.Schedule;

import java.util.Iterator;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class TabooValueSearch extends LocalSearch implements Operator {

    private EvictingQueue<Double> taboo;
    private double eps;

    public TabooValueSearch(int size, int tabooSize, double eps) {
        super(size);
        this.taboo = EvictingQueue.create(tabooSize);
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
            Iterator<Double> iterator = taboo.iterator();
            while (iterator.hasNext() && !found) {
                Double tabooSchedule = iterator.next();
                if (Precision.compareTo(tabooSchedule, proposedSchedule.getFitness(), eps) == 0) {
                    found = true;
                }
            }
            if (!found) {
                chosenSchedule = proposedSchedule;
                taboo.add(chosenSchedule.getFitness());
            }
        }
        if (chosenSchedule == null) {
            chosenSchedule = previousSchedule;
            taboo.poll();
        }
        return chosenSchedule;
    }
}