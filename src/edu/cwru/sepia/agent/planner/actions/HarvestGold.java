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
        UnitInfo newUnit = newState.units.get(peasant.unitID);
        newUnit.cargo = ResourceType.GOLD;
        newUnit.amount = 100;
        newUnit.currentAction = UnitInfo.HeuristicAction.PICKING_UP_GOLD;
        ResourceInfo newResource = newState.mines.get(resource.resourceID);
        newResource.capacity -= 100;
        newState.actions.add(this);
        return newState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HarvestGold(");
        sb.append(peasant.unitID);
        sb.append(", ");
        sb.append(resource.position.x);
        sb.append(", ");
        sb.append(resource.position.y);
        sb.append(")");
        return sb.toString();
    }
}
