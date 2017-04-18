package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class UIntValue implements Value {

    private Integer value;

    @Override
    public DataType getType() {
        return DataType.UINT;
    }

    @Override
    public String getRaw() {
        return null;
    }

    @Override
    public Integer asUInt() {
        return value;
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}