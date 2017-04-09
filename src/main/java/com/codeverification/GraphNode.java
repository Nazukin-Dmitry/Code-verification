package com.codeverification;

/**
 * Created by 1 on 08.04.2017.
 */
public abstract class GraphNode<T> {
    protected T node;

    public T getNode() {
        return node;
    }

    public void setNode(T node) {
        this.node = node;
    }

    abstract void setNextNode(GraphNode<T> nextNode);

    abstract GraphNode<T> getNextNode();
}
