package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.UnitInfo;
import edu.cwru.sepia.environment.model.state.ResourceType;

/**
 * Created by Steven on 3/28/2016.
 */
public class DepositGold extends DepositAction {

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
        UnitInfo newUnit = newState.units.get(peasant.unitID);
        newUnit.cargo = null;
        newUnit.amount = 0;
        newUnit.currentAction = UnitInfo.HeuristicAction.DEPOSITING_GOLD;
        newState.currentGold += 100;
        newState.actions.add(this);
        System.out.println("(" + newState.currentGold + "g," + newState.currentWood + "w)" );
        return newState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DepositGold(");
        sb.append(peasant.unitID);
        sb.append(", ");
        sb.append(townHall.location.x);
        sb.append(", ");
        sb.append(townHall.location.y);
        sb.append(")");
        return sb.toString();
    }
}
