package ru.mcst.RobotGroup.PathsLinking;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by bocharov_n on 19.02.16.
 */
class InOutVector {
    private RobotTrajectory robotTrajectory;
    Point2D startPoint, endPoint;
    Color color;

    public InOutVector(){
        robotTrajectory = null;
        startPoint = null;
        endPoint = null;
        color = null;
    }

    public  InOutVector(RobotTrajectory robotTrajectory){
        super();
        this.robotTrajectory = robotTrajectory;
    }

    public InOutVector(Point2D startPoint, Point2D endPoint, RobotTrajectory robotTrajectory, Color color){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.robotTrajectory = robotTrajectory;
        this.color = color;
    }

    public double getAzimuth(){
        return ( endPoint.getX() - startPoint.getX() == 0 ) ? 90 :
        Math.toDegrees(Math.atan((endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX())));
    }

    public double getShift(){
        return - startPoint.getX() * endPoint.getY() + endPoint.getX() * startPoint.getY();
    }

    public double getNormal(){
        double c = startPoint.getX() * endPoint.getY() - endPoint.getX() * startPoint.getY(),
                a = endPoint.getX() - startPoint.getX(),
                b = startPoint.getY() - endPoint.getY();
        return Math.abs(c / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));
    }

    public boolean isBehind(InOutVector vector){
        return (vector.getEndPoint().getX() - startPoint.getX()) / (endPoint.getX() - startPoint.getX()) > 0;
    }

    public RobotTrajectory getRobotTrajectory() {
        return robotTrajectory;
    }

    public Point2D getStartPoint() {
        return startPoint;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }
}
