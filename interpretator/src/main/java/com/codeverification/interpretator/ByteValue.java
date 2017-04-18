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

    @Override
    public void setValue(String value) {
        this.value = Byte.parseByte(value);
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }
}
