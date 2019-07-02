package semantics;

import semantics.exceptions.FunctionNotFoundException;
import semantics.exceptions.SymbolNameTakenException;
import semantics.exceptions.SymbolNotFoundException;
import semantics.model.Symbol;
import semantics.model.SymbolTable;
import semantics.model.SymbolType;

import java.util.Stack;

import static semantics.model.SymbolType.*;

class MemoryHandler {
    private Stack<SymbolTable> symbolTables = new Stack<>();
    private int memPointer = 500;

    public static final int VAR_SIZE = 4;

    MemoryHandler() {
        symbolTables.push(new SymbolTable());
    }

    int allocateVar(String name) throws SymbolNameTakenException {
        int address = memPointer;
        symbolTables.peek().addSymbol(name, Variable, memPointer);
        memPointer += VAR_SIZE;
        return address;
    }

    int allocateArray(String name, int size) throws SymbolNameTakenException {
        int address = memPointer;
        symbolTables.peek().addSymbol(name, Pointer, memPointer);
        memPointer += VAR_SIZE;
        memPointer += size * VAR_SIZE;
        return address;
    }

    int allocatePointer(String name) throws SymbolNameTakenException {
        int address = memPointer;
        symbolTables.peek().addSymbol(name, Pointer, memPointer);
        memPointer += VAR_SIZE;
        return address;
    }

    int allocateFunc(String name, SymbolType type, int startLine) throws SymbolNameTakenException {
        int startAddress = startLine;
        int returnAddress = memPointer;
        memPointer += VAR_SIZE;


        if (type == SymbolType.INT_Function) {
            int returnValueAddress = memPointer;
            memPointer += VAR_SIZE;
            symbolTables.peek().addIntFunction(name, startAddress, returnAddress, returnValueAddress);
        } else if (type == VOID_Function) {
            symbolTables.peek().addVoidFunction(name, startAddress, returnAddress);
        }

        return startAddress;
    }

    void addFunc(String name, Symbol funcSymbol) throws SymbolNameTakenException {
        symbolTables.peek().putFunction(name, funcSymbol);
    }

    int getTemp() {
        memPointer += VAR_SIZE;
        return memPointer - VAR_SIZE;
    }

    void startNewScope() {
        symbolTables.add(new SymbolTable());
    }

    void startBreakableScope(int breakAddress) {
        symbolTables.add(new SymbolTable(breakAddress));
    }

    boolean isInBreakableScope() {
        boolean result = false;
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            result |= symbolTables.get(i).isBreakable();
        }
        return result;
    }

    int getScopeBreakAddress() {
        assert isInBreakableScope();
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).isBreakable()) {
                return symbolTables.get(i).getBreakAddress();
            }
        }

        System.err.println("THIS POINT MUST NEVER REACHHHH");
        return symbolTables.peek().getBreakAddress();
    }

    void endNewScope() {
        symbolTables.pop();
    }

    int findAddress(String name) throws SymbolNotFoundException {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            Integer address = symbolTables.get(i).getAddress(name);
            if (address != null) {
                return address;
            }
        }
        throw new SymbolNotFoundException(name);
    }

    Symbol getFunctionByStartAddress(int startAddress) throws FunctionNotFoundException {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            Symbol function = symbolTables.get(i).getFunctionByAddress(startAddress);
            if (function != null) {
                return function;
            }
        }
        throw new FunctionNotFoundException(startAddress);
    }

    Symbol findSymbol(String name) throws SymbolNotFoundException {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            Symbol address = symbolTables.get(i).getSymbol(name);
            if (address != null) {
                return address;
            }
        }
        throw new SymbolNotFoundException(name);
    }

    Symbol getCurrentFunction() {
        SymbolTable current = symbolTables.peek();
        String function = current.getFunction();
        return current.getSymbol(function);
    }

    Symbol getFunctionByName(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            Symbol symbol = symbolTables.get(i).getSymbol(name);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    int getSymbolTableCount(){
        return symbolTables.size();
    }


}
