package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.search.LocalSearch;

import java.util.List;

public class LocalSearchAlgorithm extends Algorithm {

    public LocalSearchAlgorithm(int passLimit) {
        super(1, passLimit);
    }

    protected List<Operator> prepareOperator() {
        return List.of(new LocalSearch());
    }
}
