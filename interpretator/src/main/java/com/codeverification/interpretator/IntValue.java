package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class IntValue extends AbstractValue {

    private Integer value;

    public IntValue() {
    }

    public IntValue(Integer value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.INT;
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
    public Integer asInt() {
        return value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {
        this.value = Integer.valueOf(value);
    }

    @Override
    public Byte asByte() {
        if (isConst()) {
            return Byte.valueOf(getRaw());
        } else {
            return super.asByte();
        }
    }

    @Override
    public Long asLong() {
        if (isConst()) {
            return Long.valueOf(getRaw());
        } else {
            return super.asLong();
        }
    }

    @Override
    public String toString() {
        return "IntValue{" +
                "value=" + value +
                '}';
    }
}
