package solver.operators;

import problem.Schedule;

import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class UnknownSearch extends LocalSearch {

    public UnknownSearch(int neighbourSize) {
        super(neighbourSize);
    }

    @Override
    protected Schedule choose(List<Schedule> neighbours, Schedule previousSchedule) {
        shuffle(neighbours);
        sort(neighbours);
        return previousSchedule.isSame(neighbours.get(0)) ? neighbours.get(1) : neighbours.get(0);
    }
}