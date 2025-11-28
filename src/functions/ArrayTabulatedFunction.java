package functions;

public class ArrayTabulatedFunction implements TabulatedFunction {
    private static final int DEFAULT_CAPACITY = 8; // Initial storage size for points.

    private FunctionPoint[] points;
    private int size;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Tabulated function must contain at least two points.");
        }
        initWithZeroValues(leftX, rightX, pointsCount);
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException("Values array must contain at least two items.");
        }
        initWithValues(leftX, rightX, values);
    }

    private void initWithZeroValues(double leftX, double rightX, int pointsCount) {
        validateBorders(leftX, rightX);
        points = new FunctionPoint[Math.max(pointsCount, DEFAULT_CAPACITY)];
        size = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = i == pointsCount - 1 ? rightX : leftX + step * i;
            points[i] = new FunctionPoint(x, 0.0);
        }
    }

    private void initWithValues(double leftX, double rightX, double[] values) {
        validateBorders(leftX, rightX);
        points = new FunctionPoint[Math.max(values.length, DEFAULT_CAPACITY)];
        size = values.length;
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = i == values.length - 1 ? rightX : leftX + step * i;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    private void validateBorders(double leftX, double rightX) {
        if (!(rightX > leftX)) {
            throw new IllegalArgumentException("Right border must be greater than left border.");
        }
    }

    public double getLeftDomainBorder() {
        return size == 0 ? Double.NaN : points[0].getX();
    }

    public double getRightDomainBorder() {
        return size == 0 ? Double.NaN : points[size - 1].getX();
    }

    public double getFunctionValue(double x) {
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();
        if (x < leftBorder - EPSILON || x > rightBorder + EPSILON) {
            return Double.NaN;
        }
        for (int i = 0; i < size; i++) {
            if (Math.abs(x - points[i].getX()) <= EPSILON) {
                return points[i].getY();
            }
        }
        for (int i = 0; i < size - 1; i++) {
            FunctionPoint leftPoint = points[i];
            FunctionPoint rightPoint = points[i + 1];
            if (x < rightPoint.getX()) {
                // Linear interpolation inside the segment.
                double ratio = (x - leftPoint.getX()) / (rightPoint.getX() - leftPoint.getX());
                return leftPoint.getY() + ratio * (rightPoint.getY() - leftPoint.getY());
            }
        }
        return points[size - 1].getY();
    }

    public int getPointsCount() {
        return size;
    }

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be null.");
        }
        checkIndex(index);
        ensureCorrectOrder(point.getX(), index);
        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        checkIndex(index);
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        ensureCorrectOrder(x, index);
        points[index].setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return points[index].getY();
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y);
    }

    public void deletePoint(int index) {
        if (size <= 2) {
            throw new IllegalStateException("Tabulated function must contain at least two points.");
        }
        checkIndex(index);
        int elementsToMove = size - index - 1;
        if (elementsToMove > 0) {
            System.arraycopy(points, index + 1, points, index, elementsToMove);
        }
        points[size - 1] = null;
        size--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be null.");
        }
        ensureCapacity(size + 1);
        int insertIndex = size;
        for (int i = 0; i < size; i++) {
            double pointX = points[i].getX();
            if (Math.abs(point.getX() - pointX) <= EPSILON) {
                throw new InappropriateFunctionPointException("Point with the same x already exists.");
            }
            if (point.getX() < pointX) {
                insertIndex = i;
                break;
            }
        }
        if (insertIndex < size) {
            // Shift tail to free space for the inserted point.
            System.arraycopy(points, insertIndex, points, insertIndex + 1, size - insertIndex);
        }
        points[insertIndex] = new FunctionPoint(point);
        size++;
    }

    private void ensureCapacity(int minCapacity) {
        if (points == null) {
            points = new FunctionPoint[Math.max(DEFAULT_CAPACITY, minCapacity)];
            return;
        }
        if (points.length >= minCapacity) {
            return;
        }
        int newCapacity = Math.max(points.length + (points.length >> 1), minCapacity);
        FunctionPoint[] newPoints = new FunctionPoint[newCapacity];
        System.arraycopy(points, 0, newPoints, 0, size);
        points = newPoints;
    }

    private void ensureCorrectOrder(double x, int index) throws InappropriateFunctionPointException {
        if (index > 0) {
            double previousX = points[index - 1].getX();
            if (x <= previousX) {
                throw new InappropriateFunctionPointException("Point must keep ascending order.");
            }
        }
        if (index < size - 1) {
            double nextX = points[index + 1].getX();
            if (x >= nextX) {
                throw new InappropriateFunctionPointException("Point must keep ascending order.");
            }
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(index, size);
        }
    }
}
