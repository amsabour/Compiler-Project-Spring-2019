package semantics;

import semantics.exceptions.ArgumentWithoutFunction;
import semantics.exceptions.SymbolNameTakenException;
import semantics.exceptions.SymbolNotFoundException;
import semantics.model.Symbol;
import semantics.model.SymbolType;

import java.util.ArrayList;
import java.util.Stack;

public class SemanticAnalyzer {
    private MemoryHandler memoryHandler = new MemoryHandler();
    private Stack<String> semanticStack = new Stack<>();
    private ArrayList<String> programBlock = new ArrayList<>();

    // Program counter
    private int i = 0;

    public void callRoutine(String name, String input) {

        switch (name) {
            case "push":
                push(input);
                break;
            case "push_num":
                push_num(input);
                break;
            case "pid":
                pid(input);
                break;
            case "has_main":
                has_main();
                break;
            case "var_dec":
                var_dec();
                break;
            case "arr_dec":
                arr_dec();
                break;
            case "func_dec_begin":
                func_dec_begin();
                break;
            case "func_dec_end":
                func_dec_end();
                break;
            case "add_func_to_symbol_table":
                add_func_to_symbol_table();
                break;
            case "var_param":
                var_param();
                break;
            case "arr_param":
                arr_param();
                break;
            case "begin":
                begin();
                break;
            case "end":
                end();
                break;
            case "continuez":
                continuez();
                break;
            case "breakz":
                breakz();
                break;
            case "save":
                save();
                break;
            case "jpf_save":
                jpf_save();
                break;
            case "jp":
                jp();
                break;
            case "label":
                label();
                break;
            case "whilez":
                whilez();
                break;
            case "return_void":
                return_void();
                break;
            case "return_expr":
                return_expr();
                break;
            case "switchz":
                switchz();
                break;
            case "start_scope_breakable":
                start_scope_breakable();
                break;
            case "end_scope_breakable":
                end_scope_breakable();
                break;
            case "pop":
                pop();
                break;
            case "cmp":
                cmp();
                break;
            case "jpf":
                jpf();
                break;
            case "update_addr":
                update_addr();
                break;
            case "calc":
                calc();
                break;
            case "push_minusone_mult":
                push_minusone_mult();
                break;
            case "get_arr_element":
                get_arr_element();
                break;
            case "start_set_param":
                start_set_param();
                break;
            case "end_set_param":
                end_set_param();
                break;
            case "call":
                call();
                break;
            case "set_param":
                set_param();
                break;
            case "assign":
                assign();
                break;
        }

//        try {
//            if (name.equals("push") || name.equals("pid"))
//                getClass().getDeclaredMethod(name, String.class).invoke(this, input);
//            else
//                getClass().getDeclaredMethod(name).invoke(this);
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
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
        // TODO
        System.out.println("Semantic routine called: has_main");

        for (int i = 0; i < programBlock.size(); i++) {
            String s = programBlock.get(i);
            System.out.println("" + i + ": " + s);
        }
    }

    void push(String input) {
        System.out.println("Semantic routine called: push");
        semanticStack.push(input);
    }

    void push_num(String input) {
        System.out.println("Semantic routine called: push_num");
        semanticStack.push("#" + input);
    }

    void var_dec() {
        String name = ss(0);
        String type = ss(1);
        pop(2);
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
        int size = Integer.parseInt(semanticStack.pop().substring(1));
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
            memoryHandler.allocateFunc(funcName, type, i);
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
            symbol.addArgument(SymbolType.Variable, address);
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
        if (functionBegin.equals("_FunctionBegin")) {
            // Do Nothing. Its all taken care of
        } else {
            memoryHandler.startNewScope();
        }

        System.out.println("Semantic routine called: begin");
    }

    void end() {
        // Check out for _FunctionBegin. if it is not on top of stack remove top scope

        String functionBegin = semanticStack.get(semanticStack.size() - 2);
        if (functionBegin.equals("_FunctionBegin")) {
            // Do Nothing. Its all taken care of
        } else {
            memoryHandler.endNewScope();
        }

        System.out.println("Semantic routine called: end");
    }

    void continuez() {
        System.out.println("Semantic routine called: continue");

        String label = ss(2);
        programBlock.add(i, "(JP, " + label + ",,)");
        i = i + 1;
    }

    void breakz() {
        System.out.println("Semantic routine called: break");

        // TODO Breaks dont work in while loops yet

        if (!memoryHandler.isInBreakableScope()) {
            // TODO: 6/28/19 Error
            System.err.println("break not inside of breakable scope");
        } else {
            int breakAddress = memoryHandler.getScopeBreakAddress();
            programBlock.add(i, "(JP, " + breakAddress + ",,)");
            i = i + 1;
        }
    }

