package com.codeverification.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitrii Nazukin
 */
public class Command implements Serializable {

    private static final long serialVersionUID = 3;

    String name;

    List<Integer> args = new ArrayList<>();

    public Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getArgs() {
        return args;
    }

    public void setArgs(List<Integer> args) {
        this.args = args;
    }

    public void addArg(Integer... arg) {
        Collections.addAll(args, arg);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(" ");
        args.forEach(arg -> stringBuilder.append(arg).append(" "));
        return stringBuilder.toString();
    }
}
