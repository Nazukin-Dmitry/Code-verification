package com.codeverification.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.codeverification.Var3Parser.ArgDefContext;
import com.codeverification.Var3Parser.FuncSignatureContext;

/**
 * @author Dmitrii Nazukin
 */
public class MethodSignature implements Serializable {

    private static final long serialVersionUID = 2;

    private String funcName;
    private int argCount;
    private List<DataType> argsType;
    private DataType returnType = DataType.UNDEFINED;

    private boolean isGeneric;

    public MethodSignature(FuncSignatureContext funcSignatureContext) {
        this(funcSignatureContext.funcName.IDENTIFIER().getText(),
                funcSignatureContext.listArgDef().argDef().size());
        for (ArgDefContext arg : funcSignatureContext.listArgDef().argDef()) {
            argsType.add(DataType.getDataType(arg.typeRef().getText()));
        }
        if (funcSignatureContext.typeRef() != null) {
            returnType = DataType.getDataType(funcSignatureContext.typeRef().getText());
        } else {
            returnType = DataType.UNDEFINED;
        }
    }

    public MethodSignature(String funcName, int argCount) {
        this.funcName = funcName;
        this.argCount = argCount;
        argsType = new ArrayList<>(argCount);
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public int getArgCount() {
        return argCount;
    }

    public void setArgCount(int argCount) {
        this.argCount = argCount;
    }

    public DataType getReturnType() {
        return returnType;
    }

    public void setReturnType(DataType returnType) {
        this.returnType = returnType;
    }

    public List<DataType> getArgsType() {
        return argsType;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public void setGeneric(boolean generic) {
        isGeneric = generic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodSignature that = (MethodSignature) o;

        if (argCount != that.argCount) return false;
        if (funcName != null ? !funcName.equals(that.funcName) : that.funcName != null) return false;
        return argsType != null ? argsType.equals(that.argsType) : that.argsType == null;
    }

    @Override
    public int hashCode() {
        int result = funcName != null ? funcName.hashCode() : 0;
        result = 31 * result + argCount;
        result = 31 * result + (argsType != null ? argsType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodSignature{" +
                "funcName='" + funcName + '\'' +
                ", argCount=" + argCount +
                ", argsType=" + argsType +
                ", returnType=" + returnType +
                '}';
    }
}
