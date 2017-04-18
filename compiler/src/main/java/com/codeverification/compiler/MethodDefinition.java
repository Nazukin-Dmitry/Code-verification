package com.codeverification.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitrii Nazukin
 */
public class MethodDefinition implements Serializable {

    private static final long serialVersionUID = 1;

    private MethodSignature methodSignature;
    private List<String> funcs = new ArrayList<>();
    private List<String> consts = new ArrayList<>();
    private int varsCount;
    private List<Command> commands = new ArrayList<>();

    public List<String> getFuncs() {
        return funcs;
    }

    public List<String> getConsts() {
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
}
