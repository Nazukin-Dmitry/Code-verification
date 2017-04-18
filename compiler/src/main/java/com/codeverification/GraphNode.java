package com.codeverification;

/**
 * Created by 1 on 08.04.2017.
 */
public abstract class GraphNode<T> {
    protected T nodeValue;

    public T getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(T node) {
        this.nodeValue = node;
    }

    abstract void setNextNode(GraphNode<T> nextNode);

    abstract GraphNode<T> getNextNode();
}
