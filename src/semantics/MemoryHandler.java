package semantics;

import semantics.exceptions.SymbolNameTakenException;
import semantics.exceptions.SymbolNotFoundException;
import semantics.model.SymbolTable;
import semantics.model.SymbolType;

import java.util.Stack;

import static semantics.model.SymbolType.*;

class MemoryHandler {
    private Stack<SymbolTable> symbolTables = new Stack<>();
    private int memPointer = 500;

    private static final int VAR_SIZE = 4;

    MemoryHandler() {
        symbolTables.push(new SymbolTable());
    }

    void allocateVar(String name) throws SymbolNameTakenException {
        symbolTables.peek().addSymbol(name, Variable, memPointer);
        memPointer += VAR_SIZE;
    }

    void allocateArray(String name, int size) throws SymbolNameTakenException {
        symbolTables.peek().addSymbol(name, Array, memPointer);
        memPointer += size * VAR_SIZE;
    }

    void allocateFunc(String name, int args) throws SymbolNameTakenException {
        // TODO Fix this (Get function type as input???)
        SymbolType functionType = SymbolType.VOID_Function;
        symbolTables.peek().addSymbol(name, functionType, memPointer);
        memPointer += args * VAR_SIZE;
    }

    int getTemp() {
        memPointer += VAR_SIZE;
        return memPointer - VAR_SIZE;
    }

    void startNewScope() {
        symbolTables.add(new SymbolTable());
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
}
