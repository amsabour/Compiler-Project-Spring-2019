package parser.models;

import lexanalyzer.Tokenizer;
import lexanalyzer.models.Token;

import java.util.*;

public class Parser {

    private static Set<String> nonTerminals = new HashSet<>();
    private static Set<String> terminals = new HashSet<>();

    private static HashMap<String, Set<String>> firsts = new HashMap<>();
    private static HashMap<String, Set<String>> follows = new HashMap<>();

    private static HashMap<String, List<Rule>> rules = new HashMap<>();


    // All of this has been obtained from using the grammar on this website : https://mikedevice.github.io/first-follow/
    // Grammar used will be included
    // Initialize nonTerminals
    private static void initNonTerminals() {
        nonTerminals.add("Program");
        nonTerminals.add("Declaration-list");
        nonTerminals.add("Declaration");
        nonTerminals.add("Declaration-2");
        nonTerminals.add("Type-specifier");
        nonTerminals.add("Params");
        nonTerminals.add("Params-2");
        nonTerminals.add("Params-list");
        nonTerminals.add("Params-list-2");
        nonTerminals.add("Param");
        nonTerminals.add("Param-2");
        nonTerminals.add("Compound-stmt");
        nonTerminals.add("Statement-list");
        nonTerminals.add("Statement");
        nonTerminals.add("Expression-stmt");
        nonTerminals.add("Selection-stmt");
        nonTerminals.add("Iteration-stmt");
        nonTerminals.add("Return-stmt");
        nonTerminals.add("Return-stmt-2");
        nonTerminals.add("Switch-stmt");
        nonTerminals.add("Case-stmts");
        nonTerminals.add("Case-stmt");
        nonTerminals.add("Default-stmt");
        nonTerminals.add("Var");
        nonTerminals.add("Var-2");
        nonTerminals.add("Simple-expression");
        nonTerminals.add("Simple-expression-2");
        nonTerminals.add("Relop");
        nonTerminals.add("Additive-expression");
        nonTerminals.add("Additive-expression-2");
        nonTerminals.add("Addop");
        nonTerminals.add("Term");
        nonTerminals.add("Term-2");
        nonTerminals.add("Signed-Factor");
        nonTerminals.add("Factor");
        nonTerminals.add("Factor-2");
        nonTerminals.add("Args");
        nonTerminals.add("Arg-list");
        nonTerminals.add("Arg-list-2");
        nonTerminals.add("Expression");
        nonTerminals.add("Expression-2");
        nonTerminals.add("Expression-3");
        nonTerminals.add("Expression-4");
    }

    // Initialize terminals
    private static void initTerminals() {
        // This is epsilon
        terminals.add("eps");

        terminals.add("eof");

        terminals.add("id");

        terminals.add("num");


        terminals.add("-");
        terminals.add("(");
        terminals.add(")");
        terminals.add("{");
        terminals.add("}");
        terminals.add("==");
        terminals.add(",");
        terminals.add("+");
        terminals.add("=");
        terminals.add(";");
        terminals.add("[");
        terminals.add("]");
        terminals.add("*");
        terminals.add("<");


        terminals.add("int");
        terminals.add("if");
        terminals.add("continue");
        terminals.add("case");
        terminals.add("return");
        terminals.add("default");
        terminals.add("break");
        terminals.add("void");
        terminals.add("switch");
        terminals.add("while");
    }

