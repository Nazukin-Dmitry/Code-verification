package com.codeverification.interpretator;

import com.codeverification.compiler.ClassDefinition;
import com.codeverification.compiler.Command;
import com.codeverification.compiler.DataType;
import com.codeverification.compiler.MethodDefinition;
import com.codeverification.compiler.MethodSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeverification.compiler.DataType.BOOL;
import static com.codeverification.compiler.DataType.CHAR;
import static com.codeverification.compiler.DataType.LONG;
import static com.codeverification.compiler.DataType.STRING;
import static com.codeverification.compiler.Modificator.ANY;
import static com.codeverification.compiler.Modificator.PUBLIC;

/**
 * @author Dmitrii Nazukin
 */
public class FuncExecutor {

    private ObjectInstanceValue objectContext;

    private Interpretator interpretator;

    private Map<Integer, AbstractValue> vars = new LinkedHashMap<>();

    private List<AbstractValue> consts = new ArrayList<>();

    private MethodSignature methodSignature;

    private List<Command> commands = new ArrayList<>();

    private int currentCommand = 0;

    private Map<Integer, AbstractValue> registers = new HashMap<>();

    private List<String> funcs = new ArrayList<>();

    private boolean isNative;
    private String libraryName;
    private String nativeFunc;

    private FuncExecutor(List<AbstractValue> args, MethodDefinition methodDefinition, Interpretator interpretator) {
        this.interpretator = interpretator;
        vars.put(0, null);
        addArgs(args, methodDefinition);
        if (!methodDefinition.isNative()) {
            for (int i = 1 + args.size(); i < methodDefinition.getVarsCount(); i++) {
                vars.put(i, null);
            }

            consts.addAll(
                    methodDefinition.getConsts().stream().map(con -> ValueFactory.get(con)).collect(Collectors.toList()));

            commands = methodDefinition.getCommands();
            funcs = methodDefinition.getFuncs();
        } else {
            isNative = true;
            libraryName = methodDefinition.getLibraryName();
            nativeFunc = methodDefinition.getNativeFunc();
        }
        methodSignature = methodDefinition.getMethodSignature();

    }

    public static FuncExecutor getInstance(List<AbstractValue> args,
                                           MethodDefinition methodDefinition,
                                           Interpretator interpretator,
                                           ObjectInstanceValue objectInstanceValue) {
//        checkCall(args, methodDefinition);
        FuncExecutor funcExecutor = new FuncExecutor(args, methodDefinition, interpretator);
        funcExecutor.objectContext = objectInstanceValue;
        return funcExecutor;
    }

    public static void checkCall(List<AbstractValue> args, MethodDefinition methodDefinition, String funcName) {
        if (methodDefinition == null || methodDefinition.getMethodSignature().getArgCount() != args.size()) {
            throw new RuntimeException(funcName + " function for args " + args.toString() + " not found!!!");
        }
    }

    private void addArgs(List<AbstractValue> args, MethodDefinition methodDefinition) {
        List<DataType> argsTypes = methodDefinition.getMethodSignature().getArgsType();
        if (args.size() != methodDefinition.getMethodSignature().getArgCount()) {
            throw new RuntimeException("Wrong function args!!!"
                    + "Function name:"
                    + methodDefinition.getMethodSignature().getFuncName()
                    + " Args"
                    + args.toString());
        }
        for (int i = 0; i < argsTypes.size(); i++) {
            AbstractValue arg = args.get(i);
            vars.put(i + 1, arg);
        }
    }

    public AbstractValue executeMethod() {
        if (isNative) {
            invokeNativeMethod();
        } else {
            while (true) {
                Command command = commands.get(currentCommand);
                if (command.getName().equals("END")) {
                    break;
                } else {
                    executeCommand(command);
                }
            }
        }
        if (methodSignature.getFuncName().equals("New")) {
            return objectContext;
        } else {
            return vars.get(0);
        }
    }

