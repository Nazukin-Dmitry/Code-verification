package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

import static com.codeverification.compiler.DataType.LONG;

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

    public LongValue(long value) {
        this.value = value;
    }


    @Override
    public DataType getType() {
        return DataType.getDataType(LONG);
    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public String asString() {
        return value.toString();
    }



    public Long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }



    @Override
    public String toString() {
        return "LongValue{" +
                "value=" + value +
                '}';
    }

    @Override
    public void parse(String value) {
        if (value.startsWith("0b") || value.startsWith("0B")) {
            String bV = value.toLowerCase().replaceAll("0b", "");
            this.value = Long.valueOf(bV, 2);
        } else if (value.startsWith("0x") || value.startsWith("0X")) {
            String bX = value.toLowerCase().replaceAll("0x", "");
            this.value = Long.valueOf(bX, 16);
        } else {
            this.value = Long.valueOf(value);
        }
    }


}
