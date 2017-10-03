package com.mcst.paths.finding2.reedsShepp;

/**
 * Created by mannimarco on 13/04/2017.
 * One Reeds-Sheep segment
 */
public class Segment {

    //Are we turning?
    private boolean isTurning;
    //Are we turning left?
    private boolean isTurningLeft;
    //Are we reversing?
    private boolean isReversing;
    //The length of this segment
    private double pathLength;

    public boolean isTurning() {
        return isTurning;
    }

    public void setTurning(boolean turning) {
        isTurning = turning;
    }

    public boolean isTurningLeft() {
        return isTurningLeft;
    }

    public void setTurningLeft(boolean turningLeft) {
        isTurningLeft = turningLeft;
    }

    public boolean isReversing() {
        return isReversing;
    }

    public void setReversing(boolean reversing) {
        isReversing = reversing;
    }

    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }
}
