package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.UnitInfo;

/**
 * Created by Steven on 3/31/2016.
 */
public class BuildAction implements StripsAction{

    public int newUnitID = 0; // the ID of the unit being created
    public UnitInfo townHall = null; // the townhall doing the creation

    public BuildAction(UnitInfo townHall) {
        this.townHall = townHall;
    }

    @Override
    public double getCost() {
        return 0;
    }

    @Override
    public int getUnitID() {
        return townHall.unitID;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return state.currentGold >= 400 && (state.foodCap - state.food) >= 1 ;
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);
        UnitInfo newPeasant = new UnitInfo(getAUnit(newState));
        newPeasant.unitID = getNewID(newState);
        newPeasant.location = new Position(townHall.location);
        newPeasant.cargo = null;
        newPeasant.amount = 0;
        newPeasant.currentAction = UnitInfo.HeuristicAction.IDLE;
        newUnitID = newPeasant.unitID;
        newState.units.put(newUnitID, newPeasant);
        newState.currentGold -= 400;
        newState.food += 1;
        newState.actions.add(this);
        return newState;
    }

    /**
     * Retrieves a random peasant unit to serve as a base template
     * @param state the previous state to grab a unit from
     * @return
     */
    private UnitInfo getAUnit(GameState state) {
        int someID = state.units.keySet().iterator().next();
        return state.units.get(someID);
    }

    /**
     * Finds the first positive integer not taken as a Unit ID
     * @param state the state whose units we will compare with
     * @return
     */
    private int getNewID(GameState state) {
        int i = 1;
        while (state.units.keySet().contains(i)) {
            i++;
        }
        return i;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BuildAction(");
        sb.append(townHall.unitID);
        sb.append(", ");
        sb.append(newUnitID);
        sb.append(")");
        return sb.toString();
    }

}
