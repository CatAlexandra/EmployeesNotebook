package com.palyaeva;

/**
 * Class for changing text color in console
 */
public class Printer {

    private static final String ANSI_GREEN = "\u001B[32m";

    private static final String ANSI_RED = "\u001B[31m";

    private static final String ANSI_RESET = "\u001B[0m";

    public static void printlnSuccess(Object object) {
        System.out.println(ANSI_GREEN + object + ANSI_RESET);
    }

    public static void printlnError(Object object) {
        System.out.println(ANSI_RED + object + ANSI_RESET);
    }
}
