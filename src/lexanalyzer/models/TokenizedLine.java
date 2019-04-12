package lexanalyzer.models;

import java.util.ArrayList;

public class TokenizedLine {
    private int lineNumber;
    private ArrayList<Token> tokens;

    public TokenizedLine(int lineNumber, ArrayList<Token> tokens) {
        this.lineNumber = lineNumber;
        this.tokens = tokens;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
