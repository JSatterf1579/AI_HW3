package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.UnitInfo;

/**
 * Created by Steven on 3/29/2016.
 */
public abstract class DepositAction implements StripsAction {


    public UnitInfo peasant = null;
    public UnitInfo townHall = null;
    public double cost = 1;

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public int getUnitID() {
        return peasant.unitID;
    }
}
