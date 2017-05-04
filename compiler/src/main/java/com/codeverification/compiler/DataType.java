package com.codeverification.compiler;

/**
 * @author Dmitrii Nazukin
 */
public enum DataType {
    UNDEFINED("undefined"),
    BOOL("bool"),
    BYTE("byte"),
    INT("int"),
    UINT("uint"),
    LONG("long"),
    ULONG("ulong"),
    CHAR("char"),
    STRING("string"),
    OBJECT("Object");

    private String rawText;

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

    public String getRawText() {
        return rawText;
    }

    @Override
    public String toString() {
        return rawText;
    }
}
