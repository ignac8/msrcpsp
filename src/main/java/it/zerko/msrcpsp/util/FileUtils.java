package it.zerko.msrcpsp.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.problem.Task;
import it.zerko.msrcpsp.solver.Result;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    private static final FileUtils INSTANCE = new FileUtils();

    public static FileUtils getInstance() {
        return INSTANCE;
    }

    public void saveGraphToFile(List<Result> results, String filename) {
        XYSeries popMin = new XYSeries("Min");
        XYSeries popAvg = new XYSeries("Avg");
        XYSeries popMax = new XYSeries("Max");
        for (int counter = 0; counter < results.size(); counter++) {
            Result result = results.get(counter);
            popMin.add(counter, result.getMin());
            popAvg.add(counter, result.getAvg());
            popMax.add(counter, result.getMax());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(popMin);
        dataset.addSeries(popAvg);
        dataset.addSeries(popMax);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Harmonogramowanie",
                "Pokolenie",
                "Ocena",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
        File directory = new File("graphs");
        File file = new File("graphs\\" + filename + "." + new SimpleDateFormat("YYYY_MM_dd_HH_mm_ss").format(new Date()) + ".png");
        try {
            directory.mkdir();
            ChartUtils.saveChartAsPNG(file, chart, 1200, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveScheduleToFile(Schedule bestSchedule, String filename) {
        File directory = new File("results");
        File file = new File("results\\" + filename + "." + new SimpleDateFormat("YYYY_MM_dd_HH_mm_ss").format(new Date()) + ".sol");
        try {
            directory.mkdir();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            Multimap<Integer, Task> multimap = ArrayListMultimap.create();
            for (Task task : bestSchedule.getTasks()) {
                int startTime = task.getEndTime().get() - task.getDuration();
                multimap.put(startTime, task);
            }
            List<Integer> keys = new ArrayList<>(new TreeSet<>(multimap.keySet()));
            for (int key : keys) {
                writer.write(multimap.get(key).stream().map(task -> bestSchedule.getAssignedResources().get(task).getResourceId() + "-" + task.getTaskId() + " ")
                        .collect(Collectors.joining("", key + " ", "")));
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
