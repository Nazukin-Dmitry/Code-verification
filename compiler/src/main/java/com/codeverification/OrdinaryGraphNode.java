package com.codeverification;

/**
 * Created by 1 on 08.04.2017.
 */
public class OrdinaryGraphNode<T> extends GraphNode<T> {
    GraphNode<T> nextNode;

    public GraphNode<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(GraphNode<T> nextNode) {
        this.nextNode = nextNode;
    }
}
