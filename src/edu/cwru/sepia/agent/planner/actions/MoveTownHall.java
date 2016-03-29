package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.UnitInfo;

/**
 * Created by Steven on 3/29/2016.
 */
public class MoveTownHall extends MoveAction {

    public UnitInfo townHall = null;

    public MoveTownHall(UnitInfo peasant, UnitInfo townHall) {
        this.peasant = peasant;
        this.targetPosition = townHall.location;
        this.townHall = townHall;
        this.cost = peasant.location.chebyshevDistance(townHall.location);
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        if (peasant.type == UnitInfo.UnitType.PEASANT && townHall.type == UnitInfo.UnitType.TOWNHALL) {
            Position pos = this.getClosestAdjacentToTarget(state);
            return null == pos;
        }
        return false;
    }
}
