package it.zerko.msrcpsp.solver;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.statistic.GenerationStatistic;
import lombok.Getter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Getter
public class Solver {

    private List<Schedule> schedules;
    private List<Operator> operators;
    private int passLimit;
    private Optional<Schedule> bestSchedule;
    private List<GenerationStatistic> results;

    public Solver(List<Schedule> schedules, List<Operator> operators, int passLimit) {
        this.schedules = schedules;
        this.operators = operators;
        this.passLimit = passLimit;
        this.bestSchedule = Optional.empty();
        this.results = new ArrayList<>(passLimit);
    }

    public void solve() {
        IntStream.range(0, passLimit).forEach(i -> pass());
    }

    private void pass() {
        operators.forEach(operator -> schedules = operator.modify(schedules));
        schedules.forEach(Schedule::calculateFitness);
        Schedule schedule = schedules.stream().sorted().findFirst().get();
        if (bestSchedule.isEmpty() || schedule.getFitness() < bestSchedule.get().getFitness()) {
            bestSchedule = Optional.of(new Schedule(schedule));
        }
        results.add(new GenerationStatistic(schedules));
    }

    public JFreeChart toGraph() {
        XYSeries popMin = new XYSeries("Minimum");
        XYSeries popAvg = new XYSeries("Average");
        XYSeries popMax = new XYSeries("Maximum");
        IntStream.range(0, results.size()).forEach(counter -> popMin.add(counter, results.get(counter).getMinimum()));
        IntStream.range(0, results.size()).forEach(counter -> popAvg.add(counter, results.get(counter).getAverage()));
        IntStream.range(0, results.size()).forEach(counter -> popMax.add(counter, results.get(counter).getMaximum()));
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(popMin);
        dataset.addSeries(popAvg);
        dataset.addSeries(popMax);
        return ChartFactory.createXYLineChart(
                "Scheduling",
                "Population",
                "Fitness",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
    }

}
