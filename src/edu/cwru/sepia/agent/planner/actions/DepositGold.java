package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.UnitInfo;
import edu.cwru.sepia.environment.model.state.ResourceType;

/**
 * Created by Steven on 3/28/2016.
 */
public class DepositGold implements StripsAction {
    public UnitInfo peasant = null;
    public UnitInfo townHall = null;
    public double cost = 1;

    public DepositGold(UnitInfo unit, UnitInfo townHall) {
        this.peasant = unit;
        this.townHall = townHall;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return (peasant.type == UnitInfo.UnitType.PEASANT
                && peasant.hasResources() && townHall.type == UnitInfo.UnitType.TOWNHALL
                && peasant.cargo == ResourceType.GOLD && peasant.location.isAdjacent(townHall.location));
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);
        UnitInfo newUnit = state.units.get(peasant.unitID);
        newUnit.cargo = null;
        newUnit.amount = 0;
        newState.currentGold += 100;
        newState.actions.add(this);
        return newState;
    }
}
