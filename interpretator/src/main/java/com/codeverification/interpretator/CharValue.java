package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class CharValue extends AbstractValue {

    private Character value;

    public CharValue() {
    }

    public CharValue(Character value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.CHAR;
    }

    @Override
    public Character asChar() {
        return value;
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {
//        this.value = Byte.parseByte(value);
    }

    @Override
    public String toString() {
        return "CharValue{" +
                "value=" + value +
                '}';
    }
}
