package com.umut.ssh.util;

import java.io.PrintStream;

public class SimpleLog {

    private SimpleLog() {}

    public static PrintStream out= System.out;
    public static String trailer= "";

    public static String header= ">>>>>>>>>>>>>>> TEST: ";
    public static void log(String msg) {
        if (out!=null) out.println(header+msg+trailer);
    }

    public static void log(Class<?> c, String msg) {
        if (c==null) log(msg);
        else
        if (msg==null) log(c.getSimpleName());
        else log(c.getSimpleName()+": "+msg);
    }
}
