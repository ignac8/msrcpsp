package solvers.operators;

import problem.Schedule;

import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class LocalSearch implements Operator {

    @Override
    public List<Schedule> call(List<Schedule> schedules) {
        for (int counter = 0; counter < schedules.size(); counter++) {
            Schedule schedule = schedules.get(counter);
            List<Schedule> neighbours = schedule.generateNeighbours();
            for (Schedule neighbour : neighbours) {
                neighbour.calculate();
            }
            schedules.set(counter, choose(neighbours, schedule));

        }
        return schedules;
    }

    protected Schedule choose(List<Schedule> neighbours, Schedule previousSchedule) {
        shuffle(neighbours);
        sort(neighbours);
        return neighbours.get(0).getFitness() < previousSchedule.getFitness() ? neighbours.get(0) : previousSchedule;
    }
}