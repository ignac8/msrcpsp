import algorithms.GA;
import algorithms.SA;
import algorithms.TS;
import problem.Schedule;
import solver.Solver;

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
        int runs = 1;
        List<String> filenames = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName();
//            if ((filename.indexOf("100_5_22_15.def") == 0 || filename.indexOf("100_5_22_15.def") == 0) && filename.lastIndexOf(".def") == filename.length() - 4) {
            if (true) {
                Schedule schedule = loadScheduleFromFile(filename);
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                Vector<Future<Schedule>> vectorGA = new Vector<>(runs);
                Vector<Future<Schedule>> vectorTS = new Vector<>(runs);
                Vector<Future<Schedule>> vectorSA = new Vector<>(runs);
                double[][] finesses = new double[3][runs];
                for (int counter = 0; counter < runs; counter++) {
                    Solver solverGA = new GA().prepareSolver(schedule, filename + "_GA_" + counter);
                    Solver solverTS = new TS().prepareSolver(schedule, filename + "_TS_" + counter);
                    Solver solverSA = new SA().prepareSolver(schedule, filename + "_SA_" + counter);
                    vectorGA.add(executorService.submit(solverGA));
                    vectorTS.add(executorService.submit(solverTS));
                    vectorSA.add(executorService.submit(solverSA));
                }
                for (int counter = 0; counter < runs; counter++) {
                    try {
                        finesses[0][counter] = vectorGA.get(counter).get().getFitness();
                        finesses[1][counter] = vectorTS.get(counter).get().getFitness();
                        finesses[2][counter] = vectorSA.get(counter).get().getFitness();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(filename);
                System.out.println("GA : " + min(finesses[0]) + " " + mean(finesses[0]) + " " + max(finesses[0]) + " " + sqrt(variance(finesses[0])));
                System.out.println("TS : " + min(finesses[1]) + " " + mean(finesses[1]) + " " + max(finesses[1]) + " " + sqrt(variance(finesses[1])));
                System.out.println("SA : " + min(finesses[2]) + " " + mean(finesses[2]) + " " + max(finesses[2]) + " " + sqrt(variance(finesses[2])));
                executorService.shutdownNow();
            }
        }
        System.out.println("Finished: " + formatDurationHMS(System.currentTimeMillis() - time));
    }
}