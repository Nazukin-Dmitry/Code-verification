package com.codeverification;

/**
 * Created by 1 on 08.04.2017.
 */
public class ConditionGraphNode<T> extends GraphNode<T> {
    private GraphNode<T> trueNextNode;
    private GraphNode<T> falseNextNode;

    private ConditionType type;

    public GraphNode<T> getTrueNextNode() {
        return trueNextNode;
    }

    public void setTrueNextNode(GraphNode<T> trueNextNode) {
        this.trueNextNode = trueNextNode;
    }

    public GraphNode<T> getFalseNextNode() {
        return falseNextNode;
    }

    public void setFalseNextNode(GraphNode<T> falseNextNode) {
        this.falseNextNode = falseNextNode;
    }

    @Override
    void setNextNode(GraphNode<T> nextNode) {

    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    @Override
    GraphNode<T> getNextNode() {
        return null;
    }

    public static enum ConditionType {
        IF, WHILE, DO_WHILE, DO_UNTIL;
    }
}
