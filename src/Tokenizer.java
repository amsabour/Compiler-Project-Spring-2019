import Enums.TokenType;

import java.util.Hashtable;

public class Tokenizer {

    private static Hashtable<String, Integer> states = new Hashtable<>();
    private static Hashtable<Integer, TokenType> acceptStates = new Hashtable<>();

    private static int[][] nextStates;

    static {
        int counter = 0;

        states.put("Start", counter);
        counter++;

        String[] keywords = {"if", "else", "void", "int", "while", "break", "continue", "switch", "default", "case", "return"};
        for (String keyword : keywords) {
            for (int i = 0; i < keyword.length(); i++) {
                if (!states.containsKey(keyword.substring(0, i + 1))) {
                    states.put(keyword.substring(0, i + 1), counter);
                    if (i + 1 == keyword.length()) {
                        acceptStates.put(counter, TokenType.KEYWORD);
                    } else {
                        acceptStates.put(counter, TokenType.ID);
                    }
                    counter++;
                }
            }
        }

        // ID
        states.put("ID", counter);
        acceptStates.put(counter, TokenType.ID);
        counter++;

        // NUM
        states.put("NUM", counter);
        acceptStates.put(counter, TokenType.NUM);
        counter++;

        // Symbols (1 letter) and ==
        states.put("SYMBOL", counter);
        acceptStates.put(counter, TokenType.SYMBOL);
        counter++;

        states.put("EQUALS", counter);
        acceptStates.put(counter, TokenType.SYMBOL);
        counter++;


        // All 1 letter whitespaces
        states.put("WHITESPACE", counter);
        acceptStates.put(counter, TokenType.WHITESPACE);
        counter++;

        // All Kinds of comments
        states.put("CommentStart", counter);
        counter++;

        states.put("InlineCommentStart", counter);
        counter++;

        states.put("InlineCommentAccept", counter);
        acceptStates.put(counter, TokenType.COMMENT);
        counter++;

        states.put("MultilineCommentStart", counter);
        counter++;

        states.put("MultilineCommentMid", counter);
        counter++;

        states.put("InlineCommentAccept", counter);
        acceptStates.put(counter, TokenType.COMMENT);
        counter++;


        // 128 is the length of the ascii table. Anything other than that is definitely an invalid char.
        // This will be our table for finding the next state.
        nextStates = new int[counter][128];
        for (int i = 0; i < counter; i++) {
            for (int j = 0; j < 128; j++) {
                nextStates[i][j] = -2;
            }
        }

        int[] validChars = {';', ':', ',', '[', ']', '(', ')', '{', '}', '+', '-', '*', '=', ',', '/', 32, 10, 13, 9, 11, 12};

        // Fill in table


    }

    public static void tokenize(String file) {

    }


    private TokenType isTokenValid(String token) {
        return null;
    }

    private boolean isWhitespace(String token) {
        if (token.length() != 1) {
            return false;
        }
        char[] whitespaces = {32, 10, 13, 9, 11, 12};
        for (char whitespace : whitespaces) {
            if (token.charAt(0) == whitespace) {
                return true;
            }
        }
        return false;
    }

    private boolean isNum(String token) {
        if (token.length() == 0) {
            return false;
        }

        for (int i = 0; i < token.length(); i++) {
            if (token.charAt(i) - '0' < 0 || token.charAt((i)) - '0' > 9) {
                return false;
            }
        }
        return true;
    }

    private boolean isID(String token) {
        if (token.length() < 1) {
            return false;
        }

        char firstChar = token.charAt(0);

        if ((firstChar < 'a' || firstChar > 'z') && (firstChar < 'A' || firstChar > 'Z')) {
            return false;
        }

        for (int i = 2; i < token.length(); i++) {
            char ithChar = token.charAt(i);
            if ((ithChar < 'a' || ithChar > 'z') && (ithChar < 'A' || ithChar > 'Z') && (ithChar < '0' || ithChar > '9')) {
                return false;
            }
        }

        return true;
    }

    private boolean isKeyword(String token) {
        String[] keywords = {"if", "else", "void", "int", "while", "break", "continue", "switch", "default", "case", "return"};

        for (String keyword : keywords) {
            if (keyword.equals(token))
                return true;
        }

        return false;
    }

    private boolean isSymbol(String token) {
        String[] symbols = {";", ":", ",", "[", "]", "(", ")", "{", "}", "+", "-", "*", "=", ",", "=="};
        for (String symbol : symbols) {
            if (symbol.equals(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEOF(String token) {
        return token.length() == 0;
    }

    private boolean isComment(String token) {
        return false;
    }
}
