package com.codeverification.interpretator;

/**
 * @author Dmitrii Nazukin
 */
public interface Caller {
    Object Evkl(long A, long B);
    Object calc(int A, int B, Character character);
    Object hello(String arg);
    Object sum(int a, int b);
    Object fun1(int a, int b);
    Object sto();
    Object concat(String a, String b);
    Object Fib(int n);
    Object and(boolean a, boolean v);
    Object dummy();
    Object Factorial(int n);
    Object FibByte(byte n);
}
