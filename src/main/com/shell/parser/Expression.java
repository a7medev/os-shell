package com.shell.parser;

import java.util.List;

public sealed interface Expression {

    record Command(String cmd, List<String> args, List<String> flags, Expression redirections) implements Expression {}

    record PipedCommands(Expression left, Expression right) implements Expression {}

    record OutputRedirection(String file) implements Expression {}
    record AppendOutputRedirection(String file) implements Expression {}
    record InputRedirection(String file) implements Expression {}
}
