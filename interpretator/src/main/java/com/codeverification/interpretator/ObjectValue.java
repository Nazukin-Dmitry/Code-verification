package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

import static com.codeverification.compiler.DataType.OBJECT;

/**
 * Created by 1 on 03.05.2017.
 */
public class ObjectValue extends AbstractValue {

    private Object value;

    public ObjectValue() {
    }

    public ObjectValue(Object value) {
        this.value = value;
    }

    @Override
    public DataType getType() {
        return DataType.getDataType(OBJECT);
    }

    @Override
    public Object asObject() {
        return value;
    }

    @Override
    public void parse(String value) {
    }

    @Override
    public int compareTo(Value o) {
        return 0;
    }

    @Override
    public String toString() {
        return "ObjectValue{" +
                "value=" + value +
                '}';
    }
}
