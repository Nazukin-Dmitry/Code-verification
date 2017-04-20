package com.codeverification.interpretator;

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

/**
 * @author Dmitrii Nazukin
 */
public class FuncExecutor {

    private Interpretator interpretator;

    private Map<Integer, AbstractValue> vars = new LinkedHashMap<>();

    private List<AbstractValue> consts = new ArrayList<>();

    private MethodSignature methodSignature;

    private List<Command> commands = new ArrayList<>();

    private int currentCommand = 0;

    private Map<Integer, AbstractValue> registers = new HashMap<>();

    private List<String> funcs = new ArrayList<>();

    private FuncExecutor(List<AbstractValue> args, MethodDefinition methodDefinition, Interpretator interpretator) {
        this.interpretator = interpretator;
        vars.put(0, null);
        addArgs(args, methodDefinition);
        for (int i = 1 + args.size(); i < methodDefinition.getVarsCount(); i++) {
            vars.put(i, null);
        }

        consts.addAll(
                methodDefinition.getConsts().stream().map(con -> ValueFactory.get(con)).collect(Collectors.toList()));
        methodSignature = methodDefinition.getMethodSignature();
        commands = methodDefinition.getCommands();
        funcs = methodDefinition.getFuncs();

    }

    public static FuncExecutor getInstance(List<AbstractValue> args,
                                           MethodDefinition methodDefinition,
                                           Interpretator interpretator) {
//        checkCall(args, methodDefinition);
        FuncExecutor funcExecutor = new FuncExecutor(args, methodDefinition, interpretator);
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
            DataType argType = argsTypes.get(i);
            if (!arg.isConst()) {
                if (argType != arg.getType()) {
                    throw new RuntimeException("Wrong function args!!!"
                            + "Function name:"
                            + methodDefinition.getMethodSignature().getFuncName()
                            + " Args"
                            + args.toString());
                }
            } else {
                arg = ValueFactory.getValue(argType, arg);
            }

            vars.put(i + 1, arg);
        }
    }

    public AbstractValue executeMethod() {
        while (true) {
            Command command = commands.get(currentCommand);
            if (command.getName().equals("END")) {
                break;
            } else {
                executeCommand(command);
            }
        }
        if (vars.get(0) == null) {
            throw new RuntimeException("Return value - " + methodSignature.getFuncName() + " - hasn't been initialized!!!");
        }
        if (methodSignature.getReturnType() == DataType.UNDEFINED) {
            return vars.get(0);
        } else if (methodSignature.getReturnType() == vars.get(0).getType()) {
            return vars.get(0);
        } else {
            throw new RuntimeException("Method "
                    + methodSignature.getFuncName()
                    + " return type should be "
                    + methodSignature.getReturnType()
                    + ". Current "
                    + vars.get(0).getType());
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
                registers.put(command.getArgs().get(1), consts.get(command.getArgs().get(0)));
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
                checkCall(args, interpretator.functions.get(funcs.get(command.getArgs().get(1))),
                        funcs.get(command.getArgs().get(1)));
                FuncExecutor funcExecutor = FuncExecutor.getInstance(args,
                        interpretator.functions.get(funcs.get(command.getArgs().get(1))),
                        interpretator);
                AbstractValue v3 = funcExecutor.executeMethod();
                registers.put(command.getArgs().get(0), v3);
                currentCommand++;

        }
    }

    private void addCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
            throw new RuntimeException("+. Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }
        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }

        switch (res) {
            case STRING:
                String strValue = first.asString() + second.asString();
                registers.put(args.get(2), new StringValue(strValue));
                break;
            case INT:
                Integer intValue = first.asInt() + second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            case LONG:
                Long longValue = first.asLong() + second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
            case BYTE:
                int byteValue = first.asByte() + second.asByte();
                registers.put(args.get(2), new ByteValue((byte) byteValue));
                break;
            default:
                throw new RuntimeException("+ is unavailable for data type" + first.getType());
        }

        if (first.isConst() && second.isConst()) {
            registers.get(args.get(2)).setConst(true);
        }
    }

    private void minusCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType()&& (!second.isConst() && !first.isConst())) {
            throw new RuntimeException("-.Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }

        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }
        switch (res) {
            case INT:
                Integer intValue = first.asInt() - second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            case LONG:
                Long longValue = first.asLong() - second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
            case BYTE:
                int byteValue = first.asByte() - second.asByte();
                registers.put(args.get(2), new ByteValue((byte) byteValue));
                break;
            default:
                throw new RuntimeException("- is unavailable for data type" + first.getType());
        }
        if (first.isConst() && second.isConst()) {
            registers.get(args.get(2)).setConst(true);
        }
    }

    private void multCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
            throw new RuntimeException("*.Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }
        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }
        switch (res) {
            case INT:
                Integer intValue = first.asInt() * second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            case LONG:
                Long longValue = first.asLong() * second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
            case BYTE:
                int byteValue = first.asByte() * second.asByte();
                registers.put(args.get(2), new ByteValue((byte) byteValue));
                break;
            default:
                throw new RuntimeException("* is unavailable for data type" + first.getType());
        }
        if (first.isConst() && second.isConst()) {
            registers.get(args.get(2)).setConst(true);
        }
    }

    private void divCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
            throw new RuntimeException("/.Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }

        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }
        switch (res) {
            case INT:
                Integer intValue = first.asInt() / second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            case LONG:
                Long longValue = first.asLong() / second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
            case BYTE:
                int byteValue = first.asByte() / second.asByte();
                registers.put(args.get(2), new ByteValue((byte) byteValue));
                break;
            default:
                throw new RuntimeException("/ is unavailable for data type" + first.getType());
        }
        if (first.isConst() && second.isConst()) {
            registers.get(args.get(2)).setConst(true);
        }
    }

    private void modCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
            throw new RuntimeException("%.Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }
        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }

        switch (res) {
            case INT:
                Integer intValue = first.asInt() % second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            case LONG:
                Long longValue = first.asLong() % second.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
            case BYTE:
                int byteValue = first.asByte() % second.asByte();
                registers.put(args.get(2), new ByteValue((byte) byteValue));
                break;
            default:
                throw new RuntimeException("% is unavailable for data type" + first.getType());
        }
        if (first.isConst() && second.isConst()) {
            registers.get(args.get(2)).setConst(true);
        }
    }

    private void lessCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
            throw new RuntimeException("<.Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }
        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }

        switch (res) {
            case INT:
                boolean intValue = first.asInt() < second.asInt();
                registers.put(args.get(2), new BoolValue(intValue));
                break;
            case LONG:
                boolean longValue = first.asLong() < second.asLong();
                registers.put(args.get(2), new BoolValue(longValue));
                break;
            case BYTE:
                boolean byteValue = first.asByte() < second.asByte();
                registers.put(args.get(2), new BoolValue(byteValue));
                break;
            default:
                throw new RuntimeException("< is unavailable for data type" + first.getType());
        }
    }

    private void largerCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
            throw new RuntimeException(">.Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }

        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }
        switch (res) {
            case INT:
                boolean intValue = first.asInt() > second.asInt();
                registers.put(args.get(2), new BoolValue(intValue));
                break;
            case LONG:
                boolean longValue = first.asLong() > second.asLong();
                registers.put(args.get(2), new BoolValue(longValue));
                break;
            case BYTE:
                boolean byteValue = first.asByte() > second.asByte();
                registers.put(args.get(2), new BoolValue(byteValue));
                break;
            default:
                throw new RuntimeException("> is unavailable for data type" + first.getType());
        }
    }

    private void equalCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != second.getType() && (!second.isConst() && !first.isConst())) {
            throw new RuntimeException("==.Arguments with different data types!!!" +first.getType() + " "+second.getType());
        }
        DataType res;
        if (first.isConst()) {
            res = second.getType();
        } else {
            res = first.getType();
        }

        switch (res) {
            case INT:
                boolean intValue = first.asInt().equals(second.asInt());
                registers.put(args.get(2), new BoolValue(intValue));
                break;
            case LONG:
                boolean longValue = first.asLong().equals(second.asLong());
                registers.put(args.get(2), new BoolValue(longValue));
                break;
            case BYTE:
                boolean byteValue = first.asByte().equals(second.asByte());
                registers.put(args.get(2), new BoolValue(byteValue));
                break;
            case STRING:
                boolean stringValue = first.asString().equals(second.asString());
                registers.put(args.get(2), new BoolValue(stringValue));
                break;
            case CHAR:
                boolean charValue = first.asChar().equals(second.asChar());
                registers.put(args.get(2), new BoolValue(charValue));
                break;
            case BOOL:
                boolean boolValue = first.asBool().equals(second.asBool());
                registers.put(args.get(2), new BoolValue(boolValue));
                break;
            default:
                throw new RuntimeException("== is unavailable for data type" + first.getType());
        }
    }

    private void unAddCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));

        switch (first.getType()) {
            case INT:
                Integer intValue = first.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            case LONG:
                Long longValue = first.asLong();
                registers.put(args.get(2), new LongValue(longValue));
                break;
            case BYTE:
                byte byteValue = first.asByte();
                registers.put(args.get(2), new ByteValue(byteValue));
                break;
            default:
                throw new RuntimeException("unary + is unavailable for data type" + first.getType());
        }
    }

    private void unMinusCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));

        switch (first.getType()) {
            case INT:
                Integer intValue = first.asInt() * (-1);
                registers.put(args.get(2), new IntValue(intValue));
                break;
            case LONG:
                Long longValue = first.asLong() * (-1);
                registers.put(args.get(2), new LongValue(longValue));
                break;
            case BYTE:
                int byteValue = first.asByte() * (-1);
                registers.put(args.get(2), new ByteValue((byte) byteValue));
                break;
            default:
                throw new RuntimeException("unary + is unavailable for data type" + first.getType());
        }
    }

    private void andCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != DataType.BOOL || second.getType() != DataType.BOOL) {
            throw new RuntimeException("Operator '&&' only available for bool arguments!!!");
        }

        boolean value = first.asBool() && second.asBool();
        registers.put(args.get(2), new BoolValue(value));
    }

    private void orCommand(List<Integer> args) {
        AbstractValue first = registers.get(args.get(0));
        AbstractValue second = registers.get(args.get(1));
        if (first.getType() != DataType.BOOL || second.getType() != DataType.BOOL) {
            throw new RuntimeException("Operator '&&' only available for bool arguments!!!");
        }

        boolean value = first.asBool() || second.asBool();
        registers.put(args.get(2), new BoolValue(value));
    }

}
