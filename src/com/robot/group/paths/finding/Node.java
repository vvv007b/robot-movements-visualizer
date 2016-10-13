package com.robot.group.paths.finding;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Node {
    // список соседей узла
    private final List<Link> links = new ArrayList<>();
    // координата узла
    private Point2D.Double position;
    // коэффициенты для алгоритма поиска пути
    //private double F, H, G;
    // родитель узла (для построения найденного пути)
    //private Node parent;
    // пометка, что узел создал робот
    private boolean isRobotMade;
    // RADIANS
    private double direction = 0;

    public Node(Point2D.Double point, double direction) {
        this.position = point;
        this.direction = direction;
//        F=0;
//        H=0;
//        G=0;
//        parent=null;
        isRobotMade = false;
    }

    private static double findTouchPoints(double originXa, double originYa, double originXb, double originYb,
                                          boolean isClockwiseA, boolean isClockwiseB, double radius, Segment line) {
        double dx = originXb - originXa;
        //     changed sign, because of screen coordinates
        // double dy=originYb-originYa;
        double dy = originYa - originYb;
        double angleOrigin;
        double angleFinal;

        angleOrigin = Math.atan2(dy, dx);

        line.setLength(Math.sqrt(dx * dx + dy * dy));

        if (isClockwiseA == isClockwiseB) {
            if (isClockwiseA) {
                return Segment.capRadian(angleOrigin + Segment.halfPI);
            } else {
                return Segment.capRadian(angleOrigin - Segment.halfPI);
            }
        } else if (Math.abs(2 * radius / line.getLength()) <= 1) {
            double angleRight = Math.acos(2 * radius / line.getLength());
            angleFinal = Segment.capRadian(isClockwiseA ? angleOrigin + angleRight : angleOrigin - angleRight);
            double newX = originXa + 2 * radius * Math.cos(angleFinal);
            //     changed sign, because of screen coordinates
            //double newY=originYa+2*radius*Math.sin(angleFinal);
            double newY = originYa - 2 * radius * Math.sin(angleFinal);
            dx = originXb - newX;
            //     changed sign, because of screen coordinates
            // dy=originYb-newY;
            dy = newY - originYb;
            line.setLength(Math.sqrt(dx * dx + dy * dy));

            return angleFinal;
        } else {
            line.setLength(-1);
            return 0;
        }
    }

    // возвращает true, когда узел node является соседом данного узла
    public boolean isNeighbor(Node node) {
        synchronized (links) {
            for (Link l : links) {
                if (l.getChild() == node) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addNeighbor(Node node, double radius, int robotSize, byte[][] passabilityArray) {
        if (isNeighbor(node) || this == node) {
            return false;
        }
        Segment[][] segments = new Segment[4][3];
        Segment[] s2;
        Segment[] minS = null;
        double minLength = -1;
        double length = -1;
        for (int i = 0; i < 4; ++i) {
            if ((s2 = computeSegments(segments[i], node, (i < 2), (i == 1 || i == 3), radius)) != null) {
                if (Link.isSegmentsBlocked(s2, robotSize, passabilityArray)) {
                    s2 = null;
                } else {
                    length = s2[0].getLength() + s2[1].getLength() + s2[2].getLength() +
                            s2[0].getRadiansTotal() + s2[2].getRadiansTotal();
                }
            } else {
                length = -1;
            }
            if (length != -1 && length != 0 && (minLength == -1 || minLength > length)) {
                minS = s2;
                minLength = length;
            }
        }
        if (minS == null) {
            return false;
        }
        synchronized (links) {
            links.add(new Link(node, minS));
        }
        return true;
    }

    public Segment[] computeSegments(Segment[] segments, Node node,
                                      boolean isClockwiseA, boolean isClockwiseB, double radius) {
        Segment seg0 = new Segment();
        seg0.setIsStraightLine(false);
        seg0.setIsClockwise(isClockwiseA);
        seg0.setRadius(radius);

        Segment seg1 = new Segment();
        Segment seg2 = new Segment();
        seg2.setIsStraightLine(false);
        seg2.setIsClockwise(isClockwiseB);
        seg2.setRadius(radius);

        seg0.setStartAngle(isClockwiseA ? Segment.capRadian(direction + Segment.halfPI) :
                Segment.capRadian(direction - Segment.halfPI));

        seg0.setOriginX(position.x - radius * Math.cos(seg0.getStartAngle()));
        // changed sign, because of screen coordinates
        //seg0.setOriginY((double)coordinate.y-radius*Math.sin(seg0.getStartAngle()));
        seg0.setOriginY(position.y + radius * Math.sin(seg0.getStartAngle()));

        double radStopB = (isClockwiseB ? Segment.capRadian(node.direction + Segment.halfPI) :
                Segment.capRadian(node.direction - Segment.halfPI));

        seg2.setOriginX(node.getX() - radius * Math.cos(radStopB));
        // changed sign, because of screen coordinates
        // seg2.setOriginY((double)node.getY()-radius*Math.sin(radStopB));
        seg2.setOriginY(node.getY() + radius * Math.sin(radStopB));

        // may be for some optimization
        double originX0 = seg0.getOriginX();
        double originY0 = seg0.getOriginY();
        double originX2 = seg2.getOriginX();
        double originY2 = seg2.getOriginY();
        if (!(originX0 == originX2 && originY0 == originY2 && isClockwiseA == isClockwiseB)) {
            double radStopA = findTouchPoints(originX0, originY0, originX2, originY2,
                    isClockwiseA, isClockwiseB, radius, seg1);
            if (seg1.getLength() < 0) {
                return null;
            }

            seg0.setRadiansTotal(isClockwiseA ? Segment.capRadian(seg0.getStartAngle() - radStopA)
                    : Segment.capRadian(radStopA - seg0.getStartAngle()));
            seg0.setLength(seg0.getRadiansTotal() * radius);
            seg2.setStartAngle(isClockwiseA == isClockwiseB ?
                    radStopA : Segment.capRadian(radStopA + Math.PI));
            seg2.setRadiansTotal(isClockwiseB ? Segment.capRadian(seg2.getStartAngle() - radStopB)
                    : Segment.capRadian(radStopB - seg2.getStartAngle()));
            seg2.setLength(seg2.getRadiansTotal() * radius);

            // Finish information on the straight line segment (length already set above)
            seg1.setIsStraightLine(true);
            seg1.setOriginX(originX0 + radius * Math.cos(radStopA));
            // changed sign, because of screen coordinates
            //seg1.setOriginY(seg0.getOriginY() + radius* Math.sin(radStopA));
            seg1.setOriginY(originY0 - radius * Math.sin(radStopA));
            seg1.setStartAngle(isClockwiseA ? Segment.capRadian(radStopA - Segment.halfPI) :
                    Segment.capRadian(radStopA + Segment.halfPI));
            seg1.setRadiansTotal(0);
        } else {
            seg0.setRadiansTotal(isClockwiseA ? Segment.capRadian(seg0.getStartAngle() - radStopB)
                    : Segment.capRadian(radStopB - seg0.getStartAngle()));
            seg0.setLength(seg0.getRadiansTotal() * radius);
            seg2.setStartAngle(radStopB);
            seg2.setRadiansTotal(0);
            seg2.setLength(0);

            // Finish information on the straight line segment (length already set above)
            seg1.setIsStraightLine(true);
            seg1.setOriginX(originX0 + radius * Math.cos(radStopB));
            // changed sign, because of screen coordinates
            //seg1.setOriginY(seg0.getOriginY() + radius* Math.sin(radStopA));
            seg1.setOriginY(originY0 - radius * Math.sin(radStopB));
            seg1.setStartAngle(isClockwiseA ? Segment.capRadian(radStopB - Segment.halfPI) :
                    Segment.capRadian(radStopB + Segment.halfPI));
            seg1.setRadiansTotal(0);
            seg1.setLength(0);
        }
        segments[0] = seg0;
        segments[1] = seg1;
        segments[2] = seg2;
        return segments;
    }

    public double getX() {
        return position.x;
    }
//
//    public void setX(double x) {
//        this.x = x;
//    }

    public double getY() {
        return position.y;
    }

//    public void setY(double y) {
//        this.y = y;
//    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    //    public void setG(double G) {this.G=G; F=G+H;}
//    public double getScoreF() {return F;}
//    public double getH() {return H;}
//    public double getScoreG() {return G;}
//    public void calculateH(Node n, double robotRadius, byte[][] passabilityArray) {
//        int weight;
//        if(passabilityArray==null)
//            weight=0;
//        else {
//            if(0<=n.getX() && n.getX()<passabilityArray.length &&
//                       0<=n.getY() && n.getY()<passabilityArray[0].length) {
//                weight=(127-passabilityArray[(int)n.getX()][(int)n.getY()]+127-passabilityArray[(int)x][(int)y])/2;
//            } else {
//                weight=254;
//            }
//        }
//        Segment[][] s=new Segment[4][3];
//        double minLength=-1, length=-1;
//        int minIndex=-1;
//        for(int i=0; i<4; ++i)
//        {
//            boolean isClockwise1=(i<2);
//            boolean isClockwise2=(i==1 || i==3);
//            if((s[i]=computeSegments(s[i], n, isClockwise1, isClockwise2,
// robotRadius, -1, /*passabilityMap,*/ passabilityArray))!=null) {
//                length=s[i][0].getLength()+s[i][1].getLength()+s[i][2].getLength();
//            } else {
//                length=-1;
//            }
//            if(length!=-1 && (minLength==-1 || minLength>length)) {
//                minIndex=i;
//                minLength=length;
//            }
//        }
//        if(minIndex==-1)
//            H=Double.MAX_VALUE;
//        if(s[minIndex]!=null)
//        {
//            H=(s[minIndex][0].getLength()+s[minIndex][1].getLength()+s[minIndex][2].getLength())*(weight+1);
//            // turning penalty
//            H+=(s[minIndex][0].getRadiansTotal()+s[minIndex][2].getRadiansTotal())*(weight+1);
//        }
//        F=G+H;
//    }
//    public static double calculateG(double distanceToParent, int linkWeight, double radiansTotal, double parentG) {
//        return distanceToParent*(double)(linkWeight+1)+parentG+radiansTotal*(double)(linkWeight+1);
//    }
//    public void setParent(Node parent, double distanceToParent, int linkWeight, double linkRadiansTotal) {
//        this.parent=parent;
//        G=calculateG(distanceToParent, linkWeight, linkRadiansTotal, parent.getScoreG());
//        F=G+H;
//    }
//    public Node getParent()
//    {
//        return parent;
//    }
    public List<Link> getLinks() {
        return links;
    }

    public boolean removeNeighbor(Node node) {
        synchronized (links) {
            for (Iterator<Link> it = links.iterator(); it.hasNext(); ) {
                Link link = it.next();
                if (link.getChild() == node) {
                    it.remove();
                }
            }
        }
        return true;
    }

    public boolean getIsRobotMade() {
        return isRobotMade;
    }

    // очистка всех параметров для алгоритма поиска пути
//    public void clear() {
//        parent=null;
//        F=0;
//        G=0;
//        H=0;
//    }
    public void setIsRobotMade(boolean isRobotMade) {
        this.isRobotMade = isRobotMade;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public Link getLinkByChild(Node child) {
        synchronized (links) {
            for (Link l : links) {
                if (l.getChild() == child) {
                    return l;
                }
            }
        }
        return null;
    }

    public Point getPoint() {
        return new Point((int) position.x, (int) position.y);
    }

    public void removeLink(Link link) {
        synchronized (links) {
            links.remove(link);
        }
    }
}
