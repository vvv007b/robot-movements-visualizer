package com.mcst.paths.linking;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;


class RobotTrajectory {
    private final ArrayList<Point2D> points;
    private final ArrayList<Double> speeds;
    private final ArrayList<Long> times;
    private int direction;             //0 - nothing; 1 - in && out; 2 - only in; 3 - only out
    private final ArrayList<RobotTrajectory> connectedTrajectories;
    private final HashSet<RobotTrajectory> prev;
    private final HashSet<RobotTrajectory> next;
    private InOutVector inVector;
    private InOutVector outVector;

    public RobotTrajectory() {
        points = new ArrayList<>();
        direction = 0;
        inVector = null;
        outVector = null;
        connectedTrajectories = new ArrayList<>();
        speeds = new ArrayList<>();
        times = new ArrayList<>();
        prev = new HashSet<>();
        next = new HashSet<>();
    }

    public RobotTrajectory(RobotTrajectory robotTrajectory) {
        points = new ArrayList<>();
        this.points.addAll(robotTrajectory.getPoints().stream().collect(Collectors.toList()));
        this.direction = robotTrajectory.getDirection();
        inVector = null;
        outVector = null;
        connectedTrajectories = new ArrayList<>(robotTrajectory.getConnectedTrajectories());
        speeds = new ArrayList<>(robotTrajectory.getSpeeds());
        times = new ArrayList<>(robotTrajectory.getTimes());
        next = new HashSet<>(robotTrajectory.getNext());
        prev = new HashSet<>(robotTrajectory.getPrev());
    }


    public ArrayList<Point2D> getPoints() {
        return points;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public InOutVector getInVector() {
        return inVector;
    }

    public void setInVector(InOutVector inVector) {
        this.inVector = inVector;
    }

    public InOutVector getOutVector() {
        return outVector;
    }

    public void setOutVector(InOutVector outVector) {
        this.outVector = outVector;
    }

    public ArrayList<RobotTrajectory> getConnectedTrajectories() {
        return connectedTrajectories;
    }

    public ArrayList<Double> getSpeeds() {
        return speeds;
    }

    public ArrayList<Long> getTimes() {
        return times;
    }

    public HashSet<RobotTrajectory> getPrev() {
        return prev;
    }

    public HashSet<RobotTrajectory> getNext() {
        return next;
    }
}
