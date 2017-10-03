package com.mcst.paths.linking;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

class InOutVector {
    public static final int IN = 0;
    public static final int OUT = 1;

    private final RobotTrajectory robotTrajectory;
    private Point2D startPoint;
    private Point2D endPoint;
    private double speed;
    private double acceleration;
    private final long time;
    private final int orientation;
    private final HashSet<InOutVector> prev;
    private final HashSet<InOutVector> next;
    final long startTime;
    final long endTime;


    public InOutVector(RobotTrajectory robotTrajectory, int orientation) {
        int startIndex = 0;
        int endIndex = 1;
        double speed;
        switch (orientation) {
            case IN:
                startIndex = 0;
                endIndex = 1;
                this.startPoint = robotTrajectory.getPoints().get(startIndex);
                this.endPoint = robotTrajectory.getPoints().get(endIndex);
                this.speed = 1000 * Math.sqrt(Math.pow(endPoint.getX() - startPoint.getX(), 2) +
                        Math.pow(endPoint.getY() - startPoint.getY(), 2)) /
                        (robotTrajectory.getTimes().get(endIndex) - robotTrajectory.getTimes().get(startIndex));
                speed = 1000 * Math.sqrt(Math.pow(robotTrajectory.getPoints().get(endIndex + 1).getX() -
                        robotTrajectory.getPoints().get(endIndex).getX(), 2) +
                        Math.pow(robotTrajectory.getPoints().get(endIndex + 1).getY() -
                                robotTrajectory.getPoints().get(endIndex).getY(), 2)) /
                        (robotTrajectory.getTimes().get(endIndex + 1) - robotTrajectory.getTimes().get(endIndex));
                this.acceleration = 1000 * (speed - this.speed) / (robotTrajectory.getTimes().get(endIndex + 1) -
                        robotTrajectory.getTimes().get(endIndex));
                break;
            case OUT:
                startIndex = robotTrajectory.getPoints().size() - 2;
                endIndex = robotTrajectory.getPoints().size() - 1;
                this.startPoint = robotTrajectory.getPoints().get(startIndex);
                this.endPoint = robotTrajectory.getPoints().get(endIndex);
                this.speed = 1000 * Math.sqrt(Math.pow(endPoint.getX() - startPoint.getX(), 2) +
                        Math.pow(endPoint.getY() - startPoint.getY(), 2)) /
                        (robotTrajectory.getTimes().get(endIndex) - robotTrajectory.getTimes().get(startIndex));
                speed = 1000 * Math.sqrt(Math.pow(robotTrajectory.getPoints().get(startIndex).getX() -
                        robotTrajectory.getPoints().get(startIndex - 1).getX(), 2) +
                        Math.pow(robotTrajectory.getPoints().get(startIndex).getY() -
                                robotTrajectory.getPoints().get(startIndex - 1).getY(), 2)) /
                        (robotTrajectory.getTimes().get(startIndex) - robotTrajectory.getTimes().get(startIndex - 1));
                this.acceleration = 1000 * (this.speed - speed) / (robotTrajectory.getTimes().get(startIndex) -
                        robotTrajectory.getTimes().get(startIndex - 1));
                break;
            default:
                System.out.println("error");
                break;
        }
        this.time = robotTrajectory.getTimes().get(startIndex);
        this.startTime = robotTrajectory.getTimes().get(startIndex);
        this.endTime = robotTrajectory.getTimes().get(endIndex);
        this.robotTrajectory = robotTrajectory;
        this.orientation = orientation;
        prev = new HashSet<>();
        next = new HashSet<>();
    }

    public void drawVector(Graphics2D g2d, boolean isFilled, boolean isBorder) {
        g2d.setComposite(AlphaComposite.Src);
        g2d.setColor(Color.GRAY);
        if (isFilled) {
            g2d.fillRoundRect((int) (orientation == 0 ? startPoint.getX() : endPoint.getX()) - 5,
                    (int) (orientation == 0 ? startPoint.getY() : endPoint.getY()) - 5, 10, 10, 2, 2);
        } else {
            g2d.drawRoundRect((int) (orientation == 0 ? startPoint.getX() : endPoint.getX()) - 5,
                    (int) (orientation == 0 ? startPoint.getY() : endPoint.getY()) - 5, 10, 10, 2, 2);
        }
        if (isBorder) {
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect((int) (orientation == 0 ? startPoint.getX() : endPoint.getX()) - 5,
                    (int) (orientation == 0 ? startPoint.getY() : endPoint.getY()) - 5, 10, 10, 2, 2);
        }
    }

