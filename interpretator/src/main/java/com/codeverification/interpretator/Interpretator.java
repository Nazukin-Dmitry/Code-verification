package com.codeverification.interpretator;

import com.codeverification.compiler.ClassDefinition;
import com.codeverification.compiler.CodeGenerationVisitor.Const;
import com.codeverification.compiler.DataType;
import com.codeverification.compiler.MethodDefinition;
import com.codeverification.compiler.MethodSignature;
import com.codeverification.compiler.ParserFacade;
import com.sun.jna.platform.win32.Kernel32;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeverification.compiler.DataType.BOOL;
import static com.codeverification.compiler.DataType.CHAR;
import static com.codeverification.compiler.DataType.INT;
import static com.codeverification.compiler.DataType.STRING;
import static com.codeverification.interpretator.FuncExecutor.checkCall;

/**
 * @author Dmitrii Nazukin
 */
public class Interpretator {

    public static Interpretator instance = null;

    Pattern patternStr = Pattern.compile("\"[^ \\ \"]*(?:\\.[^ \\ \"]*)*\"");

    Pattern patternChar = Pattern.compile("'[^']'");

    Pattern patternHex = Pattern.compile("0[xX][0-9A-Fa-f]+");

    Pattern patternBits = Pattern.compile("0[bB][01]+");

    Pattern patternDec = Pattern.compile("[+-]?[0-9]+");

    Pattern patternBool = Pattern.compile("true|false");

    String s = "\"[^\"\\]*(?:\\.[^\"\\]*)*\"";

    Map<MethodSignature, MethodDefinition> functions;
    Map<String, ClassDefinition> classDefinitions;

    public static final Map<String, Object> nativeLibs = new HashMap<>();

    static {
        nativeLibs.put("kernel32.dll", Kernel32.INSTANCE);
    }

    private Interpretator(Map<MethodSignature, MethodDefinition> functions, Map<String, ClassDefinition> classDefinitions) {
        this.functions = functions;
        this.classDefinitions = classDefinitions;
    }

//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        ParserFacade parserFacade = new ParserFacade();
//        Map<String, MethodDefinition> methodDefinitions = parserFacade.readBinCodes(args[0]);
//        Interpretator interpretator = new Interpretator(methodDefinitions);
//        Scanner sc = new Scanner(System.in);
//        while(sc.hasNextLine()) {
//            try {
//                String line = sc.nextLine();
//                String[] lines = line.split("[,()]");
//                List<String> tr = new ArrayList<>(Arrays.asList(lines));
//                tr.remove(0);
//                System.out.println(interpretator.executeMethod(lines[0], tr));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static void main(String[] args) {
        try {
            ParserFacade parserFacade = new ParserFacade();
            List<Object> list = parserFacade.readBinCodes(args[0]);
            Map<MethodSignature, MethodDefinition> methodDefinitions = (Map<MethodSignature, MethodDefinition>) list.get(0);
            Map<String, ClassDefinition> classDefinitions = (Map<String, ClassDefinition>) list.get(1);
            instance = new Interpretator(methodDefinitions, classDefinitions);
            instance.executeMainMethod();
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public AbstractValue executeMethod(String func, List<String> args) {
        List<AbstractValue> argsVO = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            if (patternBits.matcher(args.get(i)).matches()
                    || patternDec.matcher(args.get(i)).matches()
                    || patternBits.matcher(args.get(i)).matches()
                    || patternHex.matcher(args.get(i)).matches()) {
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.getDataType(INT))));
            } else if (patternBool.matcher(args.get(i)).matches()) {
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.getDataType(BOOL))));
            } else if (patternChar.matcher(args.get(i)).matches()) {
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.getDataType(CHAR))));
            } else {
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.getDataType(STRING))));
            }
        }
        checkCall(argsVO, functions.get(func), func);
        FuncExecutor funcExecutor = FuncExecutor.getInstance(argsVO, functions.get(func), this, null);
        return funcExecutor.executeMethod();

    }
    
    private void executeMainMethod() {
        MethodDefinition mainMethod = findMethod("main", Collections.emptyList(), functions);
        if (mainMethod == null) {
            throw new RuntimeException("main method isn't found.");
        }
        FuncExecutor funcExecutor = FuncExecutor.getInstance(Collections.emptyList(), mainMethod, this,null);
        funcExecutor.executeMethod();
    }

    public static MethodDefinition findMethod(String funcName, List<DataType> argType, Map<MethodSignature, MethodDefinition> functions){
        MethodSignature methodSignature = new MethodSignature(funcName, argType.size());
        methodSignature.getArgsType().addAll(argType);
        if (!functions.containsKey(methodSignature)) {
            //try to find native method by funcName and args count
            Optional<MethodDefinition> any = functions.entrySet().stream().filter(entry -> {
                return entry.getKey().getFuncName().equals(funcName) && entry.getKey().getArgCount() == argType.size()
                        && entry.getValue().isNative();
            }).map(entry -> entry.getValue()).findAny();
            if (any.isPresent()) {
                return any.get();
            } else {
                //maybe generic method?
                Set<MethodSignature> methodSignatures = functions.keySet().stream()
                        .filter(sign -> sign.getFuncName().equals(funcName) && sign.getArgCount() == argType.size())
                        .collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(methodSignatures)) {
                    for (MethodSignature signature : methodSignatures) {
                        if (compareArgTypes(signature.getArgsType(), argType)) {
                            return functions.get(signature);
                        }
                    }
                }
            }
        } else {
            return functions.get(methodSignature);
        }
        return null;
    }

    private static boolean compareArgTypes(List<DataType> funTypes1, List<DataType> argTypes2) {
        boolean result = true;
        for (int i = 0; i < funTypes1.size(); i++) {
            if (!funTypes1.get(i).equals(argTypes2.get(i)) && !funTypes1.get(i).getRawText().equals(DataType.UNDEFINED)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public AbstractValue executeMethodArg(String func, List<AbstractValue> args) {
        checkCall(args, functions.get(func), func);
        FuncExecutor funcExecutor = FuncExecutor.getInstance(args, functions.get(func), this, null);
        return funcExecutor.executeMethod();

    }

}
