package com.codeverification.compiler;

import com.codeverification.compiler.CodeGenerationVisitor.Const;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Dmitrii Nazukin
 */
public class MethodDefinition implements Serializable {

    private static final long serialVersionUID = 1;

    private MethodSignature methodSignature;
    private List<String> funcs = new ArrayList<>();
    private List<Const> consts = new ArrayList<>();
    private int varsCount;
    private List<Command> commands = new ArrayList<>();

    private boolean isNative;
    private String libraryName;

    public List<String> getFuncs() {
        return funcs;
    }

    public List<Const> getConsts() {
        return consts;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(MethodSignature methodSignature) {
        this.methodSignature = methodSignature;
    }

    public int getVarsCount() {
        return varsCount;
    }

    public void setVarsCount(int varsCount) {
        this.varsCount = varsCount;
    }

    public boolean isNative() {
        return isNative;
    }

    public void setNative(boolean aNative) {
        isNative = aNative;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(".methodSignature").append("\n");
        stringBuilder.append(methodSignature).append("\n");
        if (isNative) {
            stringBuilder.append("from " + libraryName).append("\n");
        } else {
            stringBuilder.append(".funcs").append("\n");
            int i = 0;
            for (String func : funcs) {
                stringBuilder.append(i++).append(":").append(func).append("\n");
            }
            stringBuilder.append(".vars_count").append("\n");
            stringBuilder.append(varsCount).append("\n");
            stringBuilder.append(".consts").append("\n");
            i = 0;
            for (Const e : consts) {
                stringBuilder.append(i).append(":").append(e).append("\n");
                i++;
            }
            stringBuilder.append(".programm").append("\n");
            IntStream.range(0, commands.size()).forEach(idx ->
                    stringBuilder.append(idx + ": " + commands.get(idx)).append("\n"));
        }
        return stringBuilder.toString();
    }
}
