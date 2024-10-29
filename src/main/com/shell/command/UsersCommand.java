package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;

public class UsersCommand implements Command {
    public static final String USERS_NAME = "users";
    public static final String WHO_NAME = "who";

    private final String user;

    public UsersCommand(String user) {
        this.user = user;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        outputWriter.println(user);
    }
}