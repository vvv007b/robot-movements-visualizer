package com.mcst.paths.finding;

import java.awt.Point;
import java.awt.geom.Line2D;

class Link {
//    private Node parent = null;
    private Node child = null;
    private Segment[] segments = null;
    private double length = 0;

    public Link(Node child, Segment[] segments) {
//        this.parent = parent;
        this.child = child;
        this.segments = segments;
        for (Segment s : segments) {
            length += s.getLength();
        }
    }

    public Node getChild() {
        return child;
    }

//    public Node getParent() {
//        return parent;
//    }

    public Segment[] getSegments() {
        return segments;
    }

    public double getLength() {
        return length;
    }

    public int getWeight() {
        return (segments[0].getWeight() + segments[1].getWeight() + segments[2].getWeight()) / 3;
    }

    public static boolean isSegmentsBlocked(Segment[] segments, int robotSize, byte[][] passabilityArray) {
        if (passabilityArray == null) {
            return false;
        }
        double distance = 0;
        double globalDistance = 0;
        double length = segments[0].getLength() + segments[1].getLength() + segments[2].getLength();
        int iter = 0;
        double[] position = new double[3];
        int step = robotSize / 6;
        if (step == 0) {
            step = 1;
        }

        Segment segment = segments[iter];
        int weight = 0;
        int counter = 0;
        while (globalDistance < length) {
            distance += step;
            globalDistance += step;

            if (distance < segment.getLength()) {
                segment.getPositionOnDistance(position, distance);
                int tempW;
                if ((tempW = getPointWeight(new Point((int) position[0], (int) position[1]),
                        position[2], robotSize, passabilityArray)) == 255) {
                    return true;
                } else {
                    weight += tempW;
                    counter++;
                }
            } else {
                if (counter != 0) {
                    segment.setWeight(weight / counter);
                } else {
                    int tempWeight;
                    segment.getPositionOnDistance(position, 0);
                    if ((tempWeight = getPointWeight(new Point((int) position[0], (int) position[1]), position[2],
                            robotSize, passabilityArray)) == 255) {
                        return true;
                    } else {
                        segment.setWeight(tempWeight);
                    }
                }
                weight = 0;
                counter = 0;
                distance -= segment.getLength();
                ++iter;
                if (iter >= segments.length) {
                    break;
                }
                if (segment.getLength() == 0) {
                    distance -= step;
                    globalDistance -= step;
                }
                segment = segments[iter];
            }
        }
        return false;
    }

    public static int getPointWeight(Point center, double azimuth,
                                     int robotSize, byte[][] passabilityArray) {
        if (passabilityArray == null) {
            return 255;
        }
        robotSize /= 2;
        int dx = robotSize - 2;
        int dy = dx - robotSize / 3;
        int[] arrayX = {dx, -dx, -dx, dx, 0, 0};
        int[] arrayY = {dy, dy, -dy, -dy, -dy, dy};
        int weight = 0;
        int length = passabilityArray[0].length;
        double cos = Math.cos(azimuth);
        double sin = Math.sin(azimuth);

        // rotating and checking
        for (int i = 0; i < 6; ++i) {
            dx = arrayX[i];
            dy = arrayY[i];
            int xi = center.x + (int) (cos * dx + sin * dy);
            int yi = center.y + (int) (cos * dy - sin * dx);

            if ((xi | yi) < 0) {
                return 255;
            }
            if (xi < passabilityArray.length && yi < length) {
                int add = passabilityArray[xi][yi];
                if (add == -128) {
                    return 255; // point is blocked
                }
                weight += 127 - add;
            } else if (xi <= passabilityArray.length + robotSize && yi <= length + robotSize) {
                weight += 254;
            } else {
                return 255;
            }
        }
        return weight / 4;
    }

    public static boolean isSegmentsBlockedByRobot(Segment[] segments, Point center2, double azimuth2,
                                                   int robotSize, int howFar, double passed) {
        double distance = 0;
        double globalDistance = 0;
        double length = segments[0].getLength() + segments[1].getLength() + segments[2].getLength();
        int iter = 0;
        double[] position = new double[3];
        //int step=robotSize/6;
        int step = robotSize / 8;
        if (step == 0) {
            step = 1;
        }

        if (passed > 0) {
            while (globalDistance < passed) {
                globalDistance += step;
            }
            distance = globalDistance;
        }

        Segment segment = segments[iter];
        while ((!(howFar > 0) || globalDistance < howFar) && globalDistance < length) {
            distance += step;
            globalDistance += step;

            if (distance < segment.getLength()) {
                segment.getPositionOnDistance(position, distance);
                // USE POSITION!!!
                if (isPointBlockedByRobot(new Point((int) position[0], (int) position[1]), position[2],
                        center2, azimuth2, robotSize)) {
                    return true;
                }
            } else {
                distance -= segment.getLength();
                ++iter;
                if (iter >= segments.length) {
                    break;
                }
                if (segment.getLength() == 0) {
                    distance -= step;
                    globalDistance -= step;
                }
                segment = segments[iter];
            }
        }
        return false;
    }

    private static boolean isPointBlockedByRobot(Point center1, double azimuth1, Point center2, double azimuth2,
                                                 int robotSize) {
        robotSize /= 2;
        int dx1 = robotSize - 2;
        int dy1 = dx1 - robotSize / 3;
        int dx2 = robotSize - 2;
        int dy2 = dx2 - robotSize / 3;
//        xy[1] xy[0]
//           center
//        xy[2] xy[3]
        int[] x1 = {dx1, -dx1, -dx1, dx1};
        int[] y1 = {dy1, dy1, -dy1, -dy1};
        int[] x2 = {dx2, -dx2, -dx2, dx2};
        int[] y2 = {dy2, dy2, -dy2, -dy2};

        double cos1 = Math.cos(azimuth1);
        double sin1 = Math.sin(azimuth1);
        double cos2 = Math.cos(azimuth2);
        double sin2 = Math.sin(azimuth2);

        for (int i = 0; i < 4; ++i) {
            dx1 = x1[i];
            dy1 = y1[i];
            dx2 = x2[i];
            dy2 = y2[i];
            x1[i] = center1.x + (int) (cos1 * dx1 + sin1 * dy1);
            y1[i] = center1.y + (int) (cos1 * dy1 - sin1 * dx1);
            x2[i] = center2.x + (int) (cos2 * dx2 + sin2 * dy2);
            y2[i] = center2.y + (int) (cos2 * dy2 - sin2 * dx2);
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (Line2D.linesIntersect(x1[i], y1[i], x1[i + 1], y1[i + 1], x2[j], y2[j], x2[j + 1], y2[j + 1])) {
                    return true;
                }
            }
            if (Line2D.linesIntersect(x1[i], y1[i], x1[i + 1], y1[i + 1], x2[3], y2[3], x2[0], y2[0])) {
                return true;
            }
        }
        for (int j = 0; j < 3; ++j) {
            if (Line2D.linesIntersect(x1[3], y1[3], x1[0], y1[0], x2[j], y2[j], x2[j + 1], y2[j + 1])) {
                return true;
            }
        }
        return Line2D.linesIntersect(x1[3], y1[3], x1[0], y1[0], x2[3], y2[3], x2[0], y2[0]);
    }

    public double getRadiansTotal() {
        return segments[0].getRadiansTotal() + segments[2].getRadiansTotal();
    }
}
