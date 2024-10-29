package com.shell.command;

import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class EchoCommand implements Command {
    public static final String NAME = "echo";

    private final List<String> arguments;

    public EchoCommand(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        for (String argument : arguments) {
            outputWriter.println(argument);
        }
    }
}