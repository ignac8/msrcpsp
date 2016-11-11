package solvers.operators;

import com.google.common.collect.EvictingQueue;
import problem.Schedule;

import java.util.Iterator;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class TabuSearch extends LocalSearch implements Operator {

    private EvictingQueue<Schedule> tabu;

    public TabuSearch(int size) {
        tabu = EvictingQueue.create(size);
    }

    @Override
    protected Schedule choose(List<Schedule> neighbours, Schedule previousSchedule) {
        shuffle(neighbours);
        sort(neighbours);
        Schedule chosenSchedule = null;
        for (int counter = 0; counter < neighbours.size() && chosenSchedule == null; counter++) {
            Schedule proposedSchedule = neighbours.get(counter);
            boolean found = false;
            Iterator<Schedule> iterator = tabu.iterator();
            while (iterator.hasNext() && !found) {
                Schedule tabuSchedule = iterator.next();
                if (tabuSchedule.isSame(proposedSchedule)) {
                    found = true;
                }
            }
            if (!found) {
                chosenSchedule = proposedSchedule;
                tabu.add(chosenSchedule);
            }
        }
        if (chosenSchedule == null) {
            chosenSchedule = previousSchedule;
            tabu.poll();
        }
        return chosenSchedule;
    }
}