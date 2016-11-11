import problem.Schedule;
import solvers.Solver;
import solvers.operators.LocalSearch;
import solvers.operators.Operator;
import solvers.operators.OrderCrossover;
import solvers.operators.OrderMutation;
import solvers.operators.OrderRepair;
import solvers.operators.ResourceCrossover;
import solvers.operators.ResourceMutation;
import solvers.operators.SimulatedAnnealing;
import solvers.operators.TournamentSelection;
import solvers.operators.UnknownSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;
import static java.util.Arrays.asList;
import static org.apache.commons.math3.stat.StatUtils.max;
import static org.apache.commons.math3.stat.StatUtils.mean;
import static org.apache.commons.math3.stat.StatUtils.min;
import static org.apache.commons.math3.stat.StatUtils.variance;
import static utils.FileUtils.loadScheduleFromFile;

public class Main {
    public static void main(String... args) {
        //System.out.println("Press enter to continue...");
        //new Scanner(System.in).nextLine();
        System.out.println("OK!");
        File directory = new File("datasets");
        List<File> files = asList(directory.listFiles());
        for (File file : files) {
            String filename = file.getName();
            if ((filename.indexOf("100_10_65_15.def") == 0 || filename.indexOf("100_10_65_15.def") == 0) && filename.lastIndexOf(".def") == filename.length() - 4) {
                Schedule schedule = loadScheduleFromFile(filename);
                int counterLimit = 10;
                double[] finesses = new double[counterLimit];
                for (int counter = 0; counter < counterLimit; counter++) {
                    List<Operator> operators = new ArrayList<>();
                    int populationSize = 1;
                    int passLimit = 1000;
                    long timeLimit = 50 * 1000;
//                    operators.add(new OrderRepair());
//                    operators.add(new TournamentSelection(10));
//                    operators.add(new OrderCrossover(0.85));
//                    operators.add(new ResourceCrossover(0.85));
//                    operators.add(new OrderMutation(0.01));
//                    operators.add(new ResourceMutation(0.01));
//                    operators.add(new OrderRepair());
                    //operators.add(new LocalSearch());
                    //operators.add(new UnknownSearch());
                    //operators.add(new TabuValueSearch(20, Precision.EPSILON));
                    double temp = 0.025;
                    operators.add(new SimulatedAnnealing(temp, temp * passLimit));
                    Solver solver = new Solver(schedule, populationSize, operators, passLimit, timeLimit);
                    Schedule best = solver.run();
                    solver.graph(filename);
                    solver.save(filename);
                    finesses[counter] = best.getFitness();
                    System.out.println(filename + ": " + (counter + 1));
                }
                System.out.println(filename + ": " + min(finesses) + " " + mean(finesses) + " " + max(finesses) + " " + sqrt(variance(finesses)));
            }
        }
        System.out.println("Finished");
    }
}