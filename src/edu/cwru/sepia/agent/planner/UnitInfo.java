package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

/**
 * Created by Joseph on 3/28/2016.
 */
public class UnitInfo {

    public UnitType type;

    public Position location;

    public ResourceType cargo;
    public int amount;

    public UnitInfo(UnitView unit) {
        if(unit.getTemplateView().getName().equals("Peasant")) {
            type = UnitType.PEASANT;
        } else if(unit.getTemplateView().getName().equals("TownHall")) {
            type = UnitType.TOWNHALL;
        }

        location = new Position(unit.getXPosition(), unit.getYPosition());
        cargo = unit.getCargoType();
        amount = unit.getCargoAmount();
    }

    public UnitInfo(UnitInfo unit) {
        type = unit.type;
        location = new Position(unit.location);
        cargo = unit.cargo;
        amount = unit.amount;
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
}