    public double getAzimuth() {
        Point2D vector = new Point2D.Double(endPoint.getX() - startPoint.getX(), endPoint.getY() - startPoint.getY());
        return (vector.getX() == 0) ? 90 : (vector.getX() < 0 && vector.getY() > 0) ?
                (180 + Math.toDegrees(Math.atan(vector.getY() / vector.getX()))) :
                Math.toDegrees(Math.atan(vector.getY() / vector.getX()));
    }

    public double getNormal() {
        return Math.abs((startPoint.getX() * endPoint.getY() - endPoint.getX() * startPoint.getY()) /
                Math.sqrt(Math.pow(endPoint.getX() - startPoint.getX(), 2) +
                        Math.pow(startPoint.getY() - endPoint.getY(), 2)));
    }

    public boolean isPotentialFollowerTo(InOutVector secondVector) {
        final double possibleAngle = 90;
        double rotation = 15;          //rotation degrees
        Point2D firstVector = new Point2D.Double(endPoint.getX() - startPoint.getX(),
                endPoint.getY() - startPoint.getY());
//        double x = endPoint.getX() - startPoint.getX();
//        double y = endPoint.getY() - startPoint.getY();
        Point2D wayVector = new Point2D.Double(secondVector.getStartPoint().getX() - endPoint.getX(),
                secondVector.getStartPoint().getY() - endPoint.getY());
        Point2D sectorStart = new Point2D.Double(firstVector.getX() * Math.cos(rotation) -
                firstVector.getY() * Math.sin(rotation),
                firstVector.getX() * Math.sin(rotation) + firstVector.getY() * Math.cos(rotation));
        Point2D sectorEnd = new Point2D.Double(firstVector.getX() * Math.cos(-rotation) -
                firstVector.getY() * Math.sin(-rotation),
                firstVector.getX() * Math.sin(-rotation) + firstVector.getY() * Math.cos(-rotation));
        double wayVectorLength = Math.sqrt(Math.pow(wayVector.getX(), 2) + Math.pow(wayVector.getY(), 2));
        double sectorStartLength = Math.sqrt(Math.pow(sectorStart.getX(), 2) + Math.pow(sectorStart.getY(), 2));
        double sectorEndLength = Math.sqrt(Math.pow(sectorEnd.getX(), 2) + Math.pow(sectorEnd.getY(), 2));
        sectorStart = new Point2D.Double(sectorStart.getX() * wayVectorLength / sectorStartLength,
                sectorStart.getY() * wayVectorLength / sectorStartLength);
        sectorEnd = new Point2D.Double(sectorEnd.getX() * wayVectorLength / sectorEndLength,
                sectorEnd.getY() * wayVectorLength / sectorEndLength);
        double distance = Math.sqrt(Math.pow(endPoint.getX() - secondVector.getStartPoint().getX(), 2) +
                Math.pow(endPoint.getY() - secondVector.getStartPoint().getY(), 2));
        double possibleDistance = (this.speed + secondVector.speed) / 2 *
                ((double) (secondVector.time - this.time) / 1000);
        boolean isInReachableDistance = (distance < possibleDistance * 1.2 && distance > possibleDistance * 0.7) ||
                (distance < possibleDistance + 5 && distance > possibleDistance - 5);
        boolean isAzimuthCorrect = Math.abs(this.getAzimuth() - secondVector.getAzimuth()) < possibleAngle;
        boolean isInSector = !areClockwise(sectorStart, wayVector) && areClockwise(sectorEnd, wayVector);
        return isInSector && isInReachableDistance && isAzimuthCorrect;
    }

    private boolean areClockwise(Point2D v1, Point2D v2) {
        return -v1.getX() * v2.getY() + v1.getY() * v2.getX() <= 0;
    }

    public double getX() {
        return orientation == IN ? startPoint.getX() : endPoint.getX();
    }

    public double getY() {
        return orientation == IN ? startPoint.getY() : endPoint.getY();
    }

    public RobotTrajectory getRobotTrajectory() {
        return robotTrajectory;
    }

    private Point2D getStartPoint() {
        return startPoint;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public int getOrientation() {
        return orientation;
    }

    public HashSet<InOutVector> getNext() {
        return next;
    }

    public HashSet<InOutVector> getPrev() {
        return prev;
    }
}
