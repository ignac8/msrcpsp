package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.search.SimulatedAnnealing;

import java.util.List;

public class SimulatedAnnealingAlgorithm extends Algorithm {

    private double maxTemp;
    private int searchSize;

    public SimulatedAnnealingAlgorithm(int populationSize, int passLimit, double maxTemp, int searchSize) {
        super(populationSize, passLimit);
        this.maxTemp = maxTemp;
        this.searchSize = searchSize;
    }

    protected List<Operator> prepareOperator() {
        return List.of(new SimulatedAnnealing(maxTemp, passLimit, searchSize));
    }
}