    void save() {
        System.out.println("Semantic routine called: save");
        semanticStack.push("" + i);
        programBlock.add(i, "EMPTY FOR NOW (SAVE)");
        i = i + 1;
    }

    void jpf_save() {
        System.out.println("Semantic routine called: jpf_save");

        int jpf_line = Integer.parseInt(semanticStack.pop());
        String expression = semanticStack.pop();

        programBlock.remove(jpf_line);
        programBlock.add(jpf_line, "(JPF, " + expression + ", " + (i + 1) + ",)");

        semanticStack.push("" + i);
        programBlock.add(i, "EMPTY FOR NOW (SAVE)");
        i = i + 1;
    }

    void jp() {
        System.out.println("Semantic routine called: jp");

        int jp_line = Integer.parseInt(semanticStack.pop());
        programBlock.remove(jp_line);
        programBlock.add(jp_line, "(JP, " + i + ", , )");
    }

    void label() {
        System.out.println("Semantic routine called: label");
        semanticStack.push("" + i);
    }

    void whilez() {
        System.out.println("Semantic routine called: while");

        int jpf_line = Integer.parseInt(semanticStack.pop());
        String expression = semanticStack.pop();
        int loop_addr = Integer.parseInt(semanticStack.pop());

        programBlock.add(i, "(JP, " + loop_addr + ",,)");
        i = i + 1;


        programBlock.remove(jpf_line);
        programBlock.add(jpf_line, "(JPF, " + expression + ", " + i + ",)");

    }

    void return_void() {
        System.out.println("Semantic routine called: return_void");

        Symbol function = memoryHandler.getCurrentFunction();
        if (function == null) {
            System.err.println("Return but outside of all functions");
            return;
        }


        if (!function.isVoidFunc()) {
            System.err.println("Return void inside non void function!!!!!!!!");
            return;
        }

        int returnAddress = function.getReturnAddress();
        programBlock.add(i, "(JP, @" + returnAddress + ",,)");
        i = i + 1;

        semanticStack.push("void");
    }

    void return_expr() {
        System.out.println("Semantic routine called: return_expr");

        Symbol function = memoryHandler.getCurrentFunction();
        if (function == null) {
            System.err.println("Return but outside of all functions");
            return;
        }


        if (!function.isIntFunc()) {
            System.err.println("Return expression inside non int function!!!!!!!!");
            return;
        }


        int returnAddress = function.getReturnAddress();
        int returnValueAddress = function.getReturnValueAddress();

        String expression = semanticStack.pop();
        programBlock.add(i, "(ASSIGN, " + expression + ", " + returnValueAddress + ", )");
        i = i + 1;
        programBlock.add(i, "(JP, @" + returnAddress + ",,)");
        i = i + 1;

        semanticStack.push("" + returnValueAddress);
    }

    void switchz() {
        System.out.println("Semantic routine called: switch");
        programBlock.add(i, "(JP, " + (i + 2) + ", ,)");
        i = i + 1;
        programBlock.add(i, "EMPTY FOR NOW");
        semanticStack.push("" + i);
        i = i + 1;
    }

    void start_scope_breakable() {
        System.out.println("Semantic routine called: start_scope_breakable");

        int breakAddress = Integer.parseInt(ss(1));
        memoryHandler.startBreakableScope(breakAddress);
    }

    void end_scope_breakable() {
        // This is only for switch
        System.out.println("Semantic routine called: end_scope_breakable");
        int jp_line = Integer.parseInt(ss(0));
        pop(1);

        programBlock.remove(jp_line);
        programBlock.add(jp_line, "(JP, " + i + ", , )");

        memoryHandler.endNewScope();
    }

    void pop() {
        System.out.println("Semantic routine called: pop");
        semanticStack.pop();
    }

    void cmp() {
        System.out.println("Semantic routine called: cmp");

        String case_value = ss(0);
        String switch_expr = ss(1);
        pop(1);

        int t = memoryHandler.getTemp();
        programBlock.add(i, "(EQ, " + switch_expr + ", " + case_value + ", " + t + ")");
        i = i + 1;

        semanticStack.push("" + t);
    }

    void jpf() {
        System.out.println("Semantic routine called: jpf");

        int jpf_line = Integer.parseInt(ss(0));
        String eq = ss(1);
        pop(2);

        programBlock.remove(jpf_line);
        programBlock.add(jpf_line, "(JPF, " + eq + "," + i + ",)");
    }

    void update_addr() {
        System.out.println("Semantic routine called: update_addr]");
        // TODO
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
        pop(2);
        i++;
    }

}
