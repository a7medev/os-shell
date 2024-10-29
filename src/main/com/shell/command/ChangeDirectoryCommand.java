package com.shell.command;

import com.shell.CommandLineInterpreter;
import com.shell.util.FileUtils;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ChangeDirectoryCommand implements Command {
    public static final String NAME = "cd";

    private final String targetDirectory;
    private final String workingDirectory;
    private final CommandLineInterpreter interpreter;

    public ChangeDirectoryCommand(String targetDirectory, String workingDirectory, CommandLineInterpreter interpreter) {
        this.targetDirectory = targetDirectory;
        this.workingDirectory = workingDirectory;
        this.interpreter = interpreter;
    }


    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        File file = FileUtils.fileInWorkingDirectory(targetDirectory, workingDirectory);

        if (!file.exists() || !file.isDirectory()) {
            errorWriter.println(NAME + ": " + targetDirectory + " No such directory");
            return;
        }
        if(file.toString().equals(workingDirectory+"\\ ")){
            errorWriter.println(NAME + ": Please specify a directory");
            return;
        }

        interpreter.setWorkingDirectory(file.toPath().normalize().toString());
    }
}
