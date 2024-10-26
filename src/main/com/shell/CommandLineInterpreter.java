package com.shell;

import com.shell.command.CatCommand;
import com.shell.command.Command;
import com.shell.command.MoveCommand;
import com.shell.command.TouchCommand;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterpreter {
    private final String workingDirectory;
    private final String shortWorkingDirectory;
    private final String user;
    private final String home;
    private final PrintWriter outputWriter;
    private final PrintWriter errorWriter;
    private final Scanner inputScanner;
    private boolean isRunning = true;

    public CommandLineInterpreter(String workingDirectory, String user, String home, PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        this.workingDirectory = workingDirectory;
        this.shortWorkingDirectory = workingDirectory.replaceFirst(home, "~");
        this.user = user;
        this.home = home;
        this.outputWriter = outputWriter;
        this.errorWriter = errorWriter;
        this.inputScanner = inputScanner;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void prompt() {
        outputWriter.print(shortWorkingDirectory + " $ ");
        outputWriter.flush();

        String input = inputScanner.nextLine().trim();

        if (input.isEmpty()) {
            return;
        }

        // Basic parsing until the ArgumentsParser is implemented.
        // TODO: Replace this logic with the actual parsing logic.
        // TODO: Handle ~ in file path to point to $HOME.
        List<String> arguments = new ArrayList<>(List.of(input.split("\\s+")));
        String commandName = arguments.remove(0);

        Command command = createCommand(commandName, arguments);

        if (command == null) {
            errorWriter.println("Unknown command: " + commandName);
            return;
        }

        command.execute(outputWriter, errorWriter, inputScanner);
    }

    private Command createCommand(String command, List<String> arguments) {
        return switch (command) {
            case "mv" -> new MoveCommand(arguments, false, workingDirectory);
            case "cat" -> new CatCommand(arguments.get(0), workingDirectory);
            case "touch" -> new TouchCommand(arguments, workingDirectory);
            // FIXME: Do we need to refactor this to have a dedicated exit command? Will it need access to the CommandLineInterpreter?
            case "exit" -> (outputWriter, errorWriter, inputScanner) -> isRunning = false;
            default -> null;
        };
    }
}
