package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;

import java.util.List;

/**
 * Created by Steven on 3/31/2016.
 * A StripsAction composed of more than one action.
 */
public class CombinationAction implements StripsAction {

    public List<StripsAction> actions; // the subactions that make up this multi-action move

    public CombinationAction(List<StripsAction> actions) {
        this.actions = actions;
    }

    @Override
    public double getCost() {
        double cost = Double.MIN_VALUE;
        for (StripsAction action: actions) {
            if (action.getCost() > cost) {
                cost = action.getCost();
            }
        }
        return cost;
    }

    @Override
    public int getUnitID() {
        return -1;
    }

    @Override
    public boolean preconditionsMet(GameState state) { // makes sure all states will have their preconditions met if performed sequentially
        for (StripsAction action: actions) {
            if(action.preconditionsMet(state)) {
                state = action.apply(state);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public GameState apply(GameState state) { // applies all actions to the state, one after another
        GameState newState = new GameState(state);
        for (StripsAction action: actions) {
            newState = action.apply(newState);
            newState.actions.remove(newState.actions.size()-1);
        }
        newState.actions.add(this);
        return newState;
    }

    public String toString() {
        String s = "----------{\n";
        for(StripsAction action : actions) {
            s = s + action.toString() + "\n";
        }
        s = s + "-----------}";
        return s;
    }
}
