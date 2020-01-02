package it.zerko.msrcpsp.operator.mutation;

import it.zerko.msrcpsp.problem.Schedule;

public class OrderMutation extends Mutation {

    public OrderMutation(double chance) {
        super(chance);
    }

    @Override
    protected void mutation(Schedule schedule) {
        schedule.mutateOrder();
    }
}