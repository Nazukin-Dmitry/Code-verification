package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * Created by 1 on 18.04.2017.
 */
public class ULongValue extends AbstractValue {

    private Long value;

    public ULongValue() {
    }

    public ULongValue(Long value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.ULONG;
    }

    @Override
    public int compareTo(Value o) {
        return 0;
    }

//    @Override
//    public Long asULong() {
//        return value;
//    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {

    }

    @Override
    public String toString() {
        return "ULongValue{" +
                "value=" + value +
                '}';
    }
}
