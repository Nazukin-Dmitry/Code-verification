package com.codeverification;

/**
 * Created by 1 on 08.04.2017.
 */
public class OrdinaryGraphNode<T> extends GraphNode<T> {
    T nextNode;

    public T getNextNode() {
        return nextNode;
    }

    public void setNextNode(T nextNode) {
        this.nextNode = nextNode;
    }
}
