package com.shell;

import com.shell.command.*;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterpreter {
    private String workingDirectory;
    private String shortWorkingDirectory;
    private final String user;
    private final String kernel;
    private final String home;
    private final PrintWriter outputWriter;
    private final PrintWriter errorWriter;
    private final Scanner inputScanner;
    private boolean isRunning = true;

    public CommandLineInterpreter(String workingDirectory, String user, String home, String kernel, PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        this.user = user;
        this.home = home;
        this.kernel = kernel;
        this.outputWriter = outputWriter;
        this.errorWriter = errorWriter;
        this.inputScanner = inputScanner;
        setWorkingDirectory(workingDirectory);
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        this.shortWorkingDirectory = getShortWorkingDirectory(workingDirectory, home);
    }

    private String getShortWorkingDirectory(String workingDirectory, String home) {
        if (workingDirectory.startsWith(home)) {
            return "~" + workingDirectory.substring(home.length());
        }

        return workingDirectory;
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
            case MoveCommand.NAME -> new MoveCommand(arguments, false, workingDirectory);
            case CatCommand.NAME -> new CatCommand(arguments.get(0), workingDirectory);
            case RemoveCommand.NAME -> new RemoveCommand(arguments, false, true, workingDirectory);
            case TouchCommand.NAME -> new TouchCommand(arguments, workingDirectory);
            case ListCommand.NAME -> new ListCommand(arguments,false, false, workingDirectory);
            case CopyCommand.NAME -> new CopyCommand(arguments, false, workingDirectory);
            case PrintWorkingDirectoryCommand.NAME -> new PrintWorkingDirectoryCommand(workingDirectory);
            case ChangeDirectoryCommand.NAME -> new ChangeDirectoryCommand(arguments.isEmpty() ? " " : arguments.get(0), workingDirectory, this);
            case MakeDirectoryCommand.NAME -> new MakeDirectoryCommand(arguments, workingDirectory);
            case RemoveDirectoryCommand.NAME -> new RemoveDirectoryCommand(arguments, workingDirectory);
            case ManualCommand.NAME -> new ManualCommand(arguments.isEmpty() ? " " : arguments.get(0));
            case UsersCommand.USERS_NAME, UsersCommand.WHO_NAME -> new UsersCommand(user);
            case UnameCommand.NAME -> new UnameCommand(kernel);
            case DateCommand.NAME -> new DateCommand(LocalDateTime.now());
            case EchoCommand.NAME -> new EchoCommand(arguments);
            // FIXME: Do we need to refactor this to have a dedicated exit command? Will it need access to the CommandLineInterpreter?
            case "exit" -> (outputWriter, errorWriter, inputScanner) -> isRunning = false;
            default -> null;
        };
    }
}
