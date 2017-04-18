package com.codeverification;

/**
 * @author Dmitrii Nazukin
 */
public enum DataType {
    UNDEFINED("indefined"),
    BOOL("bool"),
    BYTE("byte"),
    INT("int"),
    UINT("uint"),
    LONG("long"),
    ULONG("ulong"),
    CHAR("char"),
    STRING("string");

    String rawText;

    DataType(String rawText) {
        this.rawText = rawText;
    }

    public static DataType getDataType(String rawName) {
        for (DataType dataType : values()) {
            if (dataType.rawText.equals(rawName)) {
                return dataType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return rawText;
    }
}
