package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to represent the state of the game after applying one of the avaiable actions. It will also
 * track the A* specific information such as the parent pointer and the cost and heuristic function. Remember that
 * unlike the path planning A* from the first assignment the cost of an action may be more than 1. Specifically the cost
 * of executing a compound action such as move can be more than 1. You will need to account for this in your heuristic
 * and your cost function.
 *
 * The first instance is constructed from the StateView object (like in PA2). Implement the methods provided and
 * add any other methods and member variables you need.
 *
 * Some useful API calls for the state view are
 *
 * state.getXExtent() and state.getYExtent() to get the map size
 *
 * I recommend storing the actions that generated the instance of the GameState in this class using whatever
 * class/structure you use to represent actions.
 */
public class GameState implements Comparable<GameState> {

    public int xExtent;
    public int yExtent;

    public int playernum;

    public int requiredGold;
    public int requiredWood;

    public int currentGold;
    public int currentWood;

    public Map<Integer, UnitInfo> units;
    public Map<Integer, UnitInfo> townHalls;

    public List<StripsAction> actions;

    public Map<Integer, ResourceInfo> mines;
    public Map<Integer, ResourceInfo> trees;

    public boolean canBuildPeasants;

    /**
     * Construct a GameState from a stateview object. This is used to construct the initial search node. All other
     * nodes should be constructed from the another constructor you create or by factory functions that you create.
     *
     * @param state The current stateview at the time the plan is being created
     * @param playernum The player number of agent that is planning
     * @param requiredGold The goal amount of gold (e.g. 200 for the small scenario)
     * @param requiredWood The goal amount of wood (e.g. 200 for the small scenario)
     * @param buildPeasants True if the BuildPeasant action should be considered
     */
    public GameState(State.StateView state, int playernum, int requiredGold, int requiredWood, boolean buildPeasants) {

        xExtent = state.getXExtent();
        yExtent = state.getYExtent();
        this.requiredGold = requiredGold;
        this.requiredWood = requiredWood;
        canBuildPeasants = buildPeasants;
        this.playernum = playernum;

        units = new HashMap<>();
        townHalls = new HashMap<>();
        List<Integer> unitIds = state.getUnitIds(playernum);
        for(Integer id : unitIds) {
            UnitView unit = state.getUnit(id);
            if(unit.getTemplateView().getName().equals("Peasant")) {
                units.put(unit.getID(), new UnitInfo(unit));
            } else if (unit.getTemplateView().getName().equals("TownHall")) {
                townHalls.put(unit.getID(), new UnitInfo(unit));
            }
        }

        mines = new HashMap<>();
        for(ResourceNode.ResourceView view : state.getResourceNodes(ResourceNode.Type.GOLD_MINE)) {
            mines.put(view.getID(), new ResourceInfo(view));
        }

        trees = new HashMap<>();
        for(ResourceNode.ResourceView view : state.getResourceNodes(ResourceNode.Type.TREE)) {
            trees.put(view.getID(), new ResourceInfo(view));
        }



        actions = new ArrayList<>();
    }

    /**
     * Creates a deep copy of the GameState for use in applying actions
     * @param oldState
     */
    public GameState(GameState oldState) {
        //Deep copy the state
        xExtent = oldState.xExtent;
        yExtent = oldState.yExtent;
        requiredWood = oldState.requiredWood;
        requiredGold = oldState.requiredGold;
        canBuildPeasants = oldState.canBuildPeasants;
        playernum = oldState.playernum;

        units = new HashMap<>();

        for(Integer i : oldState.units.keySet()) {
            units.put(i, new UnitInfo(oldState.units.get(i)));
        }

        townHalls = new HashMap<>();
        for(Integer i : oldState.townHalls.keySet()) {
            townHalls.put(i, new UnitInfo(oldState.townHalls.get(i)));
        }

        mines = new HashMap<>();
        for(Integer i : oldState.mines.keySet()) {
            mines.put(i, new ResourceInfo(oldState.mines.get(i)));
        }

        trees = new HashMap<>();
        for(Integer i: oldState.trees.keySet()) {
            trees.put(i, new ResourceInfo(oldState.trees.get(i)));
        }

        actions = new ArrayList<>();
        for(int i = 0; i < oldState.actions.size(); i++){
            actions.add(oldState.actions.get(i));
        }
    }

