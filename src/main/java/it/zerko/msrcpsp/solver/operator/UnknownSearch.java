package it.zerko.msrcpsp.solver.operator;

import it.zerko.msrcpsp.problem.Schedule;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class UnknownSearch extends LocalSearch {

    public UnknownSearch(int callCount, int neighbourSize) {
        super(callCount, neighbourSize);
    }

    @Override
    protected List<Schedule> choose(List<List<Schedule>> listOfNeighbours, List<Schedule> previousSchedules) {
        List<Schedule> result = new ArrayList<>();
        for (int counter = 0; counter < listOfNeighbours.size(); counter++) {
            List<Schedule> neighbours = listOfNeighbours.get(counter);
            Schedule previousSchedule = previousSchedules.get(counter);
            shuffle(neighbours);
            sort(neighbours);
            Schedule chosenSchedule = previousSchedule.isSame(neighbours.get(0)) ? neighbours.get(1) : neighbours.get(0);
            result.add(chosenSchedule);
        }
        return result;
    }
}