package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

import static com.codeverification.compiler.DataType.INT;

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
        return DataType.getDataType(INT);
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

//    @Override
//    public Integer asInt() {
//        return value;
//    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public void parse(String value) {
        if (value.startsWith("0b") || value.startsWith("0B")) {
            String bV = value.toLowerCase().replaceAll("0b", "");
            this.value = Integer.valueOf(bV, 2);
        } else if (value.startsWith("0x") || value.startsWith("0X")) {
            String bX = value.toLowerCase().replaceAll("0x", "");
            this.value = Integer.valueOf(bX, 16);
        } else {
            this.value = Integer.valueOf(value);
        }
    }

//    @Override
//    public Byte asByte() {
//        if (isConst()) {
//            return value.byteValue();
//        } else {
//            return super.asByte();
//        }
//    }

    @Override
    public long asLong() {
        if (isConst()) {
            return value.longValue();
        } else {
            return super.asLong();
        }
    }

    @Override
    public String toString() {
        return "IntValue{" + "value=" + value + '}';
    }

    @Override
    public AbstractValue getCopy() {
        return null;
    }
}
