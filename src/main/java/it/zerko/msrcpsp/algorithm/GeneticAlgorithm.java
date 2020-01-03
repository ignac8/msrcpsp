package it.zerko.msrcpsp.algorithm;

import it.zerko.msrcpsp.operator.Operator;
import it.zerko.msrcpsp.operator.crossover.OrderCrossover;
import it.zerko.msrcpsp.operator.crossover.ResourceCrossover;
import it.zerko.msrcpsp.operator.mutation.OrderMutation;
import it.zerko.msrcpsp.operator.mutation.ResourceMutation;
import it.zerko.msrcpsp.operator.selection.TournamentSelection;

import java.util.List;

public class GeneticAlgorithm extends Algorithm {

    private int tournamentSize;
    private double crossoverChance;
    private double mutationChance;

    public GeneticAlgorithm(int populationSize, int passLimit, int tournamentSize,
                            double crossoverChance, double mutationChance) {
        super(populationSize, passLimit);
        this.tournamentSize = tournamentSize;
        this.crossoverChance = crossoverChance;
        this.mutationChance = mutationChance;
    }

    protected List<Operator> prepareOperator() {
        return List.of(
                new TournamentSelection(tournamentSize),
                new OrderCrossover(crossoverChance),
                new ResourceCrossover(crossoverChance),
                new OrderMutation(mutationChance),
                new ResourceMutation(mutationChance));
    }
}
