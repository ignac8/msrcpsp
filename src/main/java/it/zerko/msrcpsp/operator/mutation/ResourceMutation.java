package it.zerko.msrcpsp.operator.mutation;

import it.zerko.msrcpsp.problem.Schedule;

public class ResourceMutation extends Mutation {

    public ResourceMutation(double chance) {
        super(chance);
    }

    @Override
    protected void mutation(Schedule schedule) {
        schedule.mutateResource();
    }
}