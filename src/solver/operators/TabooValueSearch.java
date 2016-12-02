package solver.operators;

import com.google.common.collect.EvictingQueue;
import org.apache.commons.math3.util.Precision;
import problem.Schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class TabooValueSearch extends LocalSearch implements Operator {

    private EvictingQueue<Double> taboo;
    private double eps;

    public TabooValueSearch(int neighbourSize, int tabooSize, double eps) {
        super(neighbourSize);
        this.taboo = EvictingQueue.create(tabooSize);
        this.eps = eps;
    }

    @Override
    protected List<Schedule> choose(List<List<Schedule>> listOfNeighbours, List<Schedule> previousSchedules) {
        List<Schedule> result = new ArrayList<>();
        for (int outerCounter = 0; outerCounter < listOfNeighbours.size(); outerCounter++) {
            List<Schedule> neighbours = listOfNeighbours.get(outerCounter);
            Schedule previousSchedule = previousSchedules.get(outerCounter);
            shuffle(neighbours);
            sort(neighbours);
            Schedule chosenSchedule = null;
            for (int innerCounter = 0; innerCounter < neighbours.size() && chosenSchedule == null; innerCounter++) {
                Schedule proposedSchedule = neighbours.get(innerCounter);
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
            result.add(chosenSchedule);
        }
        return result;
    }
}