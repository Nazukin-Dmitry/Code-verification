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

    private Map<String, Integer> fields = new LinkedHashMap<>();
    private Map<String, Modificator> fieldsModificator = new LinkedHashMap<>();

    private Map<MethodSignature, MethodDefinition> functions = new HashMap<>();
    private Map<MethodSignature, Modificator> functionsModificator = new HashMap<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, Integer> getFields() {
        return fields;
    }

    public Map<String, Modificator> getFieldsModificator() {
        return fieldsModificator;
    }

    public Map<MethodSignature, MethodDefinition> getFunctions() {
        return functions;
    }

    public Map<MethodSignature, Modificator> getFunctionsModificator() {
        return functionsModificator;
    }

    public Integer getField(String name) {
        if (fields.containsKey(name)) {
            return fields.get(name);
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
