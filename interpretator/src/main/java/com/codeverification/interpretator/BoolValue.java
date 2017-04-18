package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class BoolValue implements Value {
    private Boolean value;

    @Override
    public DataType getType() {
        return DataType.BOOL;
    }

    @Override
    public String getRaw() {
        return value.toString();
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    @Override
    public Boolean asBool() {
        return value;
    }
}
