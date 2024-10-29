package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;

public class UnameCommand implements Command {
    public static final String NAME = "uname";


    //@Override
    public void execute (PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        try {
            String kernel = System.getProperty("os.name");
            outputWriter.println(kernel);
        }
        catch (SecurityException e) {
            errorWriter.println("access denied");
        }
        catch (NullPointerException | IllegalArgumentException e) {
            errorWriter.println("provided key is not valid");
        }
    }
}