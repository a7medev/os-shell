package com.shell;

import com.shell.command.*;
import com.shell.parser.*;

import java.io.PrintWriter;
import java.time.LocalDateTime;
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

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.getTokens();
        Parser parser = new Parser(tokens);

        try {
            Expression commands = parser.parse();

            Command command = evaluateCommand(commands);
            if (command != null) {
                command.execute(outputWriter, errorWriter, inputScanner);
            }
        } catch (UnexpectedTokenException e) {
            errorWriter.println(e.getMessage());
        }
    }

    private Command evaluateCommand(Expression expression) {
        if (expression instanceof Expression.Command commandExpression) {
            String commandName = commandExpression.cmd();
            List<String> arguments = commandExpression.args();
            List<String> flags = commandExpression.flags();
            Expression redirections = commandExpression.redirections();

            Command command = createCommand(commandName, arguments, flags);

            if (command == null) {
                errorWriter.println("Unknown command: " + commandName);
                return null;
            }

            return redirect(redirections, command);

        } else if (expression instanceof Expression.PipedCommands pipedCommands) {
            Expression left = pipedCommands.left();
            Expression right = pipedCommands.right();

            return new PipedCommand(evaluateCommand(left), evaluateCommand(right));
        }

        return null;
    }

    private Command redirect(Expression redirection, Command cmd) {
        if (redirection instanceof Expression.OutputRedirection outputRedirection) {
            String file = outputRedirection.file();
            return new OutputRedirectionCommand(file, workingDirectory, cmd);
        } else if (redirection instanceof Expression.AppendOutputRedirection appendOutputRedirection) {
            String file = appendOutputRedirection.file();
            return new AppendOutputRedirectionCommand(file, workingDirectory, cmd);
        } else if (redirection instanceof Expression.InputRedirection inputRedirection) {
            String file = inputRedirection.file();
            return new InputRedirectionCommand(file, workingDirectory, cmd);
        }

        return cmd;
    }


    private Command createCommand(String command, List<String> arguments, List<String> flags) {
        return switch (command) {
            case MoveCommand.NAME -> new MoveCommand(arguments, flags.contains("f"), workingDirectory);
            case CatCommand.NAME -> new CatCommand(arguments.isEmpty() ? null : arguments.get(0), workingDirectory);
            case RemoveCommand.NAME -> new RemoveCommand(arguments, flags.contains("f"), flags.contains("r"), workingDirectory);
            case TouchCommand.NAME -> new TouchCommand(arguments, workingDirectory);
            case ListCommand.NAME -> new ListCommand(arguments, flags.contains("a"), flags.contains("r"), workingDirectory);
            case CopyCommand.NAME -> new CopyCommand(arguments, flags.contains("f"), workingDirectory);
            case PrintWorkingDirectoryCommand.NAME -> new PrintWorkingDirectoryCommand(workingDirectory);
            case ChangeDirectoryCommand.NAME -> new ChangeDirectoryCommand(arguments.isEmpty() ? "" : arguments.get(0), workingDirectory, this);
            case MakeDirectoryCommand.NAME -> new MakeDirectoryCommand(arguments, workingDirectory);
            case RemoveDirectoryCommand.NAME -> new RemoveDirectoryCommand(arguments, workingDirectory);
            case ManualCommand.NAME -> new ManualCommand(arguments.isEmpty() ? null : arguments.get(0));
            case UsersCommand.USERS_NAME, UsersCommand.WHO_NAME -> new UsersCommand(user);
            case UnameCommand.NAME -> new UnameCommand(kernel);
            case DateCommand.NAME -> new DateCommand(LocalDateTime.now());
            case EchoCommand.NAME -> new EchoCommand(arguments);
            case "exit" -> (outputWriter, errorWriter, inputScanner) -> isRunning = false;
            default -> null;
        };
    }
}
