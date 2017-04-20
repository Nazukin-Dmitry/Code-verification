package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class LongValue extends AbstractValue {

    private Long value;

    public LongValue() {
    }

    public LongValue(Long value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.LONG;
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    @Override
    public Long asLong() {
        return value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {
        this.value = Long.valueOf(value);
    }

    @Override
    public String toString() {
        return "LongValue{" +
                "value=" + value +
                '}';
    }


}
