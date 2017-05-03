package com.codeverification.interpretator;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author Dmitrii Nazukin
 */
public interface Kernel32 extends Library {
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32.dll", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);



}