    private void executeCommand(Command command) {
        switch (CommandType.valueOf(command.getName())) {
            case ADD:
                addCommand(command.getArgs());
                currentCommand++;
                break;
            case MINUS:
                minusCommand(command.getArgs());
                currentCommand++;
                break;
            case DIV:
                divCommand(command.getArgs());
                currentCommand++;
                break;
            case MOD:
                modCommand(command.getArgs());
                currentCommand++;
                break;
            case MULT:
                multCommand(command.getArgs());
                currentCommand++;
                break;
            case UNMINUS:
                unMinusCommand(command.getArgs());
                currentCommand++;
                break;
            case UNADD:
                unAddCommand(command.getArgs());
                currentCommand++;
                break;
            case LESS:
                lessCommand(command.getArgs());
                currentCommand++;
                break;
            case LARGER:
                largerCommand(command.getArgs());
                currentCommand++;
                break;
            case EQUAL:
                equalCommand(command.getArgs());
                currentCommand++;
                break;
            case OR:
                orCommand(command.getArgs());
                currentCommand++;
                break;
            case AND:
                andCommand(command.getArgs());
                currentCommand++;
                break;
            case PUSHVAR:
                registers.put(command.getArgs().get(1), vars.get(command.getArgs().get(0)));
                currentCommand++;
                break;
            case LOADVAR:
                AbstractValue v = registers.get(command.getArgs().get(0));
                vars.put(command.getArgs().get(1), v);
                currentCommand++;
                break;
            case PUSHCONST:
                registers.put(command.getArgs().get(1), consts.get(command.getArgs().get(0)).getCopy());
                currentCommand++;
                break;
            case JMPFALSE:
                AbstractValue v1 = registers.get(command.getArgs().get(0));
                if (!v1.asBool()) {
                    currentCommand = command.getArgs().get(1);
                } else {
                    currentCommand++;
                }
                break;
            case JMPTRUE:
                AbstractValue v2 = registers.get(command.getArgs().get(0));
                if (v2.asBool()) {
                    currentCommand = command.getArgs().get(1);
                } else {
                    currentCommand++;
                }
                break;
            case JMP:
                currentCommand = command.getArgs().get(0);
                break;
            case CALL:
                List<AbstractValue> args = new ArrayList<>();
                for (int i = 2; i < command.getArgs().size(); i++) {
                    args.add(registers.get(command.getArgs().get(i)));
                }
                List<DataType> argTypes = args.stream().map(Value::getType).collect(Collectors.toList());
                MethodDefinition methodDefinition = null;
                ObjectInstanceValue objectInstance = null;
                // 1. try to find method inside class
                if (objectContext != null) {
                    methodDefinition = objectContext.getFunction(funcs.get(command.getArgs().get(1)), argTypes, ANY);
                    objectInstance = objectContext;
                }
                // 2. else find outside
                if (methodDefinition == null) {
                    methodDefinition = interpretator.findMethod(funcs.get(command.getArgs().get(1)), argTypes, interpretator.functions);
                }
                if (methodDefinition == null) {
                    throw new RuntimeException("Method doesn't exist. " + funcs.get(command.getArgs().get(1)) + argTypes);
                }
                FuncExecutor funcExecutor = FuncExecutor.getInstance(args, methodDefinition, interpretator, objectInstance);
                AbstractValue v3 = funcExecutor.executeMethod();
                registers.put(command.getArgs().get(0), v3);
                currentCommand++;
                break;
            case PUSHCLASSVAR:
                registers.put(command.getArgs().get(1), objectContext.getProperty
                        (objectContext.getPropertyName(command.getArgs().get(0)), ANY));
                currentCommand++;
                break;
            case LOADCLASSVAR:
                AbstractValue v12 = registers.get(command.getArgs().get(0));
                String propName = objectContext.getPropertyName(command.getArgs().get(1));
                objectContext.setProperty(propName, v12, ANY);
                currentCommand++;
                break;
            case CALLOBJECTFUN:
                callObjectFun(command.getArgs());
                break;
            case INITIALIZE:
                initialize(command.getArgs());
                break;
            case LOADOBJECTFIELD:
                loadObjectField(command.getArgs()) ;
                break;
            case PUSHOBJECTFIELD:
                pushObjectField(command.getArgs());
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void addCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException("+. Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }
        DataType res = first.getType();
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }

        switch (res.getRawText()) {
            case DataType.STRING:
                String strValue = first.asString() + second.asString();
                registers.put(args.get(2), new StringValue(strValue));
                break;
//            case INT:
//                Integer intValue = first.asInt() + second.asInt();
//                registers.put(args.get(2), new IntValue(intValue));
//                break;
            case LONG:
                Long longValue = first.asLong() + second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
//            case BYTE:
//                int byteValue = first.asByte() + second.asByte();
//                registers.put(args.get(2), new ByteValue((byte) byteValue));
//                break;
            default:
                throw new RuntimeException("+ is unavailable for data type" + first.getType());
        }

//        if (first.isConst() && second.isConst()) {
//            registers.get(args.get(2)).setConst(true);
//        }
    }

    private void minusCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType()&& (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException("-.Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }

        DataType res = first.getType();;
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }
        switch (res.getRawText()) {
//            case INT:
//                Integer intValue = first.asInt() - second.asInt();
//                registers.put(args.get(2), new IntValue(intValue));
//                break;
            case LONG:
                Long longValue = first.asLong() - second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
//            case BYTE:
//                int byteValue = first.asByte() - second.asByte();
//                registers.put(args.get(2), new ByteValue((byte) byteValue));
//                break;
            default:
                throw new RuntimeException("- is unavailable for data type" + first.getType());
        }
//        if (first.isConst() && second.isConst()) {
//            registers.get(args.get(2)).setConst(true);
//        }
    }

    private void multCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException("*.Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }
        DataType res = first.getType();;
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }
        switch (res.getRawText()) {
//            case INT:
//                Integer intValue = first.asInt() * second.asInt();
//                registers.put(args.get(2), new IntValue(intValue));
//                break;
            case LONG:
                Long longValue = first.asLong() * second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
//            case BYTE:
//                int byteValue = first.asByte() * second.asByte();
//                registers.put(args.get(2), new ByteValue((byte) byteValue));
//                break;
            default:
                throw new RuntimeException("* is unavailable for data type" + first.getType());
        }
//        if (first.isConst() && second.isConst()) {
//            registers.get(args.get(2)).setConst(true);
//        }
    }

