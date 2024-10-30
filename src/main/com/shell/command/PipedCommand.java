package com.shell.command;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class PipedCommand implements Command {
    final private Command left;
    final private Command right;

    public PipedCommand(Command left, Command right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        PipedWriter pipedWriter = new PipedWriter();
        PipedReader pipedReader = new PipedReader();

        PrintWriter writer = new PrintWriter(pipedWriter);
        Scanner scanner = new Scanner(pipedReader);
        try {
            pipedWriter.connect(pipedReader);
        } catch (IOException error) {
            errorWriter.println(error.getMessage());
        }

        if (left != null) {
            left.execute(writer, errorWriter, inputScanner);
        }
        if (right != null) {
            right.execute(outputWriter, errorWriter, scanner);
        }
    }
}
