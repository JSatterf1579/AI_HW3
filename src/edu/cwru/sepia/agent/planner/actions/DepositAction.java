package edu.cwru.sepia.agent.planner.actions;

/**
 * Created by Steven on 3/29/2016.
 */
public abstract class DepositAction implements StripsAction {

    public double cost = 1;

    @Override
    public double getCost() {
        return cost;
    }
}
