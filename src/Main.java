import functions.ArrayTabulatedFunction;
import functions.FunctionPoint;
import functions.FunctionPointIndexOutOfBoundsException;
import functions.InappropriateFunctionPointException;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

public class Main {
    public static void main(String[] args) {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(0.0, Math.PI, 6);
        TabulatedFunction listFunction = new LinkedListTabulatedFunction(0.0, Math.PI, 6);

        demonstrateImplementation("Array-based implementation", arrayFunction);
        demonstrateImplementation("Linked-list implementation", listFunction);

        demonstrateExceptions(new ArrayTabulatedFunction(0.0, 2.0, 3));
        demonstrateExceptions(new LinkedListTabulatedFunction(0.0, 2.0, 3));
    }

    private static void demonstrateImplementation(String title, TabulatedFunction function) {
        try {
            fillWithSineValues(function);
            printTabulatedPoints(title + ": initial state", function);

            double newY = function.getPointY(2) + 0.3;
            function.setPoint(2, new FunctionPoint(function.getPointX(2), newY));

            function.addPoint(new FunctionPoint(Math.PI * 0.75, Math.sin(Math.PI * 0.75)));
            function.deletePoint(0);

            printTabulatedPoints(title + ": after modifications", function);
        } catch (InappropriateFunctionPointException exception) {
            System.out.println("Unexpected function point error: " + exception.getMessage());
        }
    }

    private static void demonstrateExceptions(TabulatedFunction function) {
        System.out.println("Demonstrating exceptions for " + function.getClass().getSimpleName());
        try {
            function.getPoint(-1);
        } catch (FunctionPointIndexOutOfBoundsException exception) {
            System.out.println("Caught expected index error: " + exception.getMessage());
        }

        try {
            function.setPointX(1, function.getPointX(0));
        } catch (InappropriateFunctionPointException exception) {
            System.out.println("Caught expected ordering error: " + exception.getMessage());
        }

        try {
            function.addPoint(new FunctionPoint(function.getPointX(1), 42.0));
        } catch (InappropriateFunctionPointException exception) {
            System.out.println("Caught expected duplicate point error: " + exception.getMessage());
        }

        try {
            while (function.getPointsCount() > 2) {
                function.deletePoint(0);
            }
            function.deletePoint(0);
        } catch (IllegalStateException exception) {
            System.out.println("Caught expected deletion error: " + exception.getMessage());
        }
        System.out.println();
    }

    private static void fillWithSineValues(TabulatedFunction function) {
        for (int i = 0; i < function.getPointsCount(); i++) {
            double x = function.getPointX(i);
            function.setPointY(i, Math.sin(x));
        }
    }

    private static void printTabulatedPoints(String title, TabulatedFunction function) {
        System.out.println(title);
        for (int i = 0; i < function.getPointsCount(); i++) {
            double x = function.getPointX(i);
            double y = function.getPointY(i);
            System.out.printf("Point %d: x=%.4f, y=%.6f%n", i, x, y);
        }
        System.out.println();
    }
}
