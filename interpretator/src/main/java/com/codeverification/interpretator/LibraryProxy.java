package com.codeverification.interpretator;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.codeverification.compiler.MethodDefinition;
import com.codeverification.compiler.MethodSignature;
import com.codeverification.compiler.ParserFacade;

/**
 * @author Dmitrii Nazukin
 */
public class LibraryProxy implements InvocationHandler{

    Interpretator interpretator;

    public LibraryProxy(Interpretator interpretator) {
        this.interpretator = interpretator;
    }

    @SuppressWarnings("unchecked")
    public static Object newInstance(String lib, Class<?>... interf) throws IOException, ClassNotFoundException {
        ParserFacade parserFacade = new ParserFacade();
        Map<MethodSignature, MethodDefinition> methodDefinitions = parserFacade.readBinCodes(lib);
        Interpretator interpretator1 = new Interpretator(methodDefinitions);
        return Proxy.newProxyInstance(
                interf[0].getClassLoader(),
                interf,
                new LibraryProxy(interpretator1));
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String funcName = method.getName();
        List<AbstractValue> argsVO = new ArrayList<>();
        if (args!=null && args.length != 0) {
            for (Object arg : args) {
                if (arg instanceof Number) {
                    IntValue intValue = new IntValue(((Number) arg).intValue());
                    intValue.setConst(true);
                    argsVO.add(intValue);
                } else if (arg instanceof Character) {
                    CharValue charValue = new CharValue((Character) arg);
                    charValue.setConst(true);
                    argsVO.add(charValue);
                } else if (arg instanceof Boolean) {
                    BoolValue boolValue = new BoolValue((Boolean) arg);
                    boolValue.setConst(true);
                    argsVO.add(boolValue);
                } else if (arg instanceof String) {
                    StringValue stringValue = new StringValue((String) arg);
                    stringValue.setConst(true);
                    argsVO.add(stringValue);
                } else {
                    throw new Exception();
                }
            }
        }
        return interpretator.executeMethodArg(funcName, argsVO);
    }
}
