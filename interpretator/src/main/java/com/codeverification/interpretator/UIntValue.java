package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

import static com.codeverification.compiler.DataType.UINT;

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
        return DataType.getDataType(UINT);
    }

//    @Override
//    public Integer asUInt() {
//        return value;
//    }

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

    @Override
    public AbstractValue getCopy() {
        return new UIntValue(value);
    }
}
