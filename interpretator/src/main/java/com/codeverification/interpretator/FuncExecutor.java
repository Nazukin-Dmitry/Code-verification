package com.codeverification.interpretator;

import com.codeverification.compiler.Command;
import com.codeverification.compiler.DataType;
import com.codeverification.compiler.MethodDefinition;
import com.codeverification.compiler.MethodSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Nazukin
 */
public class FuncExecutor {

    private Interpretator interpretator;

    private List<Value> vars;

    private List<Value> consts = new ArrayList<>();

    private MethodSignature methodSignature;

    private List<Command> commands = new ArrayList<>();

    private int currentCommand = 0;

    private Map<Integer, Value> registers = new HashMap<>();

    private List<String> funcs = new ArrayList<>();

    private FuncExecutor(List<Value> args, MethodDefinition methodDefinition, Interpretator interpretator) {
        this.interpretator = interpretator;
        vars = new ArrayList<>(methodDefinition.getVarsCount());
        vars.add(null);
        vars.addAll(args);
        for (int i = 1 + args.size(); i < vars.size(); i++) {
            vars.add(null);
        }

        consts.addAll(methodDefinition.getConsts().stream().map(con -> ValueFactory.get(con)).collect(Collectors.toList()));
        methodSignature = methodDefinition.getMethodSignature();
        commands = methodDefinition.getCommands();
        funcs = methodDefinition.getFuncs();

    }

    public static FuncExecutor getInstance(List<Value> args, MethodDefinition methodDefinition, Interpretator interpretator) {
        checkArgs(args, methodDefinition);
        FuncExecutor funcExecutor = new FuncExecutor(args, methodDefinition, interpretator);
        return funcExecutor;
    }

    private static void checkArgs(List<Value> args, MethodDefinition methodDefinition) {
        List<DataType> argsTypes = methodDefinition.getMethodSignature().getArgsType();
        if (args.size() != methodDefinition.getMethodSignature().getArgCount()) {
            throw new RuntimeException("Wrong functions args!!!" + methodDefinition.getMethodSignature().getFuncName());
        }
        for (int i = 0; i < argsTypes.size(); i++) {
            if (argsTypes.get(i) != args.get(i).getType()) {
                throw new RuntimeException("Wrong functions args!!!" + methodDefinition.getMethodSignature().getFuncName());
            }
        }
    }

    public Value executeMethod() {
        while (true) {
            Command command = commands.get(currentCommand);
            if (command.getName().equals("END")) {
                break;
            } else {
                executeCommand(command);
            }
        }
        return vars.get(0);
    }

    private void executeCommand(Command command) {
        switch (CommandType.valueOf(command.getName())) {
            case ADD:
                addCommand(command.getArgs());

        }
    }

    private void addCommand(List<Integer> args) {
        Value first = registers.get(args.get(0));
        Value second = registers.get(args.get(1));
        if (first.getType() != second.getType()) {
            throw new RuntimeException("Arguments with different data types!!!");
        }

        switch (first.getType()) {
            case STRING:
                String strValue = first.asString() + second.asString();
                registers.put(args.get(2), new StringValue(strValue));
                break;
            case INT:
                Integer intValue = first.asInt() + second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            default:
                throw new RuntimeException("+ is unavailable for data type" + first.getType());
        }
    }

    private void minusCommand(List<Integer> args) {
        Value first = registers.get(args.get(0));
        Value second = registers.get(args.get(1));
        if (first.getType() != second.getType()) {
            throw new RuntimeException("Arguments with different data types!!!");
        }

        switch (first.getType()) {
            case INT:
                Integer intValue = first.asInt() - second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            default:
                throw new RuntimeException("- is unavailable for data type" + first.getType());
        }
    }

    private void multCommand(List<Integer> args) {
        Value first = registers.get(args.get(0));
        Value second = registers.get(args.get(1));
        if (first.getType() != second.getType()) {
            throw new RuntimeException("Arguments with different data types!!!");
        }

        switch (first.getType()) {
            case INT:
                Integer intValue = first.asInt() * second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            default:
                throw new RuntimeException("* is unavailable for data type" + first.getType());
        }
    }

    private void divCommand(List<Integer> args) {
        Value first = registers.get(args.get(0));
        Value second = registers.get(args.get(1));
        if (first.getType() != second.getType()) {
            throw new RuntimeException("Arguments with different data types!!!");
        }

        switch (first.getType()) {
            case INT:
                Integer intValue = first.asInt() / second.asInt();
                registers.put(args.get(2), new IntValue(intValue));
                break;
            default:
                throw new RuntimeException("/ is unavailable for data type" + first.getType());
        }
    }

    public enum CommandType {
        ADD,
        MINUS,
        MULT,
        DIV,
        MOD,

        LESS,
        LARGER,
        EQUAL,

        UNADD,
        UNMINUS,

        PUSHVAR,
        LOADVAR,
        PUSHCONST,

        JUMPFALSE,
        JUMPTRUE,
        JMP,

        CALL;
    }
}
