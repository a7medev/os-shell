package com.shell;

public enum TokenType {
    // Commands
    CLEAR, DATE, ECHO, UNAME, USERS,
    WHO, PWD, CD, LS, MAN, MKDIR,
    RMDIR, TOUCH, CP, MV, RM, CAT,
    MORE, LESS,

    // Operators
    GREATER, GREATER_GREATER, SMALLER,
    SMALLER_SMALLER, PIPE
}
