package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateCommand implements Command {
    public static final String NAME = "date";

    private final LocalDateTime now;

    public DateCommand(LocalDateTime now) {
        this.now = now;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd-MM-yyyy HH:mm:ss");
        String date = now.format(formatter);

        outputWriter.println(date);
    }
}