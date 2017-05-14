package com.codeverification.compiler;

import java.io.Serializable;

/**
 * @author Dmitrii Nazukin
 */
public class DataType implements Serializable {
    public static final String UNDEFINED = "undefined";
    public static final String BOOL = "bool";
    public static final String BYTE = "byte";
    public static final String INT = "int";
    public static final String UINT = "uint";
    public static final String LONG = "long";
    public static final String ULONG = "ulong";
    public static final String CHAR = "char";
    public static final String STRING = "string";
    public static final String OBJECT = "Object";

//    private static final DataType[] values = {UNDEFINED, BOOL, BYTE, INT, UINT, LONG, ULONG, CHAR, STRING, OBJECT};

    private String rawText;

    public DataType(String rawText) {
        this.rawText = rawText;
    }

    public static DataType getDataType(String rawName) {
//        for (DataType dataType : values) {
//            if (dataType.rawText.equals(rawName)) {
//                return dataType;
//            }
//        }
        return new DataType(rawName);
    }

    public String getRawText() {
        return rawText;
    }

    @Override
    public String toString() {
        return rawText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataType dataType = (DataType) o;

        return rawText != null ? rawText.equals(dataType.rawText) : dataType.rawText == null;
    }

    @Override
    public int hashCode() {
        return rawText != null ? rawText.hashCode() : 0;
    }
}
