package semantics;

import io.OutputHandler;
import semantics.exceptions.*;
import semantics.model.Symbol;
import semantics.model.SymbolType;

import java.util.ArrayList;
import java.util.Stack;

public class SemanticAnalyzer {
    private MemoryHandler memoryHandler = new MemoryHandler();
    private Stack<String> semanticStack = new Stack<>();
    private ArrayList<String> programBlock = new ArrayList<>();


    private String outputParam = null;

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
            case "apply_break_address":
                apply_break_address();
                break;
            case "start_scope_breakable":
                start_scope_breakable();
                break;
            case "start_scope_breakable_2":
                start_scope_breakable_2();
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

        assert memoryHandler.getSymbolTableCount() == 1;

        Symbol main = memoryHandler.getFunctionByName("main");

        if (main == null || !main.isVoidFunc() || main.getArgCount() != 0) {
            OutputHandler.getInstance().printMainNotFoundError();
            return;
        }

        int jp_to_main = Integer.parseInt(semanticStack.pop());
        programBlock.remove(jp_to_main);
        programBlock.add(jp_to_main, "(JP, " + main.getAddress() + ",,)");

        for (int i = 0; i < programBlock.size(); i++) {
            OutputHandler.getInstance().printCode(programBlock.get(i), i);
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
            OutputHandler.getInstance().printIllegalTypeError(name);
//            System.err.println("Var type is void");
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
            OutputHandler.getInstance().printIllegalTypeError(name);
//            System.err.println("Array type is void");
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

        if (!functionName.equals("main")) {
            programBlock.add(i, "(JP, @" + symbol.getReturnAddress() + ",,)");
            i = i + 1;
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

        if (!memoryHandler.isInContinuableScope()) {
            // TODO: 6/28/19 Error
            OutputHandler.getInstance().printContinueError();
        } else {
            int continueAddress = memoryHandler.getScopeContinueAddress();
            programBlock.add(i, "(JP, " + continueAddress + ",,)");
            i = i + 1;
        }
    }

    void breakz() {
        System.out.println("Semantic routine called: break");


        if (!memoryHandler.isInBreakableScope()) {
            // TODO: 6/28/19 Error
            OutputHandler.getInstance().printBreakError();
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

        if(expression.equals("void")){
            OutputHandler.getInstance().printTypeMismatchError();
        }

//        semanticStack.push("" + returnValueAddress);
    }

    void apply_break_address() {
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

    void start_scope_breakable_2() {
        System.out.println("Semantic routine called: start_scope_breakable_2");

        int breakAddress = Integer.parseInt(ss(3));
        int startAddress = Integer.parseInt(ss(2));
        memoryHandler.startBreakableScope(breakAddress, startAddress);
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
        System.err.println("This will never get called ever....");
        System.exit(1);
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

        if (first.equals("void") || second.equals("void")) {
            OutputHandler.getInstance().printTypeMismatchError();
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
            if (!input.equals("output")) {
                semanticStack.push("" + memoryHandler.findAddress(input));
            } else {
                semanticStack.push(input);
            }
        } catch (SymbolNotFoundException e) {
            OutputHandler.getInstance().printScopingError(input);
//            System.err.println("Symbol Not found");
        }
    }


    void get_arr_element() {
        //TODO HERE
        System.out.println("Semantic routine called: get_arr_element");
        int t = memoryHandler.getTemp();

        String operand1 = ss(0);
        programBlock.add(i, "(MULT, " + ss(0) + ", #4, " + t + ")");
        i = i + 1;

        int t2 = memoryHandler.getTemp();
        String operand2 = ss(1);
        programBlock.add(i, "(ADD," + ss(1) + "," + t + "," + t2 + ")");
        i++;

        int t3 = memoryHandler.getTemp();
        programBlock.add(i, "(ASSIGN, @" + t2 + ", " + t3 + ",)");
        i = i + 1;

        if (operand1.equals("void") || operand2.equals("void")) {
            OutputHandler.getInstance().printTypeMismatchError();
        }

        pop(2);
        semanticStack.push("" + t3);
    }

    void start_set_param() {
        System.out.println("Semantic routine called: start_set_param");
        // TODO: 6/28/19 Fix following line for void functions
        semanticStack.push("0");
    }

    void end_set_param() {
        System.out.println("Semantic routine called: end_set_param");

        if (ss(1).equals("output")) {
            if (outputParam == null) {
                System.err.println("Output function needs atleast one parameter(Only last one is kept)");
            }
            pop(1);
            return;
        }

        int argumentNumbers = Integer.parseInt(semanticStack.pop());
        int functionStartAddress = Integer.parseInt(semanticStack.peek());

        String functionName = null;
        try {
            functionName = memoryHandler.getFunctionNameByStartAddress(functionStartAddress);
            Symbol function = memoryHandler.getFunctionByStartAddress(functionStartAddress);

            if (argumentNumbers != function.getArgCount()) {
                OutputHandler.getInstance().printArgMismatchError(functionName);
            }
        } catch (FunctionNotFoundException e) {
//            System.err.println("Your function does not exist. ....");
            // TODO TEST THIS. Using an undefined func
            OutputHandler.getInstance().printScopingError(functionName);

        }


    }

    void call() {
        System.out.println("Semantic routine called: call");

        if (semanticStack.peek().equals("output")) {
            pop(1);
            programBlock.add(i, "(PRINT, " + outputParam + ", , )");
            i = i + 1;
            outputParam = null;
            semanticStack.push("void");
            return;
        }

        int startAddress = Integer.parseInt(semanticStack.pop());
        try {
            Symbol function = memoryHandler.getFunctionByStartAddress(startAddress);
            int funcReturnAddress = function.getReturnAddress();
            programBlock.add(i, "(ASSIGN, #" + (i + 2) + ", " + funcReturnAddress + ",)");
            i = i + 1;

            programBlock.add(i, "(JP, " + startAddress + ",,)");
            i = i + 1;

            if (function.isIntFunc()) {
                semanticStack.push("" + function.getReturnValueAddress());
            } else if (function.isVoidFunc()) {
                semanticStack.push("void");
            }

        } catch (FunctionNotFoundException e) {
            System.err.println("YOu can never reach me :)))))))))))))");
        }
    }

    void set_param() {
        System.out.println("Semantic routine called: set_param");

        if (ss(2).equals("output")) {
            outputParam = semanticStack.pop();
            return;
        }

        int functionStartAddress = Integer.parseInt(ss(2));
        int argumentNumber = Integer.parseInt(ss(1));

        Symbol function = null;
        String functionName = null;
        try {
            function = memoryHandler.getFunctionByStartAddress(functionStartAddress);
            functionName = memoryHandler.getFunctionNameByStartAddress(functionStartAddress);
            int paramAddress = function.getArgAddress(argumentNumber);


            programBlock.add(i, "(ASSIGN," + ss(0) + "," + paramAddress + ",)");
            i++;
            pop(2);

            semanticStack.add("" + (argumentNumber + 1));
        } catch (FunctionNotFoundException e) {
            System.err.println("Function used cannot be found!!!!");
        } catch (TooMuchArgumentsException e) {
            OutputHandler.getInstance().printArgMismatchError(functionName);
            pop(1);
        }
    }

    void assign() {
        System.out.println("Semantic routine called: assign");
        int destination = Integer.parseInt(ss(1));
        String expression = ss(0);
        programBlock.add(i, "(ASSIGN," + ss(0) + "," + ss(1) + ",)");
        pop(2);
        semanticStack.push("" + destination);
        i++;

        if(expression.equals("void")){
            OutputHandler.getInstance().printTypeMismatchError();
        }
    }

}
