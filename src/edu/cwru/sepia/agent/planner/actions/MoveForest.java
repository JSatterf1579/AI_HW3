package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.ResourceInfo;
import edu.cwru.sepia.agent.planner.UnitInfo;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.Unit;

import java.util.List;

/**
 * Created by Steven on 3/28/2016.
 */
public class MoveForest implements StripsAction{

    public UnitInfo peasant = null;
    public ResourceInfo resource = null;

    public MoveForest(UnitInfo peasant, ResourceInfo resource) {
        this.peasant = peasant;
        this.resource = resource;
    }

    @Override
    public boolean preconditionsMet(GameState state) { //TODO check for open adjacent position!!!
        return (peasant.type == UnitInfo.UnitType.PEASANT && resource.type == ResourceNode.Type.TREE);
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);
        UnitInfo newUnit = getNewUnit(state.units);
        newUnit.location = getClosestAdjacentToTarget(state);
        newState.actions.add(this);
        return newState;
    }

    private UnitInfo getNewUnit(List<UnitInfo> oldUnits) {
        for (UnitInfo oldUnit: oldUnits) {
            if (oldUnit.equals(peasant)) {
                return oldUnit;
            }
        }
        return null;
    }

    private Position getClosestAdjacentToTarget(GameState state) {
        double closestDistance = Integer.MAX_VALUE;
        Position closestPos = null;
        List<Position> possibleLocs = resource.position.getAdjacentPositions();
        for (Position position: possibleLocs) {
            double dist = position.euclideanDistance(peasant.location);
            if (closestPos == null && position.inBounds(state.xExtent, state.yExtent) && notOccupied(position, state)) {
                closestPos = position;
                closestDistance = position.euclideanDistance(peasant.location);
            }
            if (position.inBounds(state.xExtent, state.yExtent) && notOccupied(position, state) && dist < closestDistance) {
                closestPos = position;
                closestDistance = dist;
            }
        }
        return closestPos;
    }

    private boolean notOccupied(Position pos, GameState state) {
        for (UnitInfo unit: state.units) {
            if (unit.location.equals(pos)) {
                return false;
            }
        }
        for (ResourceInfo resource: state.mines) {
            if (resource.position.equals(pos)) {
                return false;
            }
        }
        for (ResourceInfo resource: state.trees) {
            if (resource.position.equals(pos)) {
                return false;
            }
        }
        return true;
    }
}
