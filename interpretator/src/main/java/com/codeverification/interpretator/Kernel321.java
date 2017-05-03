package com.codeverification.interpretator;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

/**
 * @author Dmitrii Nazukin
 */
public interface Kernel321 extends Library {
    Kernel321 INSTANCE = (Kernel321) Native.loadLibrary("kernel32.dll", Kernel321.class, W32APIOptions.UNICODE_OPTIONS);



}
