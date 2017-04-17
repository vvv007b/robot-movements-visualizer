package com.robot.paths.linking;


import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

class Camera {

    private Point center;
    private int azimuth;
    private int radius;
    private int angle;
    private final Arc2D arc;
    private boolean isExist;
    private Tracker tracker;
    private int index;

    public Camera(Point center) {
        this.center = center;
        this.azimuth = 90;
        this.radius = 120;
        this.angle = 90;
        this.arc = new Arc2D.Double(0.0, 0.5, 120, 120, 0.0, 60.0, Arc2D.CHORD);//random parameters for creating new arc
        redrawFov();
        this.isExist = true;
        this.tracker = null;
        this.index = TrackingSystem.getCameraList().size();
    }

    void redrawFov() {
        arc.setArcByCenter(radius, radius, radius, azimuth - angle / 2, angle, Arc2D.OPEN);
    }

    public boolean isVisible(Point2D point) {
        boolean isInCircle = Math.pow(this.getX() - point.getX(), 2) + Math.pow(this.getY() - point.getY(), 2) <=
                Math.pow(this.getRadius(), 2);
        double startX = this.getX() - this.getRadius() + this.getArc().getStartPoint().getX();
        double startY = this.getY() - this.getRadius() + this.getArc().getStartPoint().getY();
        double endX = this.getX() - this.getRadius() + this.getArc().getEndPoint().getX();
        double endY = this.getY() - this.getRadius() + this.getArc().getEndPoint().getY();
        Point2D startPoint = new Point2D.Double(startX, startY);
        Point2D endPoint = new Point2D.Double(endX, endY);
        Point2D centerPoint = new Point2D.Double(this.getX(), this.getY());
        Point2D sectorStart = new Point2D.Double(startPoint.getX() - centerPoint.getX(),
                startPoint.getY() - centerPoint.getY());
        Point2D sectorEnd = new Point2D.Double(endPoint.getX() - centerPoint.getX(), endPoint.getY() - centerPoint.getY());
        Point2D relPoint = new Point2D.Double(point.getX() - centerPoint.getX(), point.getY() - centerPoint.getY());
        return isInCircle && !areClockwise(sectorStart, relPoint) && areClockwise(sectorEnd, relPoint);
    }

    private boolean areClockwise(Point2D v1, Point2D v2) {
        return -v1.getX() * v2.getY() + v1.getY() * v2.getX() <= 0;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist() {
        isExist = false;
    }

    private Arc2D getArc() {
        return arc;
    }

    public int getX() {
        return center.x;
    }

    public int getY() {
        return center.y;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
