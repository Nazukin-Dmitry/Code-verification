package com.codeverification.interpretator;

import static com.codeverification.interpretator.FuncExecutor.checkCall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.codeverification.compiler.CodeGenerationVisitor.Const;
import com.codeverification.compiler.DataType;
import com.codeverification.compiler.MethodDefinition;
import com.codeverification.compiler.ParserFacade;
import com.sun.jna.platform.win32.Kernel32;

/**
 * @author Dmitrii Nazukin
 */
public class Interpretator {

    Pattern patternStr = Pattern.compile("\"[^ \\ \"]*(?:\\.[^ \\ \"]*)*\"");

    Pattern patternChar = Pattern.compile("'[^']'");

    Pattern patternHex = Pattern.compile("0[xX][0-9A-Fa-f]+");

    Pattern patternBits = Pattern.compile("0[bB][01]+");

    Pattern patternDec = Pattern.compile("[+-]?[0-9]+");

    Pattern patternBool = Pattern.compile("true|false");

    String s = "\"[^\"\\]*(?:\\.[^\"\\]*)*\"";

    Map<String, MethodDefinition> functions;

    public static final Map<String, Object> nativeLibs = new HashMap<>();

    static {
        nativeLibs.put("kernel32.dll", Kernel32.INSTANCE);
    }

    public Interpretator(Map<String, MethodDefinition> functions) {
        this.functions = functions;
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
            Map<String, MethodDefinition> methodDefinitions = parserFacade.readBinCodes(args[0]);
            Interpretator interpretator = new Interpretator(methodDefinitions);
            interpretator.executeMainMethod();
            
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
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.INT)));
            } else if (patternBool.matcher(args.get(i)).matches()) {
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.BOOL)));
            } else if (patternChar.matcher(args.get(i)).matches()) {
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.CHAR)));
            } else {
                argsVO.add(ValueFactory.get(new Const(args.get(i), DataType.STRING)));
            }
        }
        checkCall(argsVO, functions.get(func), func);
        FuncExecutor funcExecutor = FuncExecutor.getInstance(argsVO, functions.get(func), this);
        return funcExecutor.executeMethod();

    }
    
    private void executeMainMethod() {
        MethodDefinition mainMethod = functions.get("main");
        if (mainMethod == null) {
            throw new RuntimeException("Can't find main method");
        }
        FuncExecutor funcExecutor = FuncExecutor.getInstance(Collections.emptyList(), mainMethod, this);
        funcExecutor.executeMethod();
    }

    public AbstractValue executeMethodArg(String func, List<AbstractValue> args) {
        checkCall(args, functions.get(func), func);
        FuncExecutor funcExecutor = FuncExecutor.getInstance(args, functions.get(func), this);
        return funcExecutor.executeMethod();

    }

}
