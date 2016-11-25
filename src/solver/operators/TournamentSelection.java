package solver.operators;

import problem.Schedule;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomUtils.nextInt;

public class TournamentSelection implements Operator {

    private int tournamentSize;

    public TournamentSelection(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    @Override
    public List<Schedule> call(List<Schedule> schedules) {
        int numberOfSchedules = schedules.size();
        List<Schedule> newList = new ArrayList<>();
        for (int outerCounter = 0; outerCounter < numberOfSchedules; outerCounter++) {
            Schedule scheduleToAdd = schedules.get(nextInt(0, numberOfSchedules));
            for (int innerCounter = 1; innerCounter < tournamentSize; innerCounter++) {
                Schedule newSchedule = schedules.get(nextInt(0, numberOfSchedules));
                if (newSchedule.getFitness() < scheduleToAdd.getFitness()) {
                    scheduleToAdd = newSchedule;
                }
            }
            Schedule clonedSchedule = new Schedule(scheduleToAdd);
            newList.add(clonedSchedule);
        }
        return newList;
    }
}
