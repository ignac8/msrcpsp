package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.crossover.OrderCrossover;
import it.zerko.msrcpsp.operator.crossover.ResourceCrossover;
import it.zerko.msrcpsp.operator.mutation.OrderMutation;
import it.zerko.msrcpsp.operator.mutation.ResourceMutation;
import it.zerko.msrcpsp.operator.selection.TournamentSelection;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GeneticAlgorithm extends Algorithm {

    private int tournamentSize;
    private double crossoverChance;
    private double mutationChance;

    protected List<Operator> prepareOperator() {
        return List.of(
                new TournamentSelection(tournamentSize),
                new OrderCrossover(crossoverChance),
                new ResourceCrossover(crossoverChance),
                new OrderMutation(mutationChance),
                new ResourceMutation(mutationChance));
    }
}
