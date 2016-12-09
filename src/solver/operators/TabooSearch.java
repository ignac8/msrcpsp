package solver.operators;

import com.google.common.collect.EvictingQueue;
import problem.Schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class TabooSearch extends LocalSearch {

    private EvictingQueue<Schedule> taboo;

    public TabooSearch(int callCount, int neighbourSize, int tabooSize) {
        super(callCount, neighbourSize);
        taboo = EvictingQueue.create(tabooSize);
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
            result.add(chosenSchedule);
        }
        return result;
    }
}