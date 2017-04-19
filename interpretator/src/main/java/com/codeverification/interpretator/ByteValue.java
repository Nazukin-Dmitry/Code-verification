package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class ByteValue extends AbstractValue {

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

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }

    public ByteValue(Byte value) {
        this.value = value;
    }

    public ByteValue() {
    }

    @Override
    public void parse(String value) {
        this.value = Byte.parseByte(value);
    }

    @Override
    public String toString() {
        return "ByteValue{" +
                "value=" + value +
                '}';
    }
}
