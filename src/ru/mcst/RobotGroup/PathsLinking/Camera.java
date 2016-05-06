package ru.mcst.RobotGroup.PathsLinking;

import com.sun.javafx.geom.Vec2d;

import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

/**
 * Created by bocharov_n on 14.10.15.
 */
class Camera {

    private int x, y, azimuth, r, angle;
    private Arc2D arc;
    private boolean isExist;
    private Tracker tracker;
    private int index;
//    private boolean isInCircleGlobal, areClockWiseGlobal1, areClockWiseGlobal2;
//    private Point2D curPointGlobal;
//    private ArrayList<Boolean> isInCircleGlobalList = new ArrayList<Boolean>(),
//            areClockWiseGlobal1List = new ArrayList<Boolean>(),
//            areClockWiseGlobal2List = new ArrayList<Boolean>();
//    private ArrayList<Point2D> curPointGlobalList = new ArrayList<Point2D>();
//    private ArrayList<Long> timesGlobalList = new ArrayList<Long>();

    public Camera(int x, int y, int azimuth, int r, int angle){
        this.x = x;
        this.y = y;
        this.azimuth = azimuth;
        this.r = r;
        this.angle = angle;
        this.arc = new Arc2D.Double(0.0, 0.5, r, r, 0.0, 60.0, Arc2D.CHORD);             //random parameters for creating new arc
        redrawFOV();
        this.isExist = true;
        this.tracker = null;
        this.index = TrackingSystem.getCameraList().size();
    }

    public void redrawFOV(){
        arc.setArcByCenter(r, r, r, azimuth - angle / 2, angle, Arc2D.OPEN);
    }

    public boolean isOnCorner(Point2D p){
        double accuracy = 1.0;
        boolean isOnArc = Math.abs(Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2)) - this.r) < accuracy;
        boolean isOnLeftLine = Math.abs(p.getY() - this.y -
                Math.tan(Math.toRadians(this.azimuth + this.angle / 2)) * (p.getX() - this.x)) < accuracy;
        boolean isOnRightLine = Math.abs(p.getY() - this.y -
                Math.tan(Math.toRadians(this.azimuth - this.angle / 2)) * (p.getX() - this.x)) < accuracy;
        return isOnArc || isOnLeftLine || isOnRightLine;
    }

    public boolean isVisible(Point2D point){
        double x = point.getX(), y = point.getY();
        boolean isInCircle = Math.pow(this.getX() - x, 2) + Math.pow(this.getY() - y, 2) <= Math.pow(this.getR(), 2);
        double startX = this.getX() - this.getR() + this.getArc().getStartPoint().getX(),
                startY = this.getY() - this.getR() + this.getArc().getStartPoint().getY(),
                endX = this.getX() - this.getR() + this.getArc().getEndPoint().getX(),
                endY = this.getY() - this.getR() + this.getArc().getEndPoint().getY();
        Point2D startPoint = new Point2D.Double(startX, startY),
                endPoint = new Point2D.Double(endX, endY),
                centerPoint = new Point2D.Double(this.getX(), this.getY());

        Vec2d sectorStart = new Vec2d(startPoint.getX() - centerPoint.getX(), startPoint.getY() - centerPoint.getY()),
                sectorEnd = new Vec2d(endPoint.getX() - centerPoint.getX(), endPoint.getY() - centerPoint.getY()),
                relPoint = new Vec2d(x - centerPoint.getX(), y - centerPoint.getY());
//        curPointGlobal = point;
//        isInCircleGlobal = isInCircle;
//        areClockWiseGlobal1 = !areClockwise(sectorStart, relPoint);
//        areClockWiseGlobal2 = areClockwise(sectorEnd, relPoint);
//        curPointGlobalList.add(point);
//        isInCircleGlobalList.add(isInCircle);
//        areClockWiseGlobal1List.add(!areClockwise(sectorStart, relPoint));
//        areClockWiseGlobal2List.add(areClockwise(sectorEnd, relPoint));
//        timesGlobalList.add(System.currentTimeMillis());
//        while(curPointGlobalList.size() >= 50){
//            curPointGlobalList.remove(0);
//            isInCircleGlobalList.remove(0);
//            areClockWiseGlobal1List.remove(0);
//            areClockWiseGlobal2List.remove(0);
//            timesGlobalList.remove(0);
//        }
        return  isInCircle && !areClockwise(sectorStart, relPoint) && areClockwise(sectorEnd, relPoint);
//        return isInCircle && !areClockwise(sectorStart, relPoint) && areClockwise(sectorEnd, relPoint);
    }

    private boolean areClockwise(Vec2d v1, Vec2d v2){
        return -v1.x * v2.y + v1.y * v2.x <= 0;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public Arc2D getArc() {return arc;}

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
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
