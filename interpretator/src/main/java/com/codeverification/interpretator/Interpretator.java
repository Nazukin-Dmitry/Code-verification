package com.codeverification.interpretator;

import java.util.Map;

import com.codeverification.compiler.MethodDefinition;

/**
 * @author Dmitrii Nazukin
 */
public class Enterpretator {

    Map<String, MethodDefinition> functions;

    public Enterpretator(Map<String, MethodDefinition> functions) {
        this.functions = functions;
    }


}
