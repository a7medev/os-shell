package com.shell;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var workingDirectory = System.getProperty("user.dir");
        var home = System.getProperty("user.home");
        var user = System.getProperty("user.name");

        try (var outputWriter = new PrintWriter(System.out);
             var errorWriter = new PrintWriter(outputWriter);
             var inputScanner = new Scanner(System.in)) {
            var commandLineInterpreter = new CommandLineInterpreter(workingDirectory,
                    user,
                    home,
                    outputWriter,
                    errorWriter,
                    inputScanner);

            while (commandLineInterpreter.getIsRunning()) {
                commandLineInterpreter.prompt();
            }
        }
    }
}
