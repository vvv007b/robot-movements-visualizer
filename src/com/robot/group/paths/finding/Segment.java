package com.robot.group.paths.finding;

class Segment {
    private boolean isStraightLine = true;
    private boolean isClockwise = false;
    private double originX = -1000;
    private double originY = -1000;
    private double length = 0;
    private double startAngle = 0;
    private double radiansTotal = 0;
    private double radius = 0;
    private int weight = -1;
    // for some optimization
    public static final double twoPI = 2.0 * Math.PI;
    public static final double halfPI = Math.PI / 2;

    public Segment() {
    }

//    public Segment(double originX, double originY, double length, double startAngle, int weight) {
//        this.originX = originX;
//        this.originY = originY;
//        this.length = length;
//        this.startAngle = startAngle;
//        this.weight = weight;
//    }
//
//    public Segment(double originX, double originY, boolean isClockwise, double startAngle, double radiansTotal, double radius, int weight) {
//        this.isStraightLine = false;
//        this.isClockwise = isClockwise;
//        this.originX = originX;
//        this.originY = originY;
//        this.startAngle = startAngle;
//        this.radiansTotal = radiansTotal;
//        this.length = radius * radiansTotal;
//        this.radius = radius;
//        this.weight = weight;
//    }

    public boolean getIsStraightLine() {
        return isStraightLine;
    }

    public boolean getIsClockwise() {
        return isClockwise;
    }

    public double getOriginX() {
        return originX;
    }

    public double getOriginY() {
        return originY;
    }

    public double getLength() {
        return length;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getRadiansTotal() {
        return radiansTotal;
    }

    public double getRadius() {
        return radius;
    }

    public int getWeight() {
        return weight;
    }

    public void setIsStraightLine(boolean isStraightLine) {
        this.isStraightLine = isStraightLine;
    }

    public void setIsClockwise(boolean isClockwise) {
        this.isClockwise = isClockwise;
    }

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
    }

    public void setRadiansTotal(double radiansTotal) {
        this.radiansTotal = radiansTotal;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    // returns [0] - x, [1] - y, [2] - azimuth
    public final void getPositionOnDistance(double[] result, double distance) {
        if (!isStraightLine) {
            double theta;
            if (!isClockwise) {
                // поворачиваем налево
                theta = Segment.CapRadian(startAngle + distance / radius);
                result[2] = Segment.CapRadian(theta + Segment.halfPI);
            } else {
                // поворачиваем направо
                theta = Segment.CapRadian(startAngle - distance / radius);
                result[2] = Segment.CapRadian(theta - Segment.halfPI);
            }
            result[0] = originX + radius * Math.cos(theta);
            // changed sign because of screen coordinates
            result[1] = originY - radius * Math.sin(theta);
        } else {
            result[2] = startAngle;
            result[0] = originX + distance * Math.cos(startAngle);
            // changed sign because of screen coordinates
            result[1] = originY - distance * Math.sin(startAngle);
        }
    }

    // Constrain a radian value to 0<=value<=2*PI
    public static double CapRadian(double r) {
        r %= twoPI;
        if (r < 0) r += twoPI;
        if (r < 0.00000001 || r > twoPI - 0.00000001)
            return 0.0;
        return r;
    }
}
