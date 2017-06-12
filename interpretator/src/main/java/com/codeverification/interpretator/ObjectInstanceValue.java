package com.codeverification.interpretator;

import com.codeverification.compiler.ClassDefinition;
import com.codeverification.compiler.DataType;
import com.codeverification.compiler.MethodDefinition;
import com.codeverification.compiler.Modificator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author Dmitrii Nazukin
 */
public class ObjectInstanceValue extends AbstractValue {

    private ClassDefinition classDefinition;
    private Map<String, AbstractValue> properties = new HashMap<>();

    private ObjectInstanceValue baseObject;

    public ObjectInstanceValue(ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
        if (classDefinition.getBaseClass() != null) {
            baseObject = new ObjectInstanceValue(Interpretator.instance.classDefinitions.get(classDefinition.getBaseClass()));
        }
    }

    @Override
    public DataType getType() {
        return new DataType(classDefinition.getClassName());
    }

    @Override
    public void parse(String value) {

    }

    @Override
    public int compareTo(Value value) {
        return 0;
    }

    public String getPropertyName(Integer name) {
        Optional<String> first = classDefinition.getFields().entrySet().stream()
                .filter(e -> e.getValue().equals(name)).map(Entry::getKey).findFirst();
        return first.get();
    }

    public AbstractValue getProperty(String name, Modificator modififcator) {
        AbstractValue result;
        if (classDefinition.getFields().containsKey(name)) {
            result = properties.get(name);
            if (modififcator == Modificator.PUBLIC) {
                if (classDefinition.getFieldsModificator().get(name) != Modificator.PUBLIC) {
                    throw new RuntimeException(classDefinition.getClassName() + " doesn't contain public property " + name);
                }
            } else if (modififcator == Modificator.PRIVATE) {
                if (classDefinition.getFieldsModificator().get(name)!= Modificator.PRIVATE) {
                    throw new RuntimeException(classDefinition.getClassName() + " doesn't contain private property " + name);
                }
            }
            if (result == null) {
                throw new RuntimeException("Property is not initialised: " + name);
            } else {
                return result;
            }
        } else if (baseObject != null) {
            result = baseObject.getProperty(name, Modificator.PUBLIC);
            if (result != null) {
                return result;
            }
        }

        throw new RuntimeException(classDefinition.getClassName() + " doesn't contain property " + name);
    }

    public void setProperty(String name, AbstractValue value, Modificator modificator) {
        if (classDefinition.getFields().containsKey(name)) {
            if (modificator == null || modificator == Modificator.ANY) {
                properties.put(name, value);
            } else if (modificator == Modificator.PUBLIC) {
                if (classDefinition.getFieldsModificator().get(name)== Modificator.PUBLIC) {
                    properties.put(name, value);
                } else {
                    throw new RuntimeException(classDefinition.getClassName() + " doesn't contain public property " + name);
                }
            } else if (modificator == Modificator.PRIVATE) {
                if (classDefinition.getFieldsModificator().get(name)== Modificator.PRIVATE) {
                    properties.put(name, value);
                } else {
                    throw new RuntimeException(classDefinition.getClassName() + " doesn't contain private property " + name);
                }
            }
            return;
        } else if (baseObject != null) {
            baseObject.setProperty(name, value, Modificator.PUBLIC);
        }
        throw new RuntimeException(classDefinition.getClassName() + " doesn't contain property " + name);
    }

    public MethodDefinition getFunction(String funcName, List<DataType> argType, Modificator modificator, Wrapper context) {
        context.objectInstanceValue = this;
        MethodDefinition method = null;
        method = Interpretator.findMethod(funcName, argType, classDefinition.getFunctions());
        if (method == null) {
            if (baseObject != null && !"New".equals(funcName)) {
                return baseObject.getFunction(funcName, argType, Modificator.PUBLIC, context);
            }
            return null;
//                throw new RuntimeException("Class " + classDefinition.getClassName() + ". Method doesn't exist. " + funcName);
        }

        Modificator m = classDefinition.getFunctionsModificator().get(method.getMethodSignature());
        if (modificator != null) {
            switch (modificator) {
                case ANY:
                    return method;
                default:
                    if (m == modificator) {
                        return method;
                    } else {
                        return null;
//                        throw new RuntimeException();
                    }
            }
        }

        return null;
//        throw new RuntimeException();
    }

    @Override
    public ObjectInstanceValue asObjectInstanceValue() {
        return this;
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    @Override
    public AbstractValue getCopy() {
        return new ObjectInstanceValue(classDefinition);
    }
}
