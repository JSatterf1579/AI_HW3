package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.ResourceInfo;
import edu.cwru.sepia.agent.planner.UnitInfo;
import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by Steven on 3/28/2016.
 */
public class MoveForest extends MoveAction{

    public ResourceInfo resource = null;

    public MoveForest(UnitInfo peasant, ResourceInfo resource) {
        this.peasant = peasant;
        this.resource = resource;
        this.targetPosition = resource.position;
        this.cost = peasant.location.chebyshevDistance(resource.position);
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        if (peasant.type == UnitInfo.UnitType.PEASANT && resource.type == ResourceNode.Type.TREE) {
            Position pos = this.getClosestAdjacentToTarget(state);
            return null != pos;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoveForest(");
        sb.append(peasant.unitID);
        sb.append(", ");
        sb.append(resource.position.x);
        sb.append(", ");
        sb.append(resource.position.y);
        sb.append(")");
        return sb.toString();
    }
}
