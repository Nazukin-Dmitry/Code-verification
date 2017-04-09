package com.codeverification;

/**
 * Created by 1 on 08.04.2017.
 */
public class ConditionGraphNode<T> extends GraphNode<T> {
    GraphNode<T> trueNextNode;
    GraphNode<T> falseNextNode;

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

    @Override
    GraphNode<T> getNextNode() {
        return null;
    }
}
