package com.shell.command;

import com.shell.util.FileUtils;

import java.io.*;
import java.util.Scanner;

public class CatCommand implements Command {

    public static final String NAME = "cat";

    private final String filePath;
    private final String workingDirectory;

    public CatCommand(String filePath, String workingDirectory) {
        this.filePath = filePath;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        if (filePath == null) {
            while (inputScanner.hasNextLine()) {
                String line = inputScanner.nextLine();
                outputWriter.println(line);
            }
            return;
        }

        File file = FileUtils.fileInWorkingDirectory(filePath, workingDirectory);

        if (file.isDirectory()) {
            errorWriter.println(NAME + ": " + filePath + ": Is a directory");
            return;
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            int data;
            while ((data = inputStream.read()) != -1) {
                outputWriter.write(data);
            }
        } catch (FileNotFoundException e) {
            errorWriter.println(NAME + ": " + filePath + ": No such file or directory");
        } catch (IOException e) {
            errorWriter.println(NAME + ": " + filePath + ": Couldn't read file contents");
        }
    }
}
