package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.UnitInfo;

/**
 * Created by Steven on 3/31/2016.
 */
public class BuildAction implements StripsAction{

    int newUnitID = 0;

    @Override
    public double getCost() {
        return 0;
    }

    @Override
    public int getUnitID() {
        return newUnitID;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return state.currentGold >= 400 && (state.foodCap - state.food) >= 1;
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);
        UnitInfo newPeasant = new UnitInfo(getAUnit(state));
        UnitInfo townHall = getATownHall(state);
        newPeasant.unitID = getNewID(state);
        newPeasant.location = new Position(townHall.location);
        newPeasant.cargo = null;
        newPeasant.amount = 0;
        newPeasant.currentAction = UnitInfo.HeuristicAction.IDLE;
        newUnitID = newPeasant.unitID;
        newState.units.put(newUnitID, newPeasant);
        newState.food += 1;
        return newState;
    }

    private UnitInfo getAUnit(GameState state) {
        int someID = state.units.keySet().iterator().next();
        return state.units.get(someID);
    }

    private int getNewID(GameState state) {
        int i = 0;
        while (!state.units.keySet().contains(i)) {
            i++;
        }
        return i;
    }

    private UnitInfo getATownHall(GameState state) {
        int someID = state.townHalls.keySet().iterator().next();
        return state.townHalls.get(someID);
    }
}
