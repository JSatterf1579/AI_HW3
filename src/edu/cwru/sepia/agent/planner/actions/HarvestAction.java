package edu.cwru.sepia.agent.planner.actions;

/**
 * Created by Steven on 3/28/2016.
 */
public abstract class HarvestAction implements StripsAction {

    public double cost = 1;

    @Override
    public double getCost() {
        return cost;
    }
}