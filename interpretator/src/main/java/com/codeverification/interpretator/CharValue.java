package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class CharValue implements Value {

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

    @Override
    public void setValue(String value) {
        this.value = value.charAt(1);
    }

    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }
}
