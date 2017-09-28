package com.mcst.paths.finding2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;
import java.util.TreeSet;

/**
 * Created by bocharov_n on 06.04.17.
 * Hybrid a star implementation
 */
public class SearchAlgorithm {
    private static final Logger log = LoggerFactory.getLogger(SearchAlgorithm.class);
    private Robot robot;
    private Node start;
    private Node finish;

    private TreeSet<Node> openNodes;

//
//    public static void main(String[] args) {
//        TreeSet<Node> set = new TreeSet<>();
////        Point2D.Double point = new Point2D.Double(0,0);
//        Node node1 = new Node(0,0,0);
//        Node node2 = new Node(0,0,1);
//        Node node3 = new Node(0,0,2);
//        set.add(node2);
//        set.add(node1);
//        set.add(node3);
//    }

    public SearchAlgorithm(Node start, Node finish) {
        this.start = start;
        this.finish = finish;
        this.openNodes = new TreeSet<>();
    }

    public boolean findPath() {
        openNodes.add(start);
        //maybe need correction of precision
        int endAccuracy = 5;
        //TODO: make this a parameter
        int step = 50;
        int iterations = 0;
        boolean found = false;
        boolean resign = false;
        while (!found && !resign) {
            if (iterations++ > 100000) {
                log.debug("Too many operations. Infinite loop");
                break;
            }
            if (openNodes.size() == 0) {
                resign = true;
                log.debug("Failed to find a path");
            } else {
                Node curNode = openNodes.first();
                double dist = Point2D.distance(
                        curNode.getX(),
                        curNode.getY(),
                        finish.getX(),
                        finish.getY());


            }
//            Node forwardStraightNode = new Node(
//                    new Point2D.Double(
//                            curNode.getX() + step * curNode.getAzimuth(),
//                            curNode.getY() + step * curNode.getAzimuth()),
//                    curNode.getAzimuth());
//            Node forwardRightNode = new Node(
//                    new Point2D.Double(
//                            curNode.getX() + step * (curNode.getAzimuth() - robot.getRotationAngle()),
//                            curNode.getY() + step * (curNode.getAzimuth() - robot.getRotationAngle())),
//                    curNode.getAzimuth() - robot.getRotationAngle());
//            Node forwardLeftNode = new Node(
//                    new Point2D.Double(
//                            curNode.getX() + step * (curNode.getAzimuth() + robot.getRotationAngle()),
//                            curNode.getY() + step * (curNode.getAzimuth() + robot.getRotationAngle())),
//                    curNode.getAzimuth() + robot.getRotationAngle());
//            Node backwardStraightNode = new Node(
//                    new Point2D.Double(
//                            curNode.getX() - step * curNode.getAzimuth(),
//                            curNode.getY() - step * curNode.getAzimuth()),
//                    curNode.getAzimuth());
//            Node backwardRightNode = new Node(
//                    new Point2D.Double(
//                            curNode.getX() - step * (curNode.getAzimuth() - robot.getRotationAngle()),
//                            curNode.getY() - step * (curNode.getAzimuth() - robot.getRotationAngle())),
//                    curNode.getAzimuth() - robot.getRotationAngle());
//            Node backwardLeftNode = new Node(
//                    new Point2D.Double(
//                            curNode.getX() - step * (curNode.getAzimuth() + robot.getRotationAngle()),
//                            curNode.getY() - step * (curNode.getAzimuth() + robot.getRotationAngle())),
//                    curNode.getAzimuth() + robot.getRotationAngle());

        }
        return true;
    }
}
