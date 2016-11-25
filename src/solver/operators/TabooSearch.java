package solver.operators;

import com.google.common.collect.EvictingQueue;
import problem.Schedule;

import java.util.Iterator;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class TabooSearch extends LocalSearch implements Operator {

    private EvictingQueue<Schedule> taboo;

    public TabooSearch(int size, int tabooSize) {
        super(size);
        taboo = EvictingQueue.create(tabooSize);
    }

    @Override
    protected Schedule choose(List<Schedule> neighbours, Schedule previousSchedule) {
        shuffle(neighbours);
        sort(neighbours);
        Schedule chosenSchedule = null;
        for (int counter = 0; counter < neighbours.size() && chosenSchedule == null; counter++) {
            Schedule proposedSchedule = neighbours.get(counter);
            boolean found = false;
            Iterator<Schedule> iterator = taboo.iterator();
            while (iterator.hasNext() && !found) {
                Schedule tabooSchedule = iterator.next();
                if (tabooSchedule.isSame(proposedSchedule)) {
                    found = true;
                }
            }
            if (!found) {
                chosenSchedule = proposedSchedule;
                taboo.add(chosenSchedule);
            }
        }
        if (chosenSchedule == null) {
            chosenSchedule = previousSchedule;
            taboo.poll();
        }
        return chosenSchedule;
    }
}