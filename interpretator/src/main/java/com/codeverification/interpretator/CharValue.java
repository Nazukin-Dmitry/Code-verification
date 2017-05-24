package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

import static com.codeverification.compiler.DataType.CHAR;

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
    public CharValue(char value) {
        this.value = value;
    }


    @Override
    public DataType getType() {
        return DataType.getDataType(CHAR);
    }

    @Override
    public char asChar() {
        return value;
    }

    @Override
    public int compareTo(Value value) {
        return this.value.compareTo(value.asChar());
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

    @Override
    public String asString() {
        return value.toString();
    }
}