    // Initialize firsts
    private static void initFirsts() {
        firsts.put("Program", new HashSet<>(Arrays.asList("eof", "int", "void")));
        firsts.put("Declaration-list", new HashSet<>(Arrays.asList("eps", "int", "void")));
        firsts.put("Declaration", new HashSet<>(Arrays.asList("int", "void")));
        firsts.put("Declaration-2", new HashSet<>(Arrays.asList(";", "[", "(")));
        firsts.put("Type-specifier", new HashSet<>(Arrays.asList("int", "void")));
        firsts.put("Params", new HashSet<>(Arrays.asList("int", "void")));
        firsts.put("Params-2", new HashSet<>(Arrays.asList("id", "eps")));
        firsts.put("Params-list", new HashSet<>(Arrays.asList("int", "void")));
        firsts.put("Params-list-2", new HashSet<>(Arrays.asList(",", "eps")));
        firsts.put("Param", new HashSet<>(Arrays.asList("int", "void")));
        firsts.put("Param-2", new HashSet<>(Arrays.asList("[", "eps")));

        firsts.put("Compound-stmt", new HashSet<>(Collections.singletonList("{")));
        firsts.put("Statement-list", new HashSet<>(Arrays.asList("eps", "{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id")));
        firsts.put("Statement", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id")));
        firsts.put("Expression-stmt", new HashSet<>(Arrays.asList("continue", "break", ";", "+", "-", "(", "num", "id")));
        firsts.put("Selection-stmt", new HashSet<>(Arrays.asList("if")));
        firsts.put("Iteration-stmt", new HashSet<>(Arrays.asList("while")));
        firsts.put("Return-stmt", new HashSet<>(Arrays.asList("return")));
        firsts.put("Return-stmt-2", new HashSet<>(Arrays.asList(";", "+", "-", "(", "num", "id")));
        firsts.put("Switch-stmt", new HashSet<>(Arrays.asList("switch")));
        firsts.put("Case-stmts", new HashSet<>(Arrays.asList("eps", "case")));
        firsts.put("Case-stmt", new HashSet<>(Arrays.asList("case")));
        firsts.put("Default-stmt", new HashSet<>(Arrays.asList("default", "eps")));
        firsts.put("Var", new HashSet<>(Arrays.asList("id")));

        firsts.put("Var-2", new HashSet<>(Arrays.asList("[", "eps")));
        firsts.put("Simple-expression", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));
        firsts.put("Simple-expression-2", new HashSet<>(Arrays.asList("eps", "<", "==")));
        firsts.put("Relop", new HashSet<>(Arrays.asList("<", "==")));

        firsts.put("Additive-expression", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));
        firsts.put("Additive-expression-2", new HashSet<>(Arrays.asList("eps", "+", "-")));
        firsts.put("Addop", new HashSet<>(Arrays.asList("+", "-")));
        firsts.put("Term", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));
        firsts.put("Term-2", new HashSet<>(Arrays.asList("eps", "*")));

        firsts.put("Signed-Factor", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));
        firsts.put("Factor", new HashSet<>(Arrays.asList("(", "num", "id")));
        firsts.put("Factor-2", new HashSet<>(Arrays.asList("[", "(", "eps")));

        firsts.put("Args", new HashSet<>(Arrays.asList("eps", "+", "-", "(", "num", "id")));
        firsts.put("Arg-list", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));
        firsts.put("Arg-list-2", new HashSet<>(Arrays.asList(",", "eps")));

        firsts.put("Expression", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));
        firsts.put("Expression-2", new HashSet<>(Arrays.asList("[", "(", "*", "<", "+", "-", "=", "eps")));
        firsts.put("Expression-3", new HashSet<>(Arrays.asList("*", "<", "+", "-", "=", "eps")));
        firsts.put("Expression-4", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id", "=")));

        for (String terminal : terminals) {
            firsts.put(terminal, new HashSet<>(Collections.singletonList(terminal)));
        }
    }

    // Initialize follows
    private static void initFollows() {
        follows.put("Program", new HashSet<>(Arrays.asList("$")));
        follows.put("Declaration-list", new HashSet<>(Arrays.asList("eof", "{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}")));
        follows.put("Declaration", new HashSet<>(Arrays.asList("int", "void", "eof", "{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}")));
        follows.put("Declaration-2", new HashSet<>(Arrays.asList("int", "void", "eof", "{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}")));
        follows.put("Type-specifier", new HashSet<>(Arrays.asList("id")));
        follows.put("Params", new HashSet<>(Arrays.asList(")")));
        follows.put("Params-2", new HashSet<>(Arrays.asList(")")));
        follows.put("Params-list", new HashSet<>(Arrays.asList(")")));
        follows.put("Params-list-2", new HashSet<>(Arrays.asList(")")));
        follows.put("Param", new HashSet<>(Arrays.asList(",", ")")));
        follows.put("Param-2", new HashSet<>(Arrays.asList(",", ")")));


        follows.put("Compound-stmt", new HashSet<>(Arrays.asList("int", "void", "eof", "{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));
        follows.put("Statement-list", new HashSet<>(Arrays.asList("}", "case", "default")));

        follows.put("Statement", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));
        follows.put("Expression-stmt", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));
        follows.put("Selection-stmt", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));
        follows.put("Iteration-stmt", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));
        follows.put("Return-stmt", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));
        follows.put("Return-stmt-2", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));
        follows.put("Switch-stmt", new HashSet<>(Arrays.asList("{", "continue", "break", ";", "if", "while", "return", "switch", "+", "-", "(", "num", "id", "}", "else", "case", "default")));


        follows.put("Case-stmts", new HashSet<>(Arrays.asList("default", "}")));
        follows.put("Case-stmt", new HashSet<>(Arrays.asList("case", "default", "}")));
        follows.put("Default-stmt", new HashSet<>(Arrays.asList("}")));
        follows.put("Var", new HashSet<>(Arrays.asList()));
        follows.put("Var-2", new HashSet<>(Arrays.asList()));
        follows.put("Simple-expression", new HashSet<>(Arrays.asList()));

        follows.put("Simple-expression-2", new HashSet<>(Arrays.asList(";", ")", "]", ",")));
        follows.put("Relop", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));
        follows.put("Additive-expression", new HashSet<>(Arrays.asList("<", "==", ";", ")", "]", ",")));
        follows.put("Additive-expression-2", new HashSet<>(Arrays.asList("<", "==", ";", ")", "]", ",")));

        follows.put("Addop", new HashSet<>(Arrays.asList("+", "-", "(", "num", "id")));

        follows.put("Term", new HashSet<>(Arrays.asList("+", "-", "<", "==", ";", ")", "]", ",")));
        follows.put("Term-2", new HashSet<>(Arrays.asList("+", "-", "<", "==", ";", ")", "]", ",")));
        follows.put("Signed-Factor", new HashSet<>(Arrays.asList("*", "+", "-", "<", "==", ";", ")", "]", ",")));
        follows.put("Factor", new HashSet<>(Arrays.asList("*", "+", "-", "<", "==", ";", ")", "]", ",")));
        follows.put("Factor-2", new HashSet<>(Arrays.asList("*", "+", "-", "<", "==", ";", ")", "]", ",")));
        follows.put("Args", new HashSet<>(Arrays.asList(")")));
        follows.put("Arg-list", new HashSet<>(Arrays.asList(")")));
        follows.put("Arg-list-2", new HashSet<>(Arrays.asList(")")));
        follows.put("Expression", new HashSet<>(Arrays.asList(";", ")", "]", ",")));
        follows.put("Expression-2", new HashSet<>(Arrays.asList(";", ")", "]", ",")));
        follows.put("Expression-3", new HashSet<>(Arrays.asList(";", ")", "]", ",")));
        follows.put("Expression-4", new HashSet<>(Arrays.asList(";", ")", "]", ",")));
    }

    // Initialize rules
    private static void initRules() {
        rules.put("Program", Arrays.asList(new Rule(Arrays.asList("Declaration-list", "eof"))));
        rules.put("Declaration-list", Arrays.asList(new Rule(Arrays.asList("Declaration", "Declaration-list")), new Rule(Arrays.asList("eps"))));
        rules.put("Declaration", Arrays.asList(new Rule(Arrays.asList("Type-specifier", "id", "Declaration-2"))));
        rules.put("Declaration-2", Arrays.asList(new Rule(Arrays.asList(";")), new Rule(Arrays.asList("[", "num", "]", ";")), new Rule(Arrays.asList("(", "Params", ")", "Compound-stmt"))));
        rules.put("Type-specifier", Arrays.asList(new Rule(Arrays.asList("int")), new Rule(Arrays.asList("void"))));
        rules.put("Params", Arrays.asList(new Rule(Arrays.asList("int", "id", "Param-2", "Params-list-2")), new Rule(Arrays.asList("void", "Params-2"))));
        rules.put("Params-2", Arrays.asList(new Rule(Arrays.asList("id", "Param-2", "Params-list-2")), new Rule(Arrays.asList("eps"))));
        rules.put("Params-list", Arrays.asList(new Rule(Arrays.asList("Param", "Params-list-2"))));
        rules.put("Params-list-2", Arrays.asList(new Rule(Arrays.asList(",", "Params-list")), new Rule(Arrays.asList("eps"))));
        rules.put("Param", Arrays.asList(new Rule(Arrays.asList("Type-specifier", "id", "Param-2"))));
        rules.put("Param-2", Arrays.asList(new Rule(Arrays.asList("[", "]")), new Rule(Arrays.asList("eps"))));
        rules.put("Compound-stmt", Arrays.asList(new Rule(Arrays.asList("{", "Declaration-list", "Statement-list", "}"))));
        rules.put("Statement-list", Arrays.asList(new Rule(Arrays.asList("Statement", "Statement-list")), new Rule(Arrays.asList("eps"))));
        rules.put("Statement", Arrays.asList(new Rule(Arrays.asList("Expression-stmt")), new Rule(Arrays.asList("Compound-stmt")), new Rule(Arrays.asList("Selection-stmt")), new Rule(Arrays.asList("Iteration-stmt")), new Rule(Arrays.asList("Return-stmt")), new Rule(Arrays.asList("Switch-stmt"))));
        rules.put("Expression-stmt", Arrays.asList(new Rule(Arrays.asList("Expression", ";")), new Rule(Arrays.asList("continue", ";")), new Rule(Arrays.asList("break", ";")), new Rule(Arrays.asList(";"))));
        rules.put("Selection-stmt", Arrays.asList(new Rule(Arrays.asList("if", "(", "Expression", ")", "Statement", "else", "Statement"))));
        rules.put("Iteration-stmt", Arrays.asList(new Rule(Arrays.asList("while", "(", "Expression", ")", "Statement"))));
        rules.put("Return-stmt", Arrays.asList(new Rule(Arrays.asList("return", "Return-stmt-2"))));
        rules.put("Return-stmt-2", Arrays.asList(new Rule(Arrays.asList(";")), new Rule(Arrays.asList("Expression", ";"))));
        rules.put("Switch-stmt", Arrays.asList(new Rule(Arrays.asList("switch", "(", "Expression", ")", "{", "Case-stmts", "Default-stmt", "}"))));
        rules.put("Case-stmts", Arrays.asList(new Rule(Arrays.asList("Case-stmt", "Case-stmts")), new Rule(Arrays.asList("eps"))));
        rules.put("Case-stmt", Arrays.asList(new Rule(Arrays.asList("case", "num", ":", "Statement-list"))));
        rules.put("Default-stmt", Arrays.asList(new Rule(Arrays.asList("default", ":", "Statement-list")), new Rule(Arrays.asList("eps"))));
        rules.put("Var", Arrays.asList(new Rule(Arrays.asList("id", "Var-2"))));
        rules.put("Var-2", Arrays.asList(new Rule(Arrays.asList("[", "Expression", "]")), new Rule(Arrays.asList("eps"))));
        rules.put("Simple-expression", Arrays.asList(new Rule(Arrays.asList("Additive-expression", "Simple-expression-2"))));
        rules.put("Simple-expression-2", Arrays.asList(new Rule(Arrays.asList("Relop", "Additive-expression")), new Rule(Arrays.asList("eps"))));
        rules.put("Relop", Arrays.asList(new Rule(Arrays.asList("<")), new Rule(Arrays.asList("=="))));
        rules.put("Additive-expression", Arrays.asList(new Rule(Arrays.asList("Term", "Additive-expression-2"))));
        rules.put("Additive-expression-2", Arrays.asList(new Rule(Arrays.asList("Addop", "Additive-expression")), new Rule(Arrays.asList("eps"))));
        rules.put("Addop", Arrays.asList(new Rule(Arrays.asList("+")), new Rule(Arrays.asList("-"))));
        rules.put("Term", Arrays.asList(new Rule(Arrays.asList("Signed-Factor", "Term-2"))));
        rules.put("Term-2", Arrays.asList(new Rule(Arrays.asList("eps")), new Rule(Arrays.asList("*", "Term"))));
        rules.put("Signed-Factor", Arrays.asList(new Rule(Arrays.asList("Factor")), new Rule(Arrays.asList("+", "Factor")), new Rule(Arrays.asList("-", "Factor"))));
        rules.put("Factor", Arrays.asList(new Rule(Arrays.asList("(", "Expression", ")")), new Rule(Arrays.asList("num")), new Rule(Arrays.asList("id", "Factor-2"))));
        rules.put("Factor-2", Arrays.asList(new Rule(Arrays.asList("[", "Expression", "]")), new Rule(Arrays.asList("(", "Args", ")")), new Rule(Arrays.asList("eps"))));
        rules.put("Args", Arrays.asList(new Rule(Arrays.asList("Arg-list")), new Rule(Arrays.asList("eps"))));
        rules.put("Arg-list", Arrays.asList(new Rule(Arrays.asList("Expression", "Arg-list-2"))));
        rules.put("Arg-list-2", Arrays.asList(new Rule(Arrays.asList(",", "Arg-list")), new Rule(Arrays.asList("eps"))));
        rules.put("Expression", Arrays.asList(new Rule(Arrays.asList("+", "Factor", "Term-2", "Additive-expression-2", "Simple-expression-2")), new Rule(Arrays.asList("-", "Factor", "Term-2", "Additive-expression-2", "Simple-expression-2")), new Rule(Arrays.asList("(", "Expression", ")", "Term-2", "Additive-expression-2", "Simple-expression-2")), new Rule(Arrays.asList("num", "Term-2", "Additive-expression-2", "Simple-expression-2")), new Rule(Arrays.asList("id", "Expression-2"))));
        rules.put("Expression-2", Arrays.asList(new Rule(Arrays.asList("[", "Expression", "]", "Expression-3")), new Rule(Arrays.asList("Expression-3")), new Rule(Arrays.asList("(", "Args", ")", "Term-2", "Additive-expression-2", "Simple-expression-2"))));
        rules.put("Expression-3", Arrays.asList(new Rule(Arrays.asList("*", "Term", "Additive-expression-2", "Simple-expression-2")), new Rule(Arrays.asList("<", "Additive-expression")), new Rule(Arrays.asList("Addop", "Additive-expression", "Simple-expression-2")), new Rule(Arrays.asList("=", "Expression-4")), new Rule(Arrays.asList("eps"))));
        rules.put("Expression-4", Arrays.asList(new Rule(Arrays.asList("Expression")), new Rule(Arrays.asList("=", "Additive-expression"))));
    }

    static {
        initNonTerminals();
        initTerminals();
        initFirsts();
        initFollows();
        initRules();
    }


    private Tokenizer tokenizer;
    private Token token = null;
    private int depth = -1;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;

        // Initialize token
        this.token = tokenizer.getNextToken();
    }


    public void startParse() {
        parse("Program");
    }

    private void parse(String state) {
        depth++;

        for (int i = 0; i < depth; i++) {
            System.out.print("|\t");
        }
        System.out.println(state);

        if (state.equals("eps")) {
            depth--;
            return;
        }


        if (!terminals.contains(state) && !nonTerminals.contains(state)) {
            System.err.println("WHAT THE FUCK???");
            depth--;
            return;
        }

        // Parse one terminal
        if (terminals.contains(state)) {
            if (isTokenMatch(state)) {

                // This token has been parsed
                if (!token.isEOF())
                    token = tokenizer.getNextToken();
            } else {
                System.err.println("Terminal state isn't the token");
            }
        }

        // Parse a non terminal
        if (nonTerminals.contains(state)) {
            Rule goalRule = null;
            for (Rule rule : rules.get(state)) {
                if (isRuleValid(state, rule)) {
                    goalRule = rule;
                    break;
                }
            }

            if (goalRule != null) {
                for (String component : goalRule.getComponents()) {
                    parse(component);
                }
            } else {
                // None of the rules match
                System.err.println("No rules match!");
            }
        }

        depth--;
    }


    private boolean isRuleValid(String state, Rule rule) {
        String tokenString = getTokenString();
        String firstComponent = rule.getFirstComponent();

        Set<String> first = firsts.get(firstComponent);

        boolean isInFirst = first.contains(tokenString);
        boolean isInFollow = firsts.get(state).contains("eps") && follows.get(state).contains(tokenString) && rule.isNullable();

        return isInFirst ||
                isInFollow;
    }

    private String getTokenString() {
        switch (token.getType()) {
            case SYMBOL:
            case EOF:
            case KEYWORD:
                return token.getText().trim().toLowerCase();

            case NUM:
                return "num";
            case ID:
                return "id";
            default:
                return "";
        }
    }

    private boolean isTokenMatch(String state) {
        return state.equals(getTokenString());
    }


    static class Rule {
        private List<String> comps;

        public Rule(List<String> comps) {
            this.comps = comps;
        }

        public String getFirstComponent() {
            return comps.get(0);
        }

        public List<String> getComponents() {
            return this.comps;
        }

        public boolean isNullable() {
            boolean result = true;
            for (String component : comps) {
                result = firsts.get(component).contains("eps");
                if (!result)
                    break;
            }
            return result;
        }
    }
}



