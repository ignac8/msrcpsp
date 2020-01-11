package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.search.SimulatedAnnealing;

import java.util.List;

public class SimulatedAnnealingAlgorithm extends Algorithm {

    private double maxTemp;

    public SimulatedAnnealingAlgorithm(int passLimit, double maxTemp) {
        super(1, passLimit);
        this.maxTemp = maxTemp;

    }

    protected List<Operator> prepareOperator() {
        return List.of(new SimulatedAnnealing(maxTemp));
    }
}