    private void divCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException("/.Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }

        DataType res = first.getType();;
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }
        switch (res.getRawText()) {
//            case INT:
//                Integer intValue = first.asInt() / second.asInt();
//                registers.put(args.get(2), new IntValue(intValue));
//                break;
            case LONG:
                Long longValue = first.asLong() / second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
//            case BYTE:
//                int byteValue = first.asByte() / second.asByte();
//                registers.put(args.get(2), new ByteValue((byte) byteValue));
//                break;
            default:
                throw new RuntimeException("/ is unavailable for data type" + first.getType());
        }
//        if (first.isConst() && second.isConst()) {
//            registers.get(args.get(2)).setConst(true);
//        }
    }

    private void modCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException("%.Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }
        DataType res = first.getType();;
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }

        switch (res.getRawText()) {
//            case INT:
//                Integer intValue = first.asInt() % second.asInt();
//                registers.put(args.get(2), new IntValue(intValue));
//                break;
            case LONG:
                Long longValue = first.asLong() % second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
//            case BYTE:
//                int byteValue = first.asByte() % second.asByte();
//                registers.put(args.get(2), new ByteValue((byte) byteValue));
//                break;
            default:
                throw new RuntimeException("% is unavailable for data type" + first.getType());
        }
//        if (first.isConst() && second.isConst()) {
//            registers.get(args.get(2)).setConst(true);
//        }
    }

    private void lessCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException("<.Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }
        DataType res = first.getType();;
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }

        switch (res.getRawText()) {
//            case INT:
//                boolean intValue = first.asInt() < second.asInt();
//                registers.put(args.get(2), new BoolValue(intValue));
//                break;
            case LONG:
                boolean longValue = first.asLong() < second.asLong();
                registers.put(args.get(2), new BoolValue(longValue));
                break;
            case STRING:
                boolean strV = first.compareTo(second)<0;
                registers.put(args.get(2), new BoolValue(strV));
                break;
            case CHAR:
                boolean chV = first.compareTo(second)<0;
                registers.put(args.get(2), new BoolValue(chV));
                break;
            case BOOL:
                boolean bV = first.compareTo(second)<0;
                registers.put(args.get(2), new BoolValue(bV));
                break;
//            case BYTE:
//                boolean byteValue = first.asByte() < second.asByte();
//                registers.put(args.get(2), new BoolValue(byteValue));
//                break;
            default:
                throw new RuntimeException("< is unavailable for data type" + first.getType());
        }
    }

    private void largerCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException(">.Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }

        DataType res = first.getType();;
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }
        switch (res.getRawText()) {
//            case INT:
//                boolean intValue = first.asInt() > second.asInt();
//                registers.put(args.get(2), new BoolValue(intValue));
//                break;
            case LONG:
                boolean longValue = first.asLong() > second.asLong();
                registers.put(args.get(2), new BoolValue(longValue));
                break;
            case STRING:
                boolean strV = first.compareTo(second)>0;
                registers.put(args.get(2), new BoolValue(strV));
                break;
            case CHAR:
                boolean chV = first.compareTo(second)>0;
                registers.put(args.get(2), new BoolValue(chV));
                break;
            case BOOL:
                boolean bV = first.compareTo(second)>0;
                registers.put(args.get(2), new BoolValue(bV));
                break;
//            case BYTE:
//                boolean byteValue = first.asByte() > second.asByte();
//                registers.put(args.get(2), new BoolValue(byteValue));
//                break;
            default:
                throw new RuntimeException("> is unavailable for data type" + first.getType());
        }
    }

    private void equalCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
