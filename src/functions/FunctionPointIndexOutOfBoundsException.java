package functions;

/**
 * Thrown when an operation attempts to access a function point by an index that
 * falls outside of the available range.
 */
public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {

    public FunctionPointIndexOutOfBoundsException() {
    }

    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }

    public FunctionPointIndexOutOfBoundsException(int index, int size) {
        super("Index " + index + " is out of bounds for points count " + size);
    }
}
