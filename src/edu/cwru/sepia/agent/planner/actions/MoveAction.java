package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.ResourceInfo;
import edu.cwru.sepia.agent.planner.UnitInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Steven on 3/28/2016.
 */
public abstract class MoveAction implements StripsAction{
    public UnitInfo peasant = null;
    public ResourceInfo resource = null;
    public double cost = 0;

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);
        UnitInfo newUnit = state.units.get(peasant.unitID);
        newUnit.location = getClosestAdjacentToTarget(state);
        newState.actions.add(this);
        return newState;
    }

    public Position getClosestAdjacentToTarget(GameState state) {
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
        for (Map.Entry<Integer,UnitInfo> entry: state.units.entrySet()) {
            if (entry.getValue().location.equals(pos)) {
                return false;
            }
        }
        for (Map.Entry<Integer,ResourceInfo> entry: state.mines.entrySet()) {
            if (entry.getValue().position.equals(pos)) {
                return false;
            }
        }
        for (Map.Entry<Integer,ResourceInfo> entry: state.trees.entrySet()) {
            if (entry.getValue().position.equals(pos)) {
                return false;
            }
        }
        return true;
    }
}
