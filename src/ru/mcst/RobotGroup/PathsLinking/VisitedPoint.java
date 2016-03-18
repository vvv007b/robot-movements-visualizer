package ru.mcst.RobotGroup.PathsLinking;

import java.awt.geom.Point2D;

/**
 * Created by bocharov_n on 19.02.16.
 */
class VisitedPoint extends Point2D.Double {
    private double v, t;
    public VisitedPoint(){
        super();
        v = 0;
        t = 0;
    }

    VisitedPoint(double x, double y){
        super(x,y);
    }

    VisitedPoint(double x, double y, double t){
        super(x, y);
        this.t = t;
    }

    VisitedPoint(double x, double y, double v, double t){
        super(x, y);
        this.v = v;
        this.t = t;
    }

    VisitedPoint(VisitedPoint p){
        this.x = p.x;
        this.y = p.y;
        this.v = p.v;
        this.t = p.t;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }
}
