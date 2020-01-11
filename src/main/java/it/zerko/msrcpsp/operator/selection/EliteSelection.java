package it.zerko.msrcpsp.operator.selection;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import org.apache.commons.lang3.RandomUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EliteSelection extends Operator {

    private Optional<Schedule> eliteSchedule;

    public EliteSelection() {
        this.eliteSchedule = Optional.empty();
    }

    @Override
    public List<Schedule> modify(List<Schedule> schedules) {
        if (eliteSchedule.isPresent()) {
            schedules.remove(schedules.get(RandomUtils.nextInt(0, schedules.size())));
            schedules.add(eliteSchedule.get());
            eliteSchedule = Optional.empty();
        } else {
            eliteSchedule = schedules.stream().min(Comparator.naturalOrder());
        }
        return schedules;
    }

}
