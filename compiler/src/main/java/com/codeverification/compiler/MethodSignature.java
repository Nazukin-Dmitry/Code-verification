package com.codeverification.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitrii Nazukin
 */
public class MethodSignature implements Serializable {

    private static final long serialVersionUID = 2;

    private String funcName;
    private int argCount;
    private List<DataType> argsType;
    private DataType returnType = DataType.UNDEFINED;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodSignature that = (MethodSignature) o;

        return funcName != null ? funcName.equals(that.funcName) : that.funcName == null;
    }

    @Override
    public int hashCode() {
        int result = funcName != null ? funcName.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "MethodSignature{" +
                "funcName='" + funcName + '\'' +
                ", argCount=" + argCount +
                ", argsType=" + argsType.toString() +
                ", returnType=" + returnType +
                '}';
    }
}