    /**
     * Unlike in the first A* assignment there are many possible goal states. As long as the wood and gold requirements
     * are met the peasants can be at any location and the capacities of the resource locations can be anything. Use
     * this function to check if the goal conditions are met and return true if they are.
     *
     * @return true if the goal conditions are met in this instance of game state.
     */
    public boolean isGoal() {
        if(currentGold >= requiredGold && currentWood >= requiredWood) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The branching factor of this search graph are much higher than the planning. Generate all of the possible
     * successor states and their associated actions in this method.
     *
     * @return A list of the possible successor states and their associated actions
     */
    public List<GameState> generateChildren() {

        List<GameState> children = new ArrayList<>();

        for(UnitInfo unit: units.values()) {
            //Generate all move to gold pairs
            for(ResourceInfo gold: mines.values()) {
                StripsAction move = new MoveMine(unit, gold);
                if(move.preconditionsMet(this)) {
                    children.add(move.apply(this));
                }

                //Generate all gold pickups
                StripsAction harvest = new HarvestGold(unit, gold);
                if(harvest.preconditionsMet(this)) {
                    children.add(harvest.apply(this));
                }

            }

            //Generate all move to wood pairs
            for(ResourceInfo wood: trees.values()) {
                StripsAction move = new MoveForest(unit, wood);
                if(move.preconditionsMet(this)) {
                    children.add(move.apply(this));
                }

                //Generate all wood pickups
                StripsAction harvest = new HarvestWood(unit, wood);
                if(harvest.preconditionsMet(this)) {
                    children.add(harvest.apply(this));
                }
            }

            //Generate all move to town hall pairs
            for(UnitInfo hall: townHalls.values()) {
                StripsAction move = new MoveTownHall(unit, hall);
                if(move.preconditionsMet(this)) {
                    children.add(move.apply(this));
                }

                //generate all gold dropoffs
                StripsAction dropGold = new DepositGold(unit, hall);
                if(dropGold.preconditionsMet(this)) {
                    children.add(dropGold.apply(this));
                }

                //generate all wood dropoffs
                StripsAction dropWood = new DepositWood(unit, hall);
                if(dropWood.preconditionsMet(this)) {
                    children.add(dropWood.apply(this));
                }

            }
        }


        return children;
    }

    /**
     * Write your heuristic function here. Remember this must be admissible for the properties of A* to hold. If you
     * can come up with an easy way of computing a consistent heuristic that is even better, but not strictly necessary.
     *
     * Add a description here in your submission explaining your heuristic.
     *
     * @return The value estimated remaining cost to reach a goal state from this state.
     */
    public double heuristic() {

        double heuristic = 0.0;

        //find largest distance between TH and Gold and TH and trees
        double largestTHGoldDistance = Double.MIN_VALUE;
        double largestTHWoodDistance = Double.MIN_VALUE;
        for(UnitInfo unit : townHalls.values()) {
            for(ResourceInfo mine : mines.values()) {
                double distance = unit.location.chebyshevDistance(mine.position);
                if(distance > largestTHGoldDistance) {
                    largestTHGoldDistance = distance;
                }
            }

            for(ResourceInfo tree: trees.values()) {
                double distance = unit.location.chebyshevDistance(tree.position);
                if(distance > largestTHWoodDistance) {
                    largestTHWoodDistance = distance;
                }
            }
        }

        //Lose value for amount of turns needed fulfil goal for gold
        heuristic += 2 * largestTHGoldDistance *((Math.max(requiredGold - currentGold, 0) / 100) / units.size()) * 10;

        //Same for wood
        heuristic += 2 * largestTHWoodDistance * ((Math.max(requiredWood - currentWood, 0) / 100) / units.size()) * 10;

        //Add value back for each unit carrying gold or wood, since they are half way done with the deposit
        int goldCarriers = 0;
        int woodCarriers = 0;
        for(UnitInfo unit : units.values()) {
            if(unit.cargo == ResourceType.GOLD) {
                goldCarriers++;
            } else if (unit.cargo == ResourceType.WOOD) {
                woodCarriers++;
            }
        }

        heuristic -= largestTHGoldDistance * goldCarriers * 10;
        heuristic -= largestTHWoodDistance * woodCarriers * 10;

        //Prefer units being closer to the TH, if possible
        for(UnitInfo unit : units.values()) {
            double maxDistance = Double.MIN_VALUE;
            for(UnitInfo th : townHalls.values()) {
                if(th.location.chebyshevDistance(unit.location) > maxDistance) {
                    maxDistance = th.location.chebyshevDistance(unit.location);
                }
            }
            heuristic += maxDistance;
        }

        //Bonuses for doing proper actions (turning in when you can, getting needed resources) and detriments for
        //doing un-needed tasks (collecting excess resources)
        for(UnitInfo unit : units.values()) {
            //Is gold still necessary?
            if(unit.currentAction == UnitInfo.HeuristicAction.MOVING_TO_GOLD || unit.currentAction == UnitInfo.HeuristicAction.PICKING_UP_GOLD) {
                if (currentGold < requiredGold) {
                    heuristic -= 1 - largestTHGoldDistance;
                } else {
                    //heuristic += 1;
                }
            }

            //Same for wood
            if(unit.currentAction == UnitInfo.HeuristicAction.MOVING_TO_WOOD || unit.currentAction == UnitInfo.HeuristicAction.PICKING_UP_WOOD) {
                if (currentWood < requiredWood) {
                    heuristic -= 1 - largestTHWoodDistance;
                } else {
                    //heuristic += 1;
                }
            }

            //Are you returning if you're carrying something, or still in the turn where you picked it up
            if(unit.cargo != null) {
                if (unit.currentAction == UnitInfo.HeuristicAction.MOVING_TO_HALL || unit.currentAction == UnitInfo.HeuristicAction.DEPOSITING_GOLD ||
                        unit.currentAction == UnitInfo.HeuristicAction.DEPOSITING_WOOD) {
                    heuristic -= 2;
                } else if(unit.currentAction == UnitInfo.HeuristicAction.PICKING_UP_GOLD || unit.currentAction == UnitInfo.HeuristicAction.PICKING_UP_WOOD) {
                    //Change nothing on the heuristic
                } else {
                    heuristic += 1;
                }
            }
        }

        return heuristic;
    }

    /**
     *
     * Write the function that computes the current cost to get to this node. This is combined with your heuristic to
     * determine which actions/states are better to explore.
     *
     * @return The current cost to reach this goal
     */
    public double getCost() {

        double cost = 0.0;
        for(StripsAction action : actions) {
            cost += action.getCost();
        }
        return cost;
    }

    /**
     * This is necessary to use your state in the Java priority queue. See the official priority queue and Comparable
     * interface documentation to learn how this function should work.
     *
     * @param o The other game state to compare
     * @return 1 if this state costs more than the other, 0 if equal, -1 otherwise
     */
    @Override
    public int compareTo(GameState o) {

        return Double.compare(this.heuristic() + this.getCost(), o.heuristic() + o.getCost());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameState gameState = (GameState) o;

        if (currentGold != gameState.currentGold) return false;
        if (currentWood != gameState.currentWood) return false;
        if (!units.equals(gameState.units)) return false;
        if (!townHalls.equals(gameState.townHalls)) return false;
        if (!mines.equals(gameState.mines)) return false;
        return trees.equals(gameState.trees);

    }

    @Override
    public int hashCode() {
        int result = currentGold;
        result = 31 * result + currentWood;
        result = 31 * result + units.hashCode();
        result = 31 * result + townHalls.hashCode();
        result = 31 * result + mines.hashCode();
        result = 31 * result + trees.hashCode();
        return result;
    }
}
