package lexanalyzer.models;

import lexanalyzer.enums.TokenType;

public class Token {

    private TokenType type;
    private String text;
    private int lineNumber;

    public Token(String text, TokenType type, int lineNumber) {
        this.text = text;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public boolean isError() {
        return type == TokenType.InvalidInput ||
                type == TokenType.MidParseEOF || type == TokenType.StuckWhileParsing;
    }

    public boolean isWhite() {
        return type == null || text == null || type == TokenType.COMMENT ||
                type == TokenType.WHITESPACE;

        // TODO Is this correct? (This part has been commented out
        // type == TokenType.EOF ;
    }

    public boolean isEOF() {
        return type == TokenType.EOF;
    }

    @Override
    public String toString() {
        // TODO: 3/28/19 considering isError and isWhite methods, do we still need to add the following ifs ??!!
//        if (this.type == null || this.text == null) {
//            return "";
//        }
//
//        if (this.type == TokenType.COMMENT || this.type == TokenType.EOF || this.type == TokenType.WHITESPACE) {
//            return "";
//        }

        return "(" + this.type.toString() + ", " + text.replaceAll("\n", "\\\\n") + ")";
    }
}
