package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class LongValue implements Value {

    private Long value;

    @Override
    public DataType getType() {
        return DataType.LONG;
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
    public Long asLong() {
        return value;
    }
}
