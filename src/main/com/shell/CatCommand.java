package com.shell;

import com.shell.command.Command;
import com.shell.util.FileUtils;

import java.io.*;

public class CatCommand implements Command {

    public static String NAME = "cat";

    private final String filePath;
    private final String workingDirectory;
    private final PrintWriter outputWriter;
    private final PrintWriter errorWriter;

    public CatCommand(String filePath, String workingDirectory, PrintWriter outputWriter, PrintWriter errorWriter) {
        this.filePath = filePath;
        this.workingDirectory = workingDirectory;
        this.outputWriter = outputWriter;
        this.errorWriter = errorWriter;
    }

    @Override
    public void execute() {
        File file = FileUtils.fileInWorkingDirectory(filePath, workingDirectory);

        if (file.isDirectory()) {
            errorWriter.println(NAME + ": " + filePath + ": Is a directory");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                outputWriter.println(line);
            }
        } catch (FileNotFoundException e) {
            errorWriter.println(NAME + ": " + filePath + ": No such file or directory");
        } catch (IOException e) {
            errorWriter.println(NAME + ": " + filePath + ": Couldn't read file contents");
        }
    }
}
