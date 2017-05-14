package com.codeverification.interpretator;

import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public interface Value extends Comparable<Value> {
    DataType getType();

    default Boolean asBool() {
        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'bool'");
    }

//    default Byte asByte() {
//        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'byte'");
//    }

//    default Integer asInt() {
//        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'int'");
//    }

//    default Integer asUInt() {
//        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'uint'");
//    }

    default Long asLong() {
        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'long'");
    }

//    default Long asULong() {
//        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'ulong'");
//    }

    default Character asChar() {
        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'char'");
    }

    default String asString() {
        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'string'");
    }

    default Object asObject() {
        throw new RuntimeException("Converting exception from " + getType().getRawText() + " to 'Object'");
    }

    default ObjectInstanceValue asObjectInstanceValue() {
        throw new RuntimeException("Converting exception");
    }

    void parse(String value);
}
