package solver.operators;

import problem.Schedule;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class LocalSearch extends Operator {

    private int size;

    public LocalSearch(int callCount, int neighbourSize) {
        super(callCount);
        this.size = neighbourSize;
    }

    @Override
    public List<Schedule> call(List<Schedule> schedules) {
        List<List<Schedule>> listOfNeighbours = new ArrayList<>();
        for (int counter = 0; counter < schedules.size(); counter++) {
            Schedule schedule = schedules.get(counter);
            List<Schedule> neighbours = schedule.generateRandomNeighbours(size);
            for (Schedule neighbour : neighbours) {
                neighbour.calculate();
            }
            listOfNeighbours.add(neighbours);
        }
        schedules = choose(listOfNeighbours, schedules);
        return schedules;
    }

    protected List<Schedule> choose(List<List<Schedule>> listOfNeighbours, List<Schedule> previousSchedules) {
        List<Schedule> result = new ArrayList<>();
        for (int counter = 0; counter < listOfNeighbours.size(); counter++) {
            List<Schedule> neighbours = listOfNeighbours.get(counter);
            Schedule previousSchedule = previousSchedules.get(counter);
            shuffle(neighbours);
            sort(neighbours);
            Schedule bestNeighbour = neighbours.get(0);
            Schedule chosenSchedule = bestNeighbour.getFitness() < previousSchedule.getFitness() ? bestNeighbour : previousSchedule;
            result.add(chosenSchedule);
        }
        return result;
    }
}