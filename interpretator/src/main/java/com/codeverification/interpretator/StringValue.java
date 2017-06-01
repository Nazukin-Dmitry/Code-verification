package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

import static com.codeverification.compiler.DataType.STRING;

/**
 * @author Dmitrii Nazukin
 */
public class StringValue extends AbstractValue {

    private String value;

    public StringValue() {
    }

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.getDataType(STRING);
    }

    @Override
    public int compareTo(Value value) {
        return this.value.compareTo(value.asString());
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public long asLong() {
        return new Long(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {

    }

    @Override
    public String toString() {
        return "StringValue{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public AbstractValue getCopy() {
        return new StringValue(value);
    }
}
