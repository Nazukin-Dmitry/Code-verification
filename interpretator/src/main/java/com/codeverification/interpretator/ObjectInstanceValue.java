package com.codeverification.interpretator;

import java.util.Map;

import com.codeverification.compiler.ClassDefinition;
import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class ObjectInstanceValue extends AbstractValue {

    private ClassDefinition classDefinition;
    private Map<String, AbstractValue> properties;

    @Override
    public DataType getType() {
        return DataType.OBJECT_INSTANCE;
    }

    @Override
    public void parse(String value) {

    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    public AbstractValue getProperty(String name) {
        return null;
    }

}
