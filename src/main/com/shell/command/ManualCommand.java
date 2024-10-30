package com.shell.command;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ManualCommand implements Command {
    public static final String MAN_NAME = "man";
    public static final String HELP_NAME = "help";

    private final String commandName;
    private final Map<String, String> commandManuals;

    public ManualCommand(String commandName) {
        this.commandName = commandName;
        this.commandManuals = new HashMap<>();
        loadCommandManuals();
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        if (commandName == null || commandName.isEmpty()) {
            for (Map.Entry<String, String> entry : commandManuals.entrySet()) {
                outputWriter.println(entry.getValue());
                outputWriter.println();
            }
            return;
        }

        String manual = commandManuals.get(commandName);
        if (manual != null) {
            outputWriter.println(manual);
            outputWriter.println();
        } else {
            errorWriter.println(MAN_NAME + ": No manual entry for " + commandName);
        }
    }

    private void loadCommandManuals() {
        commandManuals.put("date", "date - Displays the current date and time.");
        commandManuals.put("echo", "echo - Displays a line of text.\n\nUsage:\n  echo [text]");
        commandManuals.put("uname", "uname - Prints system information.");
        commandManuals.put("users", "users - Prints the logged-in users.");
        commandManuals.put("who", "who - Shows who is logged on.");
        commandManuals.put("pwd", "pwd - Prints the current working directory.");
        commandManuals.put("cd", "cd - Changes the current directory.\n\nUsage:\n  cd <directory>");
        commandManuals.put("ls", "ls - Lists directory contents.\n\nOptions:\n  -a   Show all files, including hidden files\n  -r   Reverse order");
        commandManuals.put("man", "man - Displays the manual page for a command.\n\nUsage:\n  man <command>");
        commandManuals.put("mkdir", "mkdir - Creates directories.\n\nUsage:\n  mkdir <directory>");
        commandManuals.put("rmdir", "rmdir - Removes empty directories.\n\nUsage:\n  rmdir <directory>");
        commandManuals.put("touch", "touch - Updates file timestamps or creates new files.\n\nUsage:\n  touch <filename>");
        commandManuals.put("cp", "cp - Copies files or directories.\n\nUsage:\n  cp <source> <destination>");
        commandManuals.put("mv", "mv - Moves or renames files.\n\nUsage:\n  mv <source> <destination>");
        commandManuals.put("rm", "rm - Removes files or directories.\n\nUsage:\n  rm <file>\n\nOptions:\n  -r   Recursive removal for directories\n  -f   Force deletion without prompt");
        commandManuals.put("cat", "cat - Concatenates and displays file contents.\n\nUsage:\n  cat <file>");
        commandManuals.put(">", "> - Redirects output to a file, overwriting it.\n\nUsage:\n  command > <file>");
        commandManuals.put(">>", ">> - Appends output to the end of a file.\n\nUsage:\n  command >> <file>");
        commandManuals.put("<", "< - Redirects the input of a command to be taken from a file.\n\nUsage:\n  command < <file>");
        commandManuals.put("|", "| - Pipes | redirect the output of the previous command as in input to another command.\n\nUsage:\n  command1 | command2");
    }
}
