package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;

public class UnameCommand implements Command {
    public static final String NAME = "uname";
    private final String kernel;

    public UnameCommand(String kernel) {
        this.kernel = kernel;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        outputWriter.println(kernel);
    }
}