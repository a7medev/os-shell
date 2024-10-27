package com.shell;

public class Token {
    final TokenType type;
    final String value;

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return type + " " + value;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Token token) {
            return token.type == type && value.equals(token.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + type.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
