package lexanalyzer.enums;

public enum TokenType {
    NUM,
    ID,
    KEYWORD,
    SYMBOL,
    COMMENT,
    WHITESPACE,
    EOF,
    InvalidInput,
    StuckWhileParsing,
    MidParseEOF,
}