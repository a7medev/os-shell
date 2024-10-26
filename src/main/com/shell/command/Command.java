package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;

public interface Command {
    void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner);
}
