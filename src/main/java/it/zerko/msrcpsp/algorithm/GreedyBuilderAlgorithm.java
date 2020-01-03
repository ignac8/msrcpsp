package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.greedy.GreedyBuilder;

import java.util.List;

public class GreedyBuilderAlgorithm extends Algorithm {

    public GreedyBuilderAlgorithm(int populationSize) {
        super(populationSize, 1);
    }

    protected List<Operator> prepareOperator() {
        return List.of(new GreedyBuilder());
    }
}
