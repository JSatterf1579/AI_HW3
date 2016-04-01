package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;

import java.util.List;

/**
 * Created by Steven on 3/31/2016.
 */
public class CombinationAction implements StripsAction {

    public List<StripsAction> actions;

    public CombinationAction(List<StripsAction> actions) {
        this.actions = actions;
    }

    @Override
    public double getCost() {
        double cost = 0;
        for (StripsAction action: actions) {
            cost += action.getCost();
        }
        return cost;
    }

    @Override
    public int getUnitID() {
        return -1;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        boolean subConditionsMet = true;
        for (StripsAction action: actions) {
            subConditionsMet = subConditionsMet && action.preconditionsMet(state);
        }
        return subConditionsMet;
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);
        for (StripsAction action: actions) {
            action.apply(newState);
        }
        newState.actions.add(this);
        return newState;
    }
}
