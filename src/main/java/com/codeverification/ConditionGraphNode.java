package com.codeverification;

/**
 * Created by 1 on 08.04.2017.
 */
public class ConditionGraphNode<T> extends GraphNode<T> {
    T trueNextNode;
    T falseNextNode;

    public T getTrueNextNode() {
        return trueNextNode;
    }

    public void setTrueNextNode(T trueNextNode) {
        this.trueNextNode = trueNextNode;
    }

    public T getFalseNextNode() {
        return falseNextNode;
    }

    public void setFalseNextNode(T falseNextNode) {
        this.falseNextNode = falseNextNode;
    }

}
