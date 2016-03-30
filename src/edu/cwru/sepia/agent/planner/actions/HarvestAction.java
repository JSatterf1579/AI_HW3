package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.ResourceInfo;
import edu.cwru.sepia.agent.planner.UnitInfo;

/**
 * Created by Steven on 3/28/2016.
 */
public abstract class HarvestAction implements StripsAction {

    public UnitInfo peasant = null;
    public ResourceInfo resource = null;
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
