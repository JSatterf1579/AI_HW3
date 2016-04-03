package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.util.Direction;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This is an outline of the PEAgent. Implement the provided methods. You may add your own methods and members.
 */
public class PEAgent extends Agent {

    // The plan being executed
    private Stack<StripsAction> plan = null;

    // maps the real unit Ids to the plan's unit ids
    // when you're planning you won't know the true unit IDs that sepia assigns. So you'll use placeholders (1, 2, 3).
    // this maps those placeholders to the actual unit IDs.
    private Map<Integer, Integer> peasantIdMap;
    private int townhallId;
    private int peasantTemplateId;
    private int latestNewUnit = 0;

    public PEAgent(int playernum, Stack<StripsAction> plan) {
        super(playernum);
        peasantIdMap = new HashMap<Integer, Integer>();
        this.plan = plan;
//        System.out.println(plan);

    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {
        // gets the townhall ID and the peasant ID
        for(int unitId : stateView.getUnitIds(playernum)) {
            Unit.UnitView unit = stateView.getUnit(unitId);
            String unitType = unit.getTemplateView().getName().toLowerCase();
            if(unitType.equals("townhall")) {
                townhallId = unitId;
            } else if(unitType.equals("peasant")) {
                peasantIdMap.put(unitId, unitId);
            }
        }

        // Gets the peasant template ID. This is used when building a new peasant with the townhall
        for(Template.TemplateView templateView : stateView.getTemplates(playernum)) {
            if(templateView.getName().toLowerCase().equals("peasant")) {
                peasantTemplateId = templateView.getID();
                break;
            }
        }

        return middleStep(stateView, historyView);
    }

    /**
     * This is where you will read the provided plan and execute it. If your plan is correct then when the plan is empty
     * the scenario should end with a victory. If the scenario keeps running after you run out of actions to execute
     * then either your plan is incorrect or your execution of the plan has a bug.
     *
     * You can create a SEPIA deposit action with the following method
     * Action.createPrimitiveDeposit(int peasantId, Direction townhallDirection)
     *
     * You can create a SEPIA harvest action with the following method
     * Action.createPrimitiveGather(int peasantId, Direction resourceDirection)
     *
     * You can create a SEPIA build action with the following method
     * Action.createPrimitiveProduction(int townhallId, int peasantTemplateId)
     *
     * You can create a SEPIA move action with the following method
     * Action.createCompoundMove(int peasantId, int x, int y)
     *
     * these actions are stored in a mapping between the peasant unit ID executing the action and the action you created.
     *
     * For the compound actions you will need to check their progress and wait until they are complete before issuing
     * another action for that unit. If you issue an action before the compound action is complete then the peasant
     * will stop what it was doing and begin executing the new action.
     *
     * To check an action's progress you can call getCurrentDurativeAction on each UnitView. If the Action is null nothing
     * is being executed. If the action is not null then you should also call getCurrentDurativeProgress. If the value is less than
     * 1 then the action is still in progress.
     *
     * Also remember to check your plan's preconditions before executing!
     */
    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
        // TODO: Implement me! (multiple actions/peasant build)
        updateUnitMapping(stateView);
        StripsAction action = plan.pop();
        Map<Integer, Action> retMap = new HashMap<>();

        //System.out.println(action.toString());

//        // wait for previous action to be done for this unit
//        if (actualUnit.getCurrentDurativeAction() != null) {
//            plan.push(action);
//            return retMap;
//        }


        if (action instanceof CombinationAction) {
            CombinationAction cAction = (CombinationAction)action;
            for (StripsAction subAction: cAction.actions) {
                if (!isUnitBusy(subAction.getUnitID(), stateView, historyView)) {
                    addSepiaAction(subAction, stateView, retMap);
                } else {
                    plan.push(subAction);
                }
            }
        } else {
            if (!isUnitBusy(action.getUnitID(), stateView, historyView)) {
                addSepiaAction(action, stateView, retMap);
            } else {
                plan.push(action);
            }
        }

        return retMap;
    }

