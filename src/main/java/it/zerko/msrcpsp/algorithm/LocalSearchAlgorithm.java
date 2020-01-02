package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.search.LocalSearch;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class LocalSearchAlgorithm extends Algorithm {

    protected List<Operator> prepareOperator() {
        return List.of(new LocalSearch());
    }
}
