package com.codeverification.interpretator;

/**
 * Created by 1 on 21.05.2017.
 */
public class NativeCaller {

    public static final NativeCaller instance = new NativeCaller();

    static {
//        System.loadLibrary("libffi.a");
        System.loadLibrary("nativecaller"); // myjni.dll (Windows) or libmyjni.so (Unixes)
    }

    public native AbstractValue callNativeFunc(String lib, String funcName, AbstractValue[] args, String[] argsType,
                                         String retType);

    public static void main(String args[]) {
//        String result = new TestJNIString().sayHello("Hello from Java");
//        System.out.println("In Java, the returned string is: " + result);
        System.out.println(0x40000000L);
        NativeCaller nativeCaller = new NativeCaller();
        AbstractValue[] argss = {new StringValue("F:\\study\\codeerification\\interpretator\\src\\main\\java\\com\\codeverification\\interpretator\\hello.txt")
                , new LongValue(0x40000000L), new LongValue(0L), new LongValue(0L),
                new LongValue(2L), new LongValue(0L), new LongValue(0L)};
        String[] argsT = {"string", "int", "int", "int", "int", "int", "int"};
        while (true) {
            AbstractValue handle = nativeCaller.callNativeFunc("kernel32.dll", "CreateFileA", argss
                    , argsT, "int");
            System.out.println("");
        }
    }
}
