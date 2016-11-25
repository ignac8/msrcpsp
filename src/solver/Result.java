package solver;

import problem.Schedule;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Result {
    private double min;
    private double avg;
    private double max;

    public Result(List<Schedule> schedules) {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        double sum = 0;
        for (Schedule schedule : schedules) {
            double fitness = schedule.getFitness();
            sum += fitness;
            min = min(min, fitness);
            max = max(max, fitness);
        }
        avg = sum / schedules.size();
    }

    public double getMin() {
        return min;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }
}
