package com.codeverification.interpretator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeverification.compiler.Command;
import com.codeverification.compiler.MethodDefinition;
import com.codeverification.compiler.MethodSignature;

/**
 * @author Dmitrii Nazukin
 */
public class FuncExecutor {

    private Interpretator interpretator;

    private List<Value> vars;

    private List<Value> consts = new ArrayList<>();

    private MethodSignature methodSignature;

    private List<Command> commans = new ArrayList<>();

    private int currentCommand = 0;

    private Map<Integer, Value> registers = new HashMap<>();

    private List<String> funcs = new ArrayList<>();

    public FuncExecutor(List<Value> args, MethodDefinition methodDefinition, Interpretator interpretator) {
        this.interpretator = interpretator;
        vars = new ArrayList<>(methodDefinition.getVarsCount());
        vars.add(null);
        for (Value arg : args) {
            vars.add(arg);
        }
        for ()
    }
}
