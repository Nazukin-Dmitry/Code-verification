package com.codeverification.interpretator;

import java.util.Map;

import com.codeverification.compiler.MethodDefinition;

/**
 * @author Dmitrii Nazukin
 */
public class Interpretator {

    Map<String, MethodDefinition> functions;

    public Interpretator(Map<String, MethodDefinition> functions) {
        this.functions = functions;
    }




}
