package semantics;

import semantics.exceptions.ArgumentWithoutFunction;
import semantics.exceptions.SymbolNameTakenException;
import semantics.exceptions.SymbolNotFoundException;
import semantics.model.Symbol;
import semantics.model.SymbolType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Stack;

public class SemanticAnalyzer {
    private MemoryHandler memoryHandler = new MemoryHandler();
    private Stack<String> semanticStack = new Stack<>();
    private ArrayList<String> programBlock = new ArrayList<>();

    // Program counter
    private int i = 0;

    public void callRoutine(String name, String input) {
        try {
            if (name.equals("push") || name.equals("pid"))
                getClass().getDeclaredMethod(name, String.class).invoke(this, input);
            else
                getClass().getDeclaredMethod(name).invoke(this);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String ss(int fromTop) {
        if (fromTop == 0)
            return semanticStack.peek();
        else
            return semanticStack.get(semanticStack.size() - fromTop - 1);
    }

    private void pop(int n) {
        for (int j = 0; j < n; j++) {
            semanticStack.pop();
        }
    }

    // TODO Handle this somehow....
    // the following methods are semantic routines

    void has_main() {
        System.out.println("Semantic routine called: has_main");
    }

    void push(String input) {
        System.out.println("Semantic routine called: push");
        semanticStack.push(input);
    }

    void var_dec() {
        String name = semanticStack.pop();
        String type = semanticStack.pop();
        if (type.equals("void")) {
            // TODO: 6/21/19 Error
            System.err.println("Var type is void");
        }
        try {
            memoryHandler.allocateVar(name);
        } catch (SymbolNameTakenException e) {
            // TODO: 6/21/19 Error
            System.err.println("Var name already taken");
        }
        System.out.println("Semantic routine called: var_dec");
    }

    void arr_dec() {
        int size = Integer.parseInt(semanticStack.pop());
        String name = semanticStack.pop();
        String type = semanticStack.pop();
        if (type.equals("void")) {
            // TODO: 6/21/19 Error
            System.err.println("Array type is void");
        }
        try {
            memoryHandler.allocateArray(name, size);
            int pointerAddress = memoryHandler.findAddress(name);
            programBlock.add(i, "(" + "ASSIGN," + "#" + (pointerAddress + MemoryHandler.VAR_SIZE) + "," + pointerAddress + ",)");
            i += 1;
        } catch (SymbolNameTakenException e) {
            // TODO: 6/21/19 Error
            System.err.println("Array name already taken");
        } catch (SymbolNotFoundException e) {
            // This should never happen
            System.err.println("WHAT THE FUCK?");
        }
        System.out.println("Semantic routine called: arr_dec");
    }

    void func_dec_begin() {
        System.out.println("Semantic routine called: func_dec_begin");

        String funcName = semanticStack.pop();
        String funcType = semanticStack.pop();
        SymbolType type = null;

        switch (funcType) {
            case "void":
                type = SymbolType.VOID_Function;
                break;
            case "int":
                type = SymbolType.INT_Function;
                break;
        }

        if (type == null) {
            System.err.println("This should not happen");
        }

        semanticStack.push("_FunctionBegin");
        semanticStack.push(funcName);

        memoryHandler.startNewScope();

        try {
            memoryHandler.allocateFunc(funcName, type);
        } catch (SymbolNameTakenException e) {
            System.err.println("Function name already taken");
        }
    }

    void func_dec_end() {
        System.out.println("Semantic routine called: func_dec_end");

        String functionName = semanticStack.peek();
        String functionBegin = semanticStack.get(semanticStack.size() - 2);
        assert functionBegin.equals("_FunctionBegin");
    }

    void add_func_to_symbol_table() {
        System.out.println("Semantic routine called: add_func_to_symbol_table");
        String functionName = semanticStack.pop();
        String functionBegin = semanticStack.pop();
        assert functionBegin.equals("_FunctionBegin");

        Symbol symbol = null;

        try {
            symbol = memoryHandler.findSymbol(functionName);
        } catch (SymbolNotFoundException e) {
            System.err.println("Function can not be found to add to previous table. Probably deleted it somewhere. check end.");
        }

        if (symbol == null) {
            System.err.println("Something went wrong");
        }

        memoryHandler.endNewScope();

        try {
            memoryHandler.addFunc(functionName, symbol);
        } catch (SymbolNameTakenException e) {
            System.err.println("Function name already taken");
        }
    }

    void arr_param() {
        System.out.println("Semantic routine called: arr_param");

        String name = semanticStack.pop();
        String type = semanticStack.pop();

        String functionName = semanticStack.peek();
        String functionBegin = semanticStack.get(semanticStack.size() - 2);
        assert functionBegin.equals("_FunctionBegin");

        if (type.equals("void")) {
            // TODO: 6/21/19 Error
            System.err.println("Function parameter can't be void");
        }

        try {
            int address = memoryHandler.allocatePointer(name);
            Symbol symbol = memoryHandler.findSymbol(functionName);
            symbol.addArgument(SymbolType.Pointer, address);
        } catch (SymbolNameTakenException e) {
            // TODO: 6/21/19 Error
            System.err.println("Array name already taken");
        } catch (ArgumentWithoutFunction argumentWithoutFunction) {
            argumentWithoutFunction.printStackTrace();
        } catch (SymbolNotFoundException e) {
            System.err.println("THIS SHOULD NOt BE Happening");
            e.printStackTrace();
        }
    }

    void var_param() {
        System.out.println("Semantic routine called: var_param");

        String name = semanticStack.pop();
        String type = semanticStack.pop();

        String functionName = semanticStack.peek();
        String functionBegin = semanticStack.get(semanticStack.size() - 2);
        assert functionBegin.equals("_FunctionBegin");


        if (type.equals("void")) {
            // TODO: 6/21/19 Error
            System.err.println("Function parameter can't be void");
        }

        try {
            int address = memoryHandler.allocateVar(name);
            Symbol symbol = memoryHandler.findSymbol(functionName);
            symbol.addArgument(SymbolType.Pointer, address);
        } catch (SymbolNameTakenException e) {
            // TODO: 6/21/19 Error
            System.err.println("Array name already taken");
        } catch (ArgumentWithoutFunction argumentWithoutFunction) {
            argumentWithoutFunction.printStackTrace();
        } catch (SymbolNotFoundException e) {
            System.err.println("THIS SHOULD NOt BE Happening 2");
            e.printStackTrace();
        }
    }

    void begin() {
        // Check out for _FunctionBegin. if it is not on top of stack add new scope

        String functionBegin = semanticStack.get(semanticStack.size() - 2);
        if (functionBegin.equals("_FunctionBegin")){
            // Do Nothing. Its all taken care of
        }else{
            memoryHandler.startNewScope();
        }

        System.out.println("Semantic routine called: begin");
    }

    void end() {
        // Check out for _FunctionBegin. if it is not on top of stack remove top scope

        String functionBegin = semanticStack.get(semanticStack.size() - 2);
        if (functionBegin.equals("_FunctionBegin")){
            // Do Nothing. Its all taken care of
        }else{
            memoryHandler.endNewScope();
        }

        System.out.println("Semantic routine called: end");
    }

    void continuez() {
        System.out.println("Semantic routine called: continue");
        // TODO
    }

    void breakz() {
        System.out.println("Semantic routine called: break");
        // TODO
    }

    void save() {
        System.out.println("Semantic routine called: save");

    }

    void jpf_save() {
        System.out.println("Semantic routine called: jpf_save");
    }

    void jp() {
        System.out.println("Semantic routine called: jp");
    }

    void label() {
        System.out.println("Semantic routine called: label");
    }

    void whilez() {
        System.out.println("Semantic routine called: while");
    }

    void return_void() {
        System.out.println("Semantic routine called: return_void");
    }

    void return_expr() {
        System.out.println("Semantic routine called: return_expr");
    }

    void switchz() {
        System.out.println("Semantic routine called: switch");
    }

    void start_scope_breakable() {
        System.out.println("Semantic routine called: start_scope_breakable");
    }

    void end_scope() {
        System.out.println("Semantic routine called: end_scope");
    }

    void pop() {
        System.out.println("Semantic routine called: pop");
    }

    void cmp() {
        System.out.println("Semantic routine called: cmp");
    }

    void jpf() {
        System.out.println("Semantic routine called: jpf");
    }

    void update_addr() {
        System.out.println("Semantic routine called: update_addr]");
    }

    void calc() {
        System.out.println("Semantic routine called: calc");
        String second = semanticStack.pop();
        String op = semanticStack.pop();
        String first = semanticStack.pop();
        String command = null;
        switch (op) {
            case "+":
                command = "ADD";
                break;
            case "-":
                command = "SUB";
                break;
            case "*":
                command = "MULT";
                break;
            case "<":
                command = "LT";
                break;
            case "==":
                command = "EQ";
                break;
        }
        int t = memoryHandler.getTemp();
        programBlock.add(i, "(" + command + "," + first + "," + second + "," + t + ")");
        i++;
        pop(3);
        semanticStack.push("" + t);
    }

    void push_minusone_mult() {
        System.out.println("Semantic routine called: push_minusone_mult");
        semanticStack.push("#-1");
        semanticStack.push("*");
    }

    void pid(String input) {
        System.out.println("Semantic routine called: pid");
        try {
            semanticStack.push("" + memoryHandler.findAddress(input));
        } catch (SymbolNotFoundException e) {
            // TODO: 6/28/19 Error
            System.err.println("Symbol Not found");
        }
    }

    void get_arr_element() {
        System.out.println("Semantic routine called: get_arr_element");
        int t = memoryHandler.getTemp();
        programBlock.add(i, "(ADD," + ss(1) + "," + ss(0) + "," + t + ")");
        i++;
        pop(2);
        semanticStack.push("" + t);
    }

    void start_set_param() {
        System.out.println("Semantic routine called: start_set_param");
        // TODO: 6/28/19 Fix following line for void functions
        semanticStack.push("2");
    }

    void end_set_param() {
        System.out.println("Semantic routine called: end_set_param");
        pop(1);
    }

    void call() {
        System.out.println("Semantic routine called: call");
        programBlock.add(i, "(ASSIGN," + (i + 2) + "," + ss(0) + ",)");
        i++;
        // TODO: 6/28/19 Implement getFunctionAddressByName to find address location in program block
//        programBlock.add(i, "(JP," + memoryHandler.getFunctionAddressByName(ss(1)));
        i++;
    }

    void set_param() {
        System.out.println("Semantic routine called: set_param");
        int paramAddress = Integer.parseInt(ss(1)) + Integer.parseInt(ss(2));
        programBlock.add(i, "(ASSIGN," + ss(0) + "," + paramAddress + ",)");
        i++;
        semanticStack.add(semanticStack.size() - 2, "" + Integer.parseInt(ss(1)) + 1);
        pop(1);
    }

    void assign() {
        System.out.println("Semantic routine called: assign");
        programBlock.add(i, "(ASSIGN," + ss(0) + "," + ss(1) + ",)");
        i++;
    }

}
