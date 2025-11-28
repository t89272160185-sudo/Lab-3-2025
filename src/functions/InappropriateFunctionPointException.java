package functions;

/**
 * Thrown when an operation attempts to insert or modify a function point in a way
 * that breaks the ordering of the tabulated function.
 */
public class InappropriateFunctionPointException extends Exception {

    public InappropriateFunctionPointException() {
    }

    public InappropriateFunctionPointException(String message) {
        super(message);
    }
}
