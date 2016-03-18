package ru.mcst.RobotGroup.PathsLinking;

/**
 * Created by bocharov_n on 13.11.15.
 */
class KeyPoint extends RoundButton {
    private int x, y;
    private double t, v;
                                                //V  - how many pixels in one time quant
    private Trajectory parentTrajectory;

    public KeyPoint(){
        super("");
        this.x = 0;
        this.y = 0;
        this.v = 0;
        this.t = 0;
    }

    public KeyPoint(int x, int y, double t, double v){
        super("");
        this.x = x;
        this.y = y;
        this.t = t;
        this.v = v;
    }

    public KeyPoint(int x, int y, double v){
        super("");
        this.x = x;
        this.y = y;
        this.t = 0;
        this.v = v;
    }

    public int getx() {
        return x;
    }

    public void setx(int x) {
        this.x = x;
    }

    public int gety() {
        return y;
    }

    public void sety(int y) {
        this.y = y;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    public Trajectory getParentTrajectory() {
        return parentTrajectory;
    }

    public void setParentTrajectory(Trajectory parentTrajectory) {
        this.parentTrajectory = parentTrajectory;
    }
}
