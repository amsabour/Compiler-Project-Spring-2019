package semantics.model;

import semantics.exceptions.ArgumentWithoutFunction;
import semantics.exceptions.TooMuchArgumentsException;

import java.util.ArrayList;

public class Symbol {
    private SymbolType type;
    private int address;

    private ArrayList<SymbolType> argTypes;
    private ArrayList<Integer> argAddresses;


    private int returnValueAddress = -1;
    private int returnAddress = -1;


    Symbol(int address, int returnAddress) {
        this.type = SymbolType.VOID_Function;
        this.argTypes = new ArrayList<>();
        this.argAddresses = new ArrayList<>();

        this.address = address;

        // This is a temp each function has which must be filled when calling a function
        this.returnAddress = returnAddress;

    }

    Symbol(int address, int returnAddress, int returnValueAddress) {
        this.type = SymbolType.INT_Function;
        this.argTypes = new ArrayList<>();
        this.argAddresses = new ArrayList<>();

        this.address = address;

        // This is a temp each function has which must be filled when calling a function
        this.returnAddress = returnAddress;

        this.returnValueAddress = returnValueAddress;

    }

    Symbol(SymbolType type, int address) {
        this.type = type;
        this.address = address;
    }


    SymbolType getType() {
        return type;
    }

    public int getAddress() {
        return address;
    }


    public void addArgument(SymbolType type, int address) throws ArgumentWithoutFunction {
        if (!isFunction())
            throw new ArgumentWithoutFunction();
        if (type != SymbolType.Pointer && type != SymbolType.Variable) {
            System.err.println("WHAT THE FUCK IS HAPPENIGN?");
            System.exit(1);
        }

        argTypes.add(type);
        argAddresses.add(address);
    }

    public boolean isIntFunc() {
        return this.type == SymbolType.INT_Function;
    }

    public boolean isVoidFunc() {
        return this.type == SymbolType.VOID_Function;
    }

    public boolean isFunction() {
        return isIntFunc() || isVoidFunc();
    }

    public int getReturnAddress() {
        if (!isFunction())
            throw new RuntimeException("Cannot get return address of non function symbol");

        return returnAddress;
    }

    public int getReturnValueAddress() {
        if (!isIntFunc())
            throw new RuntimeException("Cannot get return value address of non function or non int function symbol");

        return returnValueAddress;
    }

    public int getArgAddress(int index) throws TooMuchArgumentsException {
        if (index < argAddresses.size())
            return argAddresses.get(index);
        throw new TooMuchArgumentsException();
    }

    public int getArgCount() {
        assert isFunction();
        assert argAddresses.size() == argTypes.size();
        return argAddresses.size();
    }
}

