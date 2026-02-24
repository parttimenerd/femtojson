package me.bechberger.util.json;

public class JSONParseException extends RuntimeException {

    public JSONParseException(int line, int column, String message) {
        super("Error at line " + line + ", column " + column + ": " + message);
    }
}