package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.ResourceInfo;
import edu.cwru.sepia.agent.planner.UnitInfo;

/**
 * Created by Steven on 3/28/2016.
 */
public abstract class HarvestAction implements StripsAction {

    public UnitInfo peasant = null; // the peasant harvesting a resource
    public ResourceInfo resource = null; // the resource being harvested
    public double cost = 1; // The cost for harvesting is always 1

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public int getUnitID() {
        return peasant.unitID;
    }
}
