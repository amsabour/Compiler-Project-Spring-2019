package lexanalyzer.enums;

public enum TokenType {
    NUM,
    ID,
    KEYWORD,
    SYMBOL,
    EOF,

    COMMENT,
    WHITESPACE,

    // Error Tokens
    InvalidInput,
    StuckWhileParsing,
    MidParseEOF;


    public static boolean isError(TokenType type) {
        switch (type) {
            case InvalidInput:
            case StuckWhileParsing:
            case MidParseEOF:
                return true;
            default:
                return false;
        }
    }
}
