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
    public UnitInfo peasant = null; // the peasant moving
    public Position targetPosition = null; // the position they are moving to
    public double cost = 0; // the estimated cost of going to this position

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state); // create a copy state and apply changes
        UnitInfo newUnit = newState.units.get(peasant.unitID);
        newUnit.location = new Position(targetPosition);
        newState.actions.add(this);
        return newState;
    }

    /**
     * Used to determine if an open adjacent position exists.
     * @param state the curent game state
     * @return the position of the closest position adjacent to the stored target position
     */
    public Position getClosestAdjacentToTarget(GameState state) {
        // store distance for comparison, and position itself
        double closestDistance = Integer.MAX_VALUE;
        Position closestPos = null;
        List<Position> possibleLocs = targetPosition.getAdjacentPositions();
        for (Position position: possibleLocs) {
            double dist = position.euclideanDistance(peasant.location);
            // choose first position seen
            if (closestPos == null && position.inBounds(state.xExtent, state.yExtent) && notOccupied(position, state)) {
                closestPos = position;
                closestDistance = position.euclideanDistance(peasant.location);
            }else if (position.inBounds(state.xExtent, state.yExtent) && notOccupied(position, state) && dist < closestDistance) {
                //else, replace position if this one is closer to the unit going there
                closestPos = position;
                closestDistance = dist;
            }
        }
        return closestPos;
    }

    /**
     * Checks if a position has a unit or resource on it
     * @param pos the position in question
     * @param state the state that we will extract other positions from
     * @return
     */
    private boolean notOccupied(Position pos, GameState state) {
        // iterate over all units, mines, and trees to make sure there's nothing on this spot
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

    @Override
    public double getCost() {
        return this.cost;
    }

    @Override
    public int getUnitID() {
        return peasant.unitID;
    }
}
