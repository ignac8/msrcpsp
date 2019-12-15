package it.zerko.msrcpsp;

import it.zerko.msrcpsp.algorithm.Algorithm;
import it.zerko.msrcpsp.algorithm.GA;
import it.zerko.msrcpsp.algorithm.GA_SA;
import it.zerko.msrcpsp.algorithm.GA_TS;
import it.zerko.msrcpsp.algorithm.SA;
import it.zerko.msrcpsp.algorithm.TS;
import it.zerko.msrcpsp.problem.Schedule;
import it.zerko.msrcpsp.solver.Solver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static it.zerko.msrcpsp.util.FileUtils.loadScheduleFromFile;
import static java.lang.Math.sqrt;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationHMS;
import static org.apache.commons.math3.stat.StatUtils.max;
import static org.apache.commons.math3.stat.StatUtils.mean;
import static org.apache.commons.math3.stat.StatUtils.min;
import static org.apache.commons.math3.stat.StatUtils.variance;

public class Main {
    public static void main(String... args) {
//        System.out.println("Press enter to continue...");
//        new Scanner(System.in).nextLine();
        long time = System.currentTimeMillis();
        System.out.println("OK!");
        File directory = new File("competitive");
        File[] files = directory.listFiles();
        int runs = 25;
        List<String> filenames = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName();
//            if ((filename.indexOf("10_3_5_3.def") == 0 || filename.indexOf("100_20_46_15.def") == 0) && filename.lastIndexOf(".def") == filename.length() - 4) {
            if (true) {
                Schedule schedule = loadScheduleFromFile(filename);
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                Vector<Algorithm> algorithms = new Vector<>();
                algorithms.add(new GA());
                algorithms.add(new TS());
                algorithms.add(new SA());
                algorithms.add(new GA_TS());
                algorithms.add(new GA_SA());
                Vector<Vector<Future<Schedule>>> vectors = new Vector<>();
                for (int outerCounter = 0; outerCounter < algorithms.size(); outerCounter++) {
                    Vector<Future<Schedule>> vector = new Vector<>(runs);
                    Algorithm algorithm = algorithms.get(outerCounter);
                    for (int innerCounter = 0; innerCounter < runs; innerCounter++) {
                        Solver solver = algorithm.prepareSolver(schedule, filename + "_" + innerCounter);
                        vector.add(executorService.submit(solver));
                    }
                    vectors.add(vector);
                }
                double[][] finesses = new double[algorithms.size()][runs];
                for (int outerCounter = 0; outerCounter < algorithms.size(); outerCounter++) {
                    for (int innerCounter = 0; innerCounter < runs; innerCounter++) {
                        try {
                            finesses[outerCounter][innerCounter] = vectors.get(outerCounter).get(innerCounter).get().getFitness();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println(filename);
                for (int counter = 0; counter < algorithms.size(); counter++) {
                    System.out.println(algorithms.get(counter).getPrefix() + ": " + min(finesses[counter]) + " " + mean(finesses[counter]) + " " + max(finesses[counter]) + " " + sqrt(variance(finesses[counter])));
                }
                executorService.shutdownNow();
            }
        }
        System.out.println("Finished: " + formatDurationHMS(System.currentTimeMillis() - time));
    }
}