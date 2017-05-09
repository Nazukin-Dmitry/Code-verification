package com.codeverification.compiler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 1 on 09.05.2017.
 */
public class ClassDefinition implements Serializable {
    private String className;

    private Map<String, Integer> publicFields = new LinkedHashMap<>();
    private Map<String, Integer> privateFields = new LinkedHashMap<>();

    private Map<MethodSignature, MethodDefinition> privateFunctions = new HashMap<>();
    private Map<MethodSignature, MethodDefinition> publicFunctions = new HashMap<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, Integer> getPublicFields() {
        return publicFields;
    }

    public Map<String, Integer> getPrivateFields() {
        return privateFields;
    }

    public Map<MethodSignature, MethodDefinition> getPrivateFunctions() {
        return privateFunctions;
    }

    public Map<MethodSignature, MethodDefinition> getPublicFunctions() {
        return publicFunctions;
    }

    public Integer getField(String name) {
        if (publicFields.containsKey(name)) {
            return publicFields.get(name);
        }
        if (privateFields.containsKey(name)) {
            return privateFields.get(name);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDefinition that = (ClassDefinition) o;

        return className != null ? className.equals(that.className) : that.className == null;
    }

    @Override
    public int hashCode() {
        return className != null ? className.hashCode() : 0;
    }
}
