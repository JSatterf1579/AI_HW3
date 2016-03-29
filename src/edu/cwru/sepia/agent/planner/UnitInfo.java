package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

/**
 * Created by Joseph on 3/28/2016.
 */
public class UnitInfo {

    public int unitID;

    public UnitType type;

    public Position location;

    public ResourceType cargo;
    public int amount;
    public HeuristicAction currentAction;

    public UnitInfo(UnitView unit) {
        if(unit.getTemplateView().getName().equals("Peasant")) {
            type = UnitType.PEASANT;
        } else if(unit.getTemplateView().getName().equals("TownHall")) {
            type = UnitType.TOWNHALL;
        }

        unitID = unit.getID();

        location = new Position(unit.getXPosition(), unit.getYPosition());
        cargo = unit.getCargoType();
        amount = unit.getCargoAmount();
        currentAction = HeuristicAction.IDLE;

    }

    public UnitInfo(UnitInfo unit) {
        unitID = unit.unitID;
        type = unit.type;
        location = new Position(unit.location);
        cargo = unit.cargo;
        amount = unit.amount;
        currentAction = unit.currentAction;
    }

    public boolean hasResources() {
        return cargo != null && amount != 0;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(o instanceof UnitInfo) {
            UnitInfo other = (UnitInfo)o;
            return other.location.equals(this.location) && other.cargo == this.cargo && other.amount == this.amount;
        } else {
            return false;
        }
    }


    public enum UnitType {
        PEASANT, TOWNHALL
    }

    public enum HeuristicAction {
        MOVING_TO_HALL, MOVING_TO_GOLD, MOVING_TO_WOOD, PICKING_UP_WOOD, PICKING_UP_GOLD, DEPOSITING_GOLD, DEPOSITING_WOOD, IDLE
    }

}

