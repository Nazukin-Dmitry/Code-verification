package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class ByteValue implements Value {

    private Byte value;

    @Override
    public DataType getType() {
        return DataType.BYTE;
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
    public Byte asByte() {
        return value;
    }
}
