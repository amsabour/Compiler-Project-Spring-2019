package Models;

import Enums.TokenType;

public class Token {

    private TokenType type;
    private String text;

    public Token(String text, TokenType type) {
        this.text = text;
        this.type = type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        if (this.type == null || this.text == null) {
            return "";
        }

        if (this.type == TokenType.COMMENT || this.type == TokenType.EOF || this.type == TokenType.WHITESPACE) {
            return "";
        }

        return "(" + this.type.toString() + ", " + text + ")";
    }
}