//        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
//            throw new RuntimeException("==.Arguments with different data types!!!" +first.getType() + " "+second.getType());
//        }
        DataType res = first.getType();;
//        if (first.isConst()) {
//            res = second.getType();
//        } else {
//            res = first.getType();
//        }

        switch (res.getRawText()) {
//            case INT:
//                boolean intValue = first.asInt().equals(second.asInt());
//                registers.put(args.get(2), new BoolValue(intValue));
//                break;
            case LONG:
                boolean longValue = first.asLong() == (second.asLong());
                registers.put(args.get(2), new BoolValue(longValue));
                break;
//            case BYTE:
//                boolean byteValue = first.asByte().equals(second.asByte());
//                registers.put(args.get(2), new BoolValue(byteValue));
//                break;
            case STRING:
                boolean stringValue = first.asString().equals(second.asString());
                registers.put(args.get(2), new BoolValue(stringValue));
                break;
            case CHAR:
                boolean charValue = first.asChar() == (second.asChar());
                registers.put(args.get(2), new BoolValue(charValue));
                break;
            case BOOL:
                boolean boolValue = first.asBool() == (second.asBool());
                registers.put(args.get(2), new BoolValue(boolValue));
                break;
            default:
                throw new RuntimeException("== is unavailable for data type" + first.getType());
        }
    }

    private void unAddCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));

        switch (first.getType().getRawText()) {
//            case INT:
//                Integer intValue = first.asInt();
//                registers.put(args.get(2), new IntValue(intValue));
//                break;
            case LONG:
                Long longValue = first.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
//            case BYTE:
//                byte byteValue = first.asByte();
//                registers.put(args.get(2), new ByteValue(byteValue));
//                break;
            default:
                throw new RuntimeException("unary + is unavailable for data type" + first.getType());
        }
    }

    private void unMinusCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));

        switch (first.getType().getRawText()) {
//            case INT:
//                Integer intValue = first.asInt() * (-1);
//                registers.put(args.get(2), new IntValue(intValue));
//                break;
            case LONG:
                Long longValue = first.asLong() * (-1);
                registers.put(args.get(1), new LongValue(longValue));
                break;
//            case BYTE:
//                int byteValue = first.asByte() * (-1);
//                registers.put(args.get(2), new ByteValue((byte) byteValue));
//                break;
            default:
                throw new RuntimeException("unary + is unavailable for data type" + first.getType());
        }
    }

    private void andCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (!first.getType().getRawText().equals(BOOL) || !second.getType().getRawText().equals(BOOL)) {
            throw new RuntimeException("Operator '&&' only available for bool arguments!!!");
        }

        boolean value = first.asBool() && second.asBool();
        registers.put(args.get(2), new BoolValue(value));
    }

    private void orCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (!first.getType().getRawText().equals(BOOL) || !second.getType().getRawText().equals(BOOL)) {
            throw new RuntimeException("Operator '||' only available for bool arguments!!!");
        }

        boolean value = first.asBool() || second.asBool();
        registers.put(args.get(2), new BoolValue(value));
    }

    private void invokeNativeMethod() {
//        AbstractValue result = new ObjectValue(null);
//        if (Interpretator.nativeLibs.containsKey(libraryName)) {
//            Object nativeLib = interpretator.nativeLibs.get(libraryName);
//            if (libraryName.equals("kernel32.dll")) {
//                Kernel32 kernel32 = (Kernel32) nativeLib;
//                switch (methodSignature.getFuncName()) {
//                    case "CreateFile":
//                        Object result1 = kernel32.CreateFile(vars.get(1).asString(), (int)vars.get(2).asLong(),
//                                (int)vars.get(3).asLong(), null, (int)vars.get(5).asLong(),
//                                (int)vars.get(6).asLong(), null);
//                        result = new ObjectValue(result1);
//                        break;
//                    case "WriteFile":
//                        boolean result2 = kernel32.WriteFile((HANDLE) vars.get(1).asObject(), vars.get(2).asString().getBytes(),
//                                vars.get(2).asString().getBytes().length, new IntByReference(), null);
//                        result = new BoolValue(result2);
//                        break;
//                    case "CloseHandle":
//                        boolean result3 = kernel32.CloseHandle((HANDLE) vars.get(1).asObject());
//                        result = new BoolValue(result3);
//                        break;
//                    default:
//                        throw new RuntimeException("Function name " + methodSignature.getFuncName() + " doesnt' exist in kernel32.dll");
//                }
//            } else {
//                throw new RuntimeException("Library name " + libraryName + " doesn't exist in interpreter");
//            }
//        } else {
//            throw new RuntimeException("Library name " + libraryName + " doesn't exist in interpreter");
//        }
        AbstractValue[] args = new AbstractValue[methodSignature.getArgCount()];
        String[] argTypes = new String[methodSignature.getArgCount()];
        argTypes = methodSignature.getArgsType().stream().map(DataType::getRawText).toArray(String[]::new);
        String retType = methodSignature.getReturnType().getRawText();

        for (int i = 1; i < methodSignature.getArgCount()+1; i++) {
            args[i-1] = vars.get(i);
        }
        AbstractValue result = NativeCaller.instance.
                callNativeFunc(libraryName, nativeFunc, args, argTypes, retType);
//        if (nativeFunc.equals("ReadFile")) {
//            System.out.println(args[1].asString());
//        }
        vars.put(0, result);
    }

    private void callObjectFun(List<Integer> comArgs) {
        List<AbstractValue> args = new ArrayList<>();
        for (int i = 3; i < comArgs.size(); i++) {
            args.add(registers.get(comArgs.get(i)));
        }
        List<DataType> argTypes = args.stream().map(Value::getType).collect(Collectors.toList());
        ObjectInstanceValue objectInstance = registers.get(comArgs.get(1)).asObjectInstanceValue();
        MethodDefinition methodDefinition = objectInstance.getFunction(funcs.get(comArgs.get(2)), argTypes, PUBLIC);
        if (methodDefinition == null) {
            throw new RuntimeException(objectInstance.getClassDefinition().getClassName() + " class. Public method doesn't exist. "
                    + funcs.get(comArgs.get(2)) + argTypes);
        }
        FuncExecutor funcExecutor = FuncExecutor.getInstance(args, methodDefinition,
                interpretator, objectInstance);
        AbstractValue v3 = funcExecutor.executeMethod();
        registers.put(comArgs.get(0), v3);
        currentCommand++;
    }

    private void initialize(List<Integer> comArgs) {
        Integer targetRegistr = comArgs.get(0);
        Integer classNameNum = comArgs.get(1);
        List<AbstractValue> args = new ArrayList<>();
        for (int i = 2; i < comArgs.size(); i++) {
            args.add(registers.get(comArgs.get(i)));
        }
        String className = consts.get(classNameNum).asString();
        ClassDefinition classDefinition = interpretator.classDefinitions.get(className);
        if (classDefinition != null) {
            ObjectInstanceValue objectInstanceValue = new ObjectInstanceValue(classDefinition);
            MethodDefinition aNew = objectInstanceValue.getFunction("New", args.stream().map(arg -> arg.getType()).collect(Collectors.toList()),
                    PUBLIC);
            if (aNew != null) {
                FuncExecutor funcExecutor = FuncExecutor.getInstance(args, aNew, interpretator, new ObjectInstanceValue(classDefinition));
                AbstractValue result = funcExecutor.executeMethod();
                registers.put(targetRegistr, result);
            } else {
                throw new RuntimeException("Class " + classDefinition.getClassName() + ". Public constructor didn't found for args " + args);
            }
        } else {
            throw new RuntimeException("Class isn't found: "+ className);
        }
        currentCommand++;
    }

    private void loadObjectField(List<Integer> comArgs) {
        String propNeame = consts.get(comArgs.get(2)).asString();
        AbstractValue v = registers.get(comArgs.get(0));
        ObjectInstanceValue object = registers.get(comArgs.get(1)).asObjectInstanceValue();
        object.setProperty(propNeame, v, PUBLIC);
        currentCommand++;
    }

    private void pushObjectField(List<Integer> comArgs) {
        String propName = consts.get(comArgs.get(1)).asString();
        Integer targetRegistr = comArgs.get(2);
        ObjectInstanceValue object = registers.get(comArgs.get(0)).asObjectInstanceValue();
        registers.put(targetRegistr, object.getProperty(propName, PUBLIC));
        currentCommand++;
    }
}
