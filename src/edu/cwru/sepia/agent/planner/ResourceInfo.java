package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;

/**
 * Created by Joseph on 3/28/2016.
 */
public class ResourceInfo {

    public ResourceNode.Type type;
    public Position position;
    public int capacity;


    public ResourceInfo(ResourceNode.ResourceView view) {
        type = view.getType();
        position = new Position(view.getXPosition(), view.getYPosition());
        capacity = view.getAmountRemaining();
    }

    public ResourceInfo(ResourceInfo info) {
        type = info.type;
        position = new Position(info.position);
        capacity = info.capacity;
    }

}
