package ru.mcst.RobotGroup.PathsLinking;

import com.sun.javafx.geom.Vec2d;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bocharov_n on 14.10.15.
 */
class Camera {

    private int x, y, azimuth, r, angle;
    private List<VisitedPoint> visiblePoints;
    private Arc2D arc;
    private double accuracy = 1.0;
    private boolean isExist;
    private Tracker tracker;

    public Camera(){
//        super("");
        
        x = 1;
        y = 1;
        azimuth = 0;
        r = 3;
        angle = 90;
        visiblePoints = new ArrayList<VisitedPoint>();
        arc = null;
        redrawFOV();
        isExist = true;
        tracker = null;
    }

    public Camera(int x, int y, int azimuth, int r, int angle){
//        super("");

        this.x = x;
        this.y = y;
        this.azimuth = azimuth;
        this.r = r;
        this.angle = angle;
        visiblePoints = new ArrayList<VisitedPoint>();
        arc = null;
        redrawFOV();
        isExist = true;
        tracker = null;

    }

    public void redrawFOV(){
        arc = new Arc2D.Double(0.0, 0.5, r, r, 0.0, 60.0, Arc2D.CHORD);

        arc.setArcByCenter(r, r, r, azimuth - angle / 2, angle, Arc2D.OPEN);


    }

//    public void redrawVisibleImage(){
//        this.visibleImage = new BufferedImage(x + r + 1, y + r + 1, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = visibleImage.createGraphics();
//
//        g2d.setComposite(AlphaComposite.Clear);
//        g2d.fillRect(0, 0, visibleImage.getWidth(), visibleImage.getHeight());
//        g2d.setComposite(AlphaComposite.Src);
//        g2d.setColor(Color.RED);
//        for(Point2D point:visiblePoints){
//            g2d.fillOval((int)point.getX() - 3, (int)point.getY() - 3, 6, 6);
//        }
//        g2d.dispose();
//        visibleImageLabel.removeAll();
//        visibleImageLabel.setIcon(new ImageIcon(visibleImage));
//        visibleImageLabel.repaint();
//    }
//
//    public void clearVisibleImage(){
//        this.visibleImage = new BufferedImage(x + r + 1, y + r + 1, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = visibleImage.createGraphics();
//
//        g2d.setComposite(AlphaComposite.Clear);
//        g2d.fillRect(0, 0, visibleImage.getWidth(), visibleImage.getHeight());
//        g2d.dispose();
//        visibleImageLabel.removeAll();
//        visibleImageLabel.setIcon(new ImageIcon(visibleImage));
//        visibleImageLabel.repaint();
//    }

    public boolean isOnCorner(Point2D p){
        boolean isOnArc = Math.abs(Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2)) - this.r) < this.accuracy;
        boolean isOnLeftLine = Math.abs(p.getY() - this.y -
                Math.tan(Math.toRadians(this.azimuth + this.angle / 2)) * (p.getX() - this.x)) < this.accuracy; // TODO: check this
        boolean isOnRightLine = Math.abs(p.getY() - this.y -
                Math.tan(Math.toRadians(this.azimuth - this.angle / 2)) * (p.getX() - this.x)) < this.accuracy;
//        System.out.println(p.getX()+" "+p.getY());
//        System.out.println(Math.abs(p.getY() - this.y - Math.tan(this.azimuth + this.angle / 2) * (p.getX() - this.x)));
//        System.out.println( Math.abs(Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2)) - this.r));
//        System.out.println(isOnArc + " " + isOnLeftLine + " " + isOnRightLine);
        return isOnArc || isOnLeftLine || isOnRightLine;
    }

    public boolean isVisible(Point2D point){
        double x = point.getX(), y = point.getY();
        boolean isInCircle = Math.pow(this.getX() - x, 2) + Math.pow(this.getY() - y, 2) <= Math.pow(this.getR(), 2);
//        if(Math.pow(this.getX() - x, 2) + Math.pow(this.getY() - y, 2) <= Math.pow(this.getR(), 2)) isInCircle = true;
        double startX = this.getX() - this.getR() + this.getArc().getStartPoint().getX(),
                startY = this.getY() - this.getR() + this.getArc().getStartPoint().getY(),
                endX = this.getX() - this.getR() + this.getArc().getEndPoint().getX(),
                endY = this.getY() - this.getR() + this.getArc().getEndPoint().getY();
        Point2D startPoint = new Point2D.Double(startX, startY),
                endPoint = new Point2D.Double(endX, endY),
                centerPoint = new Point2D.Float(this.getX(), this.getY());

        Vec2d sectorStart = new Vec2d(startPoint.getX() - centerPoint.getX(), startPoint.getY() - centerPoint.getY()),
                sectorEnd = new Vec2d(endPoint.getX() - centerPoint.getX(), endPoint.getY() - centerPoint.getY()),
                relPoint = new Vec2d(x - centerPoint.getX(), y - centerPoint.getY());
        return isInCircle && !areClockwise(sectorStart, relPoint) && areClockwise(sectorEnd, relPoint);
    }

    private static boolean areClockwise(Vec2d v1, Vec2d v2){
        return -v1.x * v2.y + v1.y * v2.x <= 0;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public List<VisitedPoint> getVisiblePoints() { return visiblePoints;}

    public void setVisiblePoints(List<VisitedPoint> visiblePoints) {this.visiblePoints = visiblePoints;}

    public void addVisiblePoint(VisitedPoint point){this.visiblePoints.add(point);}

    public Arc2D getArc() {return arc;}

    public void setArc(Arc2D arc) {this.arc = arc;}

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
}
