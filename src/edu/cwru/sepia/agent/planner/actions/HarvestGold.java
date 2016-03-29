package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.ResourceInfo;
import edu.cwru.sepia.agent.planner.UnitInfo;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;

/**
 * Created by Steven on 3/28/2016.
 */
public class HarvestGold extends HarvestAction{

    public UnitInfo peasant = null;
    public ResourceInfo resource = null;

    public HarvestGold(UnitInfo unit, ResourceInfo resource) {
        this.peasant = unit;
        this.resource = resource;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return (peasant.type == UnitInfo.UnitType.PEASANT
                && !peasant.hasResources() && resource.type == ResourceNode.Type.GOLD_MINE
                && resource.hasResources() && peasant.location.isAdjacent(resource.position));
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);
        UnitInfo newUnit = state.units.get(peasant.unitID);
        newUnit.cargo = ResourceType.GOLD;
        newUnit.amount = 100;
        ResourceInfo newResource = state.mines.get(resource.resourceID);
        newResource.capacity -= 100;
        newState.actions.add(this);
        return newState;
    }
}
