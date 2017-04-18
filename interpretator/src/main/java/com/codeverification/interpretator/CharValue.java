package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class CharValue implements Value {

    private Character value;

    @Override
    public DataType getType() {
        return DataType.CHAR;
    }

    @Override
    public String getRaw() {
        return null;
    }

    @Override
    public Character asChar() {
        return value;
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }
}
