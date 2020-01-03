package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.search.LocalSearch;

import java.util.List;

public class LocalSearchAlgorithm extends Algorithm {

    private int searchSize;

    public LocalSearchAlgorithm(int populationSize, int passLimit, int searchSize) {
        super(populationSize, passLimit);
        this.searchSize = searchSize;
    }

    protected List<Operator> prepareOperator() {
        return List.of(new LocalSearch(searchSize));
    }
}
