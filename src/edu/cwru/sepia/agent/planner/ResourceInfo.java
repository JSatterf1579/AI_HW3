package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by Joseph on 3/28/2016.
 */
public class ResourceInfo {

    public int resourceID;
    public ResourceNode.Type type;
    public Position position;
    public int capacity;


    public ResourceInfo(ResourceNode.ResourceView view) {
        resourceID = view.getID();
        type = view.getType();
        position = new Position(view.getXPosition(), view.getYPosition());
        capacity = view.getAmountRemaining();
    }

    public ResourceInfo(ResourceInfo info) {
        resourceID = info.resourceID;
        type = info.type;
        position = new Position(info.position);
        capacity = info.capacity;
    }

    public boolean hasResources() {
        return capacity > 0;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(o instanceof ResourceInfo) {
            ResourceInfo other = (ResourceInfo) o;
            return this.type == other.type && this.position.equals(other.position) && this.capacity == other.capacity;
        } else {
            return false;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Res");
        sb.append(resourceID);
        sb.append(position.toString());
        if (type == ResourceNode.Type.GOLD_MINE) {
            sb.append("G");
        } else {
            sb.append("W");
        }
        sb.append(capacity);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
