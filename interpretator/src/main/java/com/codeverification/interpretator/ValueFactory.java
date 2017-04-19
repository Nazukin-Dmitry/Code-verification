package com.codeverification.interpretator;

import com.codeverification.compiler.CodeGenerationVisitor.Const;
import com.codeverification.compiler.DataType;

/**
 * @author Dmitrii Nazukin
 */
public class ValueFactory {

    public static AbstractValue get(Const value){
        AbstractValue result;
        switch(value.getType()) {
            case BOOL:
                result = new BoolValue(Boolean.parseBoolean(value.getConstName()));
                result.setConst(true);
                break;
            case STRING:
                String str = value.getConstName().substring(1, value.getConstName().length()-1);
                result = new StringValue(str);
                result.setConst(true);
                break;
            case CHAR:
                Character ch = value.getConstName().charAt(1);
                result = new CharValue(ch);
                result.setConst(true);
                break;
            case INT:
                Integer i = Integer.parseInt(value.getConstName());
                result = new IntValue(i);
                result.setConst(true);
                break;
            default:
                throw new RuntimeException();
        }
        result.setRaw(value.getConstName());
        return result;
    }

    public static AbstractValue getEmpty(DataType dataType) {
        AbstractValue result;
        switch (dataType){
            case INT:
                result = new IntValue();
                break;
            case BOOL:
                result = new BoolValue();
                break;
            case BYTE:
                result = new ByteValue();
                break;
            case CHAR:
                result = new CharValue();
                break;
            case LONG:
                result = new LongValue();
                break;
            case UINT:
                result = new UIntValue();
                break;
            case ULONG:
                result = new ULongValue();
                break;
            case STRING:
                result = new StringValue();
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }

    public static AbstractValue getValue(DataType dataType, String raw) {
        AbstractValue result;
        switch (dataType){
            case INT:
                result = new IntValue();
                break;
            case BOOL:
                result = new BoolValue();
                break;
            case BYTE:
                result = new ByteValue();
                break;
            case CHAR:
                result = new CharValue();
                break;
            case LONG:
                result = new LongValue();
                break;
            case UINT:
                result = new UIntValue();
                break;
            case ULONG:
                result = new ULongValue();
                break;
            case STRING:
                result = new StringValue();
                break;
            default:
                throw new RuntimeException();
        }
        result.parse(raw);
        return result;
    }
}
