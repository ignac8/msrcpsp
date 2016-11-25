import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import problem.Schedule;
import solvers.Solver;
import solvers.operators.Operator;
import solvers.operators.OrderCrossover;
import solvers.operators.OrderMutation;
import solvers.operators.OrderRepair;
import solvers.operators.ResourceCrossover;
import solvers.operators.ResourceMutation;
import solvers.operators.SimulatedAnnealing;
import solvers.operators.TabooSearch;
import solvers.operators.TabooValueSearch;
import solvers.operators.TournamentSelection;
import solvers.operators.UnknownSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Math.sqrt;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationHMS;
import static org.apache.commons.math3.stat.StatUtils.max;
import static org.apache.commons.math3.stat.StatUtils.mean;
import static org.apache.commons.math3.stat.StatUtils.min;
import static org.apache.commons.math3.stat.StatUtils.variance;
import static utils.FileUtils.loadScheduleFromFile;

public class Main {
    public static void main(String... args) {
//        System.out.println("Press enter to continue...");
//        new Scanner(System.in).nextLine();
        long time = System.currentTimeMillis();
        System.out.println("OK!");
        File directory = new File("competitive");
        List<File> files = asList(directory.listFiles());
        int runs = 10;
        List<String> filenames = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName();
//            if ((filename.indexOf("200_20_54_15.def") == 0 || filename.indexOf("200_20_54_15.def") == 0) && filename.lastIndexOf(".def") == filename.length() - 4) {
            if (true) {
                Schedule schedule = loadScheduleFromFile(filename);
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                Vector<Future<Schedule>> vector = new Vector<>(runs);
                double[] finesses = new double[runs];
                for (int counter = 0; counter < runs; counter++) {
                    List<Operator> operators = new ArrayList<>();
                    int populationSize = 1;
                    int passLimit = 10000;
                    long timeLimit = 1000 * 1000;
//                    operators.add(new OrderRepair());
//                    operators.add(new TournamentSelection(10));
//                    operators.add(new OrderCrossover(0.85));
//                    operators.add(new ResourceCrossover(0.85));
//                    operators.add(new OrderMutation(0.005));
//                    operators.add(new ResourceMutation(0.005));
//                    operators.add(new OrderRepair());
//                    operators.add(new LocalSearch());
                    int size = 10;
                    int tabooSize = 1000;
                    operators.add(new TabooSearch(size, tabooSize));
//                    operators.add(new TabooValueSearch(1, 20, Precision.EPSILON));
//                    operators.add(new UnknownSearch(size));
//                    double decTemp = 0.99;
//                    double modifier = 1;
//                    operators.add(new SimulatedAnnealing(size, decTemp, modifier, schedule.minTime(), schedule.maxTime()));
                    Solver solver = new Solver(schedule, populationSize, operators, passLimit, timeLimit, filename + "_" + counter + "_");
//                    solver.call();
                    vector.add(executorService.submit(solver));
                }
                for (int counter = 0; counter < runs; counter++) {
                    try {
                        finesses[counter] = vector.get(counter).get().getFitness();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(filename + ": " + min(finesses) + " " + mean(finesses) + " " + max(finesses) + " " + sqrt(variance(finesses)));
                executorService.shutdownNow();
            }
        }
//        System.out.println("Finished: " + (System.currentTimeMillis() - time) + " ms");
        System.out.println("Finished: " + formatDurationHMS(System.currentTimeMillis() - time));
    }
}