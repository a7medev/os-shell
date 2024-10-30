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

        try {
            pipedWriter.connect(pipedReader);
        } catch (IOException error) {
            errorWriter.println("Error: Failed to pipe commands.");
        }

        if (left != null) {
            try (PrintWriter writer = new PrintWriter(pipedWriter)) {
                left.execute(writer, errorWriter, inputScanner);
            }
        }

        if (right != null) {
            try (Scanner scanner = new Scanner(pipedReader)) {
                right.execute(outputWriter, errorWriter, scanner);
            }
        }
    }
}
