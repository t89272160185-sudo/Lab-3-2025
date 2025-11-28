package functions;

/**
 * Base contract for tabulated functions that are defined by a finite ordered set
 * of points.
 */
public interface TabulatedFunction {
    double EPSILON = 1e-9;

    double getLeftDomainBorder();

    double getRightDomainBorder();

    double getFunctionValue(double x);

    int getPointsCount();

    FunctionPoint getPoint(int index);

    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;

    double getPointX(int index);

    void setPointX(int index, double x) throws InappropriateFunctionPointException;

    double getPointY(int index);

    void setPointY(int index, double y);

    void deletePoint(int index);

    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
}
