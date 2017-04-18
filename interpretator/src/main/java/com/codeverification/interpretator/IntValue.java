package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class IntValue implements Value {

    private Integer value;

    @Override
    public DataType getType() {
        return DataType.INT;
    }

    @Override
    public String getRaw() {
        return null;
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    @Override
    public Integer asInt() {
        return value;
    }
}
