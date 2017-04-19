package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class BoolValue extends AbstractValue {
    private Boolean value;

    public BoolValue(Boolean value) {
        this.value = value;
    }

    public BoolValue() {
    }

    @Override
    public DataType getType() {
        return DataType.BOOL;
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    @Override
    public Boolean asBool() {
        return value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {
        this.value = Boolean.parseBoolean(value);
    }

    @Override
    public String toString() {
        return "BoolValue{" +
                "value=" + value +
                '}';
    }
}
