package utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import problem.Resource;
import problem.Schedule;
import problem.Skill;
import problem.Task;
import solver.Result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static org.jfree.chart.ChartUtilities.saveChartAsPNG;

public class FileUtils {
    public static Schedule loadScheduleFromFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("datasets\\" + path)));
            String line = "";
            while (!line.contains("ResourceID")) {
                line = reader.readLine();
            }
            List<Resource> resources = new ArrayList<>();
            line = reader.readLine();
            while (!line.contains("=====")) {
                List<String> list = asList(line.split("[ \t]+"));
                int resourceId = parseInt(list.get(0));
                double salary = parseDouble(list.get(1));
                List<Skill> skills = new ArrayList<>();
                for (int counter = 2; counter < list.size(); counter += 2) {
                    String skillName = list.get(counter);
                    skillName = skillName.replace(":", "");
                    int skillLevel = parseInt(list.get(counter + 1));
                    skills.add(new Skill(skillName, skillLevel));
                }
                resources.add(new Resource(resourceId, salary, skills));
                line = reader.readLine();
            }
            reader.readLine();
            line = reader.readLine();
            List<Task> tasks = new ArrayList<>();
            while (!line.contains("=====")) {
                List<String> list = asList(line.split("[ \t]+"));
                int taskId = parseInt(list.get(0));
                int taskDuration = parseInt(list.get(1));
                String skillName = list.get(2);
                skillName = skillName.replace(":", "");
                int skillLevel = parseInt(list.get(3));
                List<Task> preconditions = new ArrayList<>();
                for (int counter = 4; counter < list.size(); counter++) {
                    int preconditionId = parseInt(list.get(counter));
                    Task foundTask = null;
                    for (int i = 0; i < tasks.size() && foundTask == null; i++) {
                        Task task = tasks.get(i);
                        if (task.getTaskId() == preconditionId) {
                            foundTask = task;
                        }
                    }
                    preconditions.add(foundTask);
                }
                tasks.add(new Task(taskId, taskDuration, skillName, skillLevel, preconditions));
                line = reader.readLine();
            }
            reader.close();
            return new Schedule(tasks, resources);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveGraphToFile(List<Result> results, String filename) {
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
            saveChartAsPNG(file, chart, 1200, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveScheduleToFile(Schedule bestSchedule, String filename) {
        File directory = new File("results");
        File file = new File("results\\" + filename + "." + new SimpleDateFormat("YYYY_MM_dd_HH_mm_ss").format(new Date()) + ".sol");
        try {
            directory.mkdir();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            Multimap<Integer, Task> multimap = ArrayListMultimap.create();
            for (Task task : bestSchedule.getTasks()) {
                int startTime = task.getEndTime() - task.getDuration();
                multimap.put(startTime, task);
            }
            List<Integer> keys = new ArrayList<>(new TreeSet<>(multimap.keySet()));
            for (int key : keys) {
                String line = key + " ";
                Collection<Task> tasks = multimap.get(key);
                for (Task task : tasks) {
                    line += task.getResource().getResourceId() + "-" + task.getTaskId() + " ";
                }
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
