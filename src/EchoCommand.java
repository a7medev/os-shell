package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;

public class EchoCommand implements Command {
    public static final String NAME = "echo";

    private final String argument;

    public EchoCommand(String argument) {
        this.argument = argument;
    }

    //@Override
    public void execute (PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        if (!outputWriter.checkError())
        {
            outputWriter.println(argument);
        }
        else
        {
            errorWriter.println("an error has occurred while writing to the output destination");
        }
    }
}