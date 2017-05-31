package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

import static com.codeverification.compiler.DataType.BYTE;

/**
 * @author Dmitrii Nazukin
 */
public class ByteValue extends AbstractValue {

    private Byte value;

    @Override
    public DataType getType() {
        return DataType.getDataType(BYTE);
    }

    @Override
    public int compareTo(Value value) {
        return 0;
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

    @Override
    public AbstractValue getCopy() {
        return null;
    }
}