    public boolean isUnitBusy(int unitID, State.StateView stateView, History.HistoryView historyView) {
        if (stateView.getTurnNumber() != 0) {
            if (!isAPeasant(unitID)) {
                return false;
            }
            Map<Integer, ActionResult> actionResults = historyView.getCommandFeedback(playernum, stateView.getTurnNumber() - 1);
            for (ActionResult result : actionResults.values()) {
                System.out.println(result.getFeedback());
                System.out.println(result.getAction());
                if (result.getAction().getUnitId() == peasantIdMap.get(unitID) && result.getFeedback() == ActionFeedback.INCOMPLETE || result.getFeedback() == ActionFeedback.FAILED) {
                    return true;
                } else if (result.getAction().getUnitId() == peasantIdMap.get(unitID) && result.getFeedback() != ActionFeedback.COMPLETED) {
//                    System.out.println("non - Incomplete/failed action");
                }
            }
        }
        return false;
    }

    public boolean isAPeasant(int ID) {
        return peasantIdMap.keySet().contains(ID);
    }

    private void updateUnitMapping(State.StateView state) {
        List<Integer> realPeasantIDs = new ArrayList<Integer>();
        for (Unit.UnitView unit: state.getAllUnits()) {
            if (unit.getTemplateView().getName().equals("Peasant")) {
                realPeasantIDs.add(unit.getID());
            }
        }
//        System.out.println(peasantIdMap.size());
//        System.out.println(realPeasantIDs.size());
        if (peasantIdMap.size() == realPeasantIDs.size()) {
            return;
        } else {
            for (Integer id: realPeasantIDs) {
                if (!peasantIdMap.containsValue(id)) {
                    peasantIdMap.put(latestNewUnit, id);
                }
            }
        }
    }

    /**
     * Returns a SEPIA version of the specified Strips Action.
     * @param action StripsAction
     * @return SEPIA representation of same action
     */
    private void addSepiaAction(StripsAction action, State.StateView stateView, Map<Integer, Action> retMap) {
        int stripsID = action.getUnitID();
        Unit.UnitView actualUnit = null;
        int actualID = 0;
        try {
            actualID = peasantIdMap.get(stripsID);
            actualUnit = stateView.getUnit(actualID);
        } catch (NullPointerException e){
//            System.out.println("Null Pointer for ActualID");
//            System.out.println(action);
//            System.out.println(peasantIdMap);
        }

        if (action instanceof  MoveAction) {
            MoveAction mAction = (MoveAction)action;
            retMap.put(actualID, Action.createCompoundMove(actualID, mAction.targetPosition.x, mAction.targetPosition.y));
        } else if (action instanceof DepositAction) {
            DepositAction dAction = (DepositAction)action;
            Direction dirToHall = new Position(actualUnit.getXPosition(), actualUnit.getYPosition()).getDirection(dAction.townHall.location);
            retMap.put(actualID, Action.createPrimitiveDeposit(actualID, dirToHall));
        }else if (action instanceof  HarvestAction) {
            HarvestAction hAction = (HarvestAction)action;
            Direction dirToHall = new Position(actualUnit.getXPosition(), actualUnit.getYPosition()).getDirection(hAction.resource.position);
            retMap.put(actualID, Action.createPrimitiveGather(actualID, dirToHall));
        } else if (action instanceof BuildAction) {
            actualID = action.getUnitID();
            BuildAction bAction = (BuildAction)action;
            retMap.put(actualID, Action.createPrimitiveBuild(actualID, peasantTemplateId));
            latestNewUnit = bAction.newUnitID;
        }
//        System.out.println(action);

    }

    @Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView) {

    }

    @Override
    public void savePlayerData(OutputStream outputStream) {

    }

    @Override
    public void loadPlayerData(InputStream inputStream) {

    }
}
