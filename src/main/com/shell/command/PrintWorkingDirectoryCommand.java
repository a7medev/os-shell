package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;

public class PrintWorkingDirectoryCommand implements Command {
    public static final String NAME = "pwd";
    private final String workingDirectory;

    public PrintWorkingDirectoryCommand(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        outputWriter.println(workingDirectory);
    }
}
