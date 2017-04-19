package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class UIntValue extends AbstractValue {

    private Integer value;

    public UIntValue(Integer value) {
        this.value = value;
    }

    public UIntValue() {
    }

    @Override
    public DataType getType() {
        return DataType.UINT;
    }

    @Override
    public Integer asUInt() {
        return value;
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {

    }

    @Override
    public String toString() {
        return "UIntValue{" +
                "value=" + value +
                '}';
    }
}
