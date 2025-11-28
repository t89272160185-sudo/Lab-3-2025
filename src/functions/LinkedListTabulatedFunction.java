package functions;

/**
 * Linked list based implementation of tabulated functions.
 */
public class LinkedListTabulatedFunction implements TabulatedFunction {
    private final FunctionNode head;
    private int size;

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Tabulated function must contain at least two points.");
        }
        this.head = createHead();
        initWithZeroValues(leftX, rightX, pointsCount);
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException("Values array must contain at least two items.");
        }
        this.head = createHead();
        initWithValues(leftX, rightX, values);
    }

    private FunctionNode createHead() {
        FunctionNode node = new FunctionNode();
        node.next = node;
        node.prev = node;
        return node;
    }

    private void initWithZeroValues(double leftX, double rightX, int pointsCount) {
        validateBorders(leftX, rightX);
        size = 0;
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = i == pointsCount - 1 ? rightX : leftX + step * i;
            FunctionNode node = addNodeToTail();
            node.point = new FunctionPoint(x, 0.0);
        }
    }

    private void initWithValues(double leftX, double rightX, double[] values) {
        validateBorders(leftX, rightX);
        size = 0;
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = i == values.length - 1 ? rightX : leftX + step * i;
            FunctionNode node = addNodeToTail();
            node.point = new FunctionPoint(x, values[i]);
        }
    }

    private void validateBorders(double leftX, double rightX) {
        if (!(rightX > leftX)) {
            throw new IllegalArgumentException("Right border must be greater than left border.");
        }
    }

    @Override
    public double getLeftDomainBorder() {
        return size == 0 ? Double.NaN : head.next.point.getX();
    }

    @Override
    public double getRightDomainBorder() {
        return size == 0 ? Double.NaN : head.prev.point.getX();
    }

    @Override
    public double getFunctionValue(double x) {
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();
        if (x < leftBorder - EPSILON || x > rightBorder + EPSILON) {
            return Double.NaN;
        }
        FunctionNode current = head.next;
        while (current != head) {
            double currentX = current.point.getX();
            if (Math.abs(x - currentX) <= EPSILON) {
                return current.point.getY();
            }
            if (current.next != head) {
                double nextX = current.next.point.getX();
                if (x < nextX) {
                    FunctionPoint leftPoint = current.point;
                    FunctionPoint rightPoint = current.next.point;
                    double ratio = (x - leftPoint.getX()) / (rightPoint.getX() - leftPoint.getX());
                    return leftPoint.getY() + ratio * (rightPoint.getY() - leftPoint.getY());
                }
            }
            current = current.next;
        }
        return head.prev.point.getY();
    }

    @Override
    public int getPointsCount() {
        return size;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be null.");
        }
        FunctionNode node = getNodeByIndex(index);
        ensureCorrectOrder(point.getX(), node.prev, node.next);
        node.point = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        ensureCorrectOrder(x, node.prev, node.next);
        node.point.setX(x);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        if (size <= 2) {
            throw new IllegalStateException("Tabulated function must contain at least two points.");
        }
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be null.");
        }
        int insertIndex = size;
        FunctionNode current = head.next;
        int currentIndex = 0;
        while (current != head) {
            double pointX = current.point.getX();
            if (Math.abs(point.getX() - pointX) <= EPSILON) {
                throw new InappropriateFunctionPointException("Point with the same x already exists.");
            }
            if (point.getX() < pointX) {
                insertIndex = currentIndex;
                break;
            }
            current = current.next;
            currentIndex++;
        }
        FunctionNode node = addNodeByIndex(insertIndex);
        node.point = new FunctionPoint(point);
    }

    private FunctionNode getNodeByIndex(int index) {
        checkIndex(index);
        FunctionNode node;
        if (index < size / 2) {
            node = head.next;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            node = head.prev;
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
        }
        return node;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode node = new FunctionNode();
        insertBetween(node, head.prev, head);
        size++;
        return node;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index == size) {
            return addNodeToTail();
        }
        if (index < 0 || index > size) {
            throw new FunctionPointIndexOutOfBoundsException(index, size + 1);
        }
        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode node = new FunctionNode();
        insertBetween(node, nextNode.prev, nextNode);
        size++;
        return node;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        FunctionNode node = getNodeByIndex(index);
        unlink(node);
        size--;
        return node;
    }

    private void insertBetween(FunctionNode node, FunctionNode prevNode, FunctionNode nextNode) {
        node.prev = prevNode;
        node.next = nextNode;
        prevNode.next = node;
        nextNode.prev = node;
    }

    private void unlink(FunctionNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.next = null;
        node.prev = null;
    }

    private void ensureCorrectOrder(double x, FunctionNode previous, FunctionNode next)
            throws InappropriateFunctionPointException {
        if (previous != head && x <= previous.point.getX()) {
            throw new InappropriateFunctionPointException("Point must keep ascending order.");
        }
        if (next != head && x >= next.point.getX()) {
            throw new InappropriateFunctionPointException("Point must keep ascending order.");
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(index, size);
        }
    }

    private static class FunctionNode {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;
    }
}
