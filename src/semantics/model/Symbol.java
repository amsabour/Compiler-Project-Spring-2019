package semantics.model;

import semantics.exceptions.ArgumentWithoutFunction;

import java.util.ArrayList;

public class Symbol {
    private SymbolType type;
    private int address;

    private boolean isFunction = false;
    private ArrayList<SymbolType> argTypes;
    private ArrayList<Integer> argAddresses;


    private boolean isVoid = false;
    private int returnValueAddress = -1;
    private int returnAddress = -1;


    Symbol(int address, int returnAddress) {
        this.type = SymbolType.VOID_Function;
        this.argTypes = new ArrayList<>();
        this.argAddresses = new ArrayList<>();

        this.address = address;

        // This is a temp each function has which must be filled when calling a function
        this.returnAddress = returnAddress;

        this.isFunction = true;
        this.isVoid = true;
    }

    Symbol(int address, int returnAddress, int returnValueAddress) {
        this.type = SymbolType.INT_Function;
        this.argTypes = new ArrayList<>();
        this.argAddresses = new ArrayList<>();

        this.address = address;

        // This is a temp each function has which must be filled when calling a function
        this.returnAddress = returnAddress;

        this.returnValueAddress = returnValueAddress;

        this.isFunction = true;
        this.isVoid = false;
    }

    Symbol(SymbolType type, int address) {
        this.type = type;
        this.address = address;
    }


    SymbolType getType() {
        return type;
    }

    int getAddress() {
        return address;
    }


    public void addArgument(SymbolType type, int address) throws ArgumentWithoutFunction {
        if (!isFunction)
            throw new ArgumentWithoutFunction();
        if (type != SymbolType.Pointer && type != SymbolType.Variable) {
            System.err.println("WHAT THE FUCK IS HAPPENIGN?");
            System.exit(1);
        }

        argTypes.add(type);
        argAddresses.add(address);
    }
}

