package com.robot.group.paths.finding;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

class SearchAlgorithm {
    private List<NodeForSearch> openList;
    private List<NodeForSearch> closeList;
    private List<Node> pathInNodes;
    private List<Link> pathInLinks;

    public SearchAlgorithm() {
        openList = new ArrayList<>();
        closeList = new ArrayList<>();
        pathInNodes = new ArrayList<>();
        pathInLinks = new ArrayList<>();
    }

//    private List<NodeForSearch> nodes = new ArrayList<>();

    public boolean searchAStar(Node nodeStart, Node nodeFinish, int robotSize, double robotRadius,
                               byte[][] passabilityArray, Robot searchingRobot, Robot blockingRobot,
                               boolean findExact) {
        clean();

//        for(Node n: nodesToPut){
//            n.clear();
// }
        if (nodeStart != null && nodeFinish != null) {
            openList.add(new NodeForSearch(nodeStart, nodeFinish, robotRadius, passabilityArray));
//            for (Node n : nodes)
//                n.calculateH(nodeFinish, robotRadius, passabilityArray);
            NodeForSearch finishForSearch;
            finishForSearch = iterationsAStar(nodeFinish, robotSize, passabilityArray, searchingRobot, blockingRobot);
            if (finishForSearch != null) {
                fillPath(finishForSearch);
                fillPathInLinks();
                return true;
            } else if (!findExact) {
//                List<Link> n_finishLinks = nodeFinish.getLinks();
//                for (Link link : n_finishLinks) {
//                    Node n = link.getChild();
                List<Node> neighbours = searchingRobot.getMap().getNodesByChild24(nodeFinish);
                for (Node n : neighbours) {
                    if (n.getX() != nodeStart.getX() && n.getY() != nodeStart.getY() &&
                            !Hypervisor.isPointOccupiedAsFinish(new Point2D.Double(n.getX(), n.getY()),
                                    searchingRobot)) {
                        clean();
                        openList.add(new NodeForSearch(nodeStart, n, robotRadius, passabilityArray));
                        finishForSearch = iterationsAStar(n, robotSize, passabilityArray,
                                searchingRobot, blockingRobot);
                        if (finishForSearch != null) {
                            fillPath(finishForSearch);
                            fillPathInLinks();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // return finish
    private NodeForSearch iterationsAStar(Node nodeFinish, int robotSize, byte[][] passabilityArray,
                                          Robot searchingRobot, Robot blockingRobot) {
        if (blockingRobot != null) {
            if (Hypervisor.checkRobotInCoordinates(new Point((int) nodeFinish.getX(), (int) nodeFinish.getY()),
                    robotSize, searchingRobot) != null) {
                return null;
            }
        }
        while (!openList.isEmpty()) {
            double minF = openList.get(0).getScoreF();
            int index = 0;

            for (int i = 0; i < openList.size(); ++i) {
                if (openList.get(i).getScoreF() < minF) {
                    minF = openList.get(i).getScoreF();
                    index = i;
                }

            }

            NodeForSearch currentNode = openList.get(index);
            openList.remove(index);
            closeList.add(currentNode);
//            List<Link> nonSynclinks = currentNode.getLinks();
//            List<Link> links=Collections.synchronizedList(nonSynclinks);
            ConcurrentLinkedQueue<Link> links;
            synchronized (currentNode.getLinks()) {
                links = new ConcurrentLinkedQueue<>(currentNode.getLinks());
            }
            //synchronized (links) {
            //for(Iterator<Link> it=currentNode.getLinks().iterator(); it.hasNext();)
            for (Iterator<Link> it = links.iterator(); it.hasNext(); ) {
                Link link = it.next();
                NodeForSearch newNode = new NodeForSearch(link.getChild(), nodeFinish, robotSize, passabilityArray);
                //if(closeList.indexOf(newNode)==-1)
                if (!closeList.contains(newNode)) {
                    int indexOfNewNode;
                    if ((indexOfNewNode = openList.indexOf(newNode)) == -1) {
                        if (Link.isSegmentsBlocked(link.getSegments(), robotSize, passabilityArray)) {
                            currentNode.getNode().removeLink(link);
                            it.remove();
                            continue;
                        }
                        if (blockingRobot != null && searchingRobot != null) {
//                            if (Link.isSegmentsBlockedByRobot(link.getSegments(),
//                                    (int) blockingRobot.getX(), (int) blockingRobot.getY(),
// blockingRobot.getAzimuth(),
//                                    robotSize, 0)) {
                            if (Hypervisor.checkRobotsOnWay(searchingRobot, link, 0) != null) {
                                it.remove();
                                continue;
                            }
                        }

                        openList.add(newNode);
                        newNode.setParent(currentNode, link.getLength(), link.getWeight(), link.getRadiansTotal());
                        if (newNode.getNode() == nodeFinish) {
                            return newNode;
                        }
                    } else {
                        if (Link.isSegmentsBlocked(link.getSegments(), robotSize, passabilityArray)) {
                            // deleted creation of neighborhood because I rely on makeConnectionsAround8
                            // and increasing calculation speed
                            currentNode.getNode().removeLink(link);
                            it.remove();
                            continue;
                        }
                        if (blockingRobot != null && searchingRobot != null) {
                            if (Hypervisor.checkRobotsOnWay(searchingRobot, link, 0) != null) {
                                it.remove();
                                continue;
                            }
                        }

                        if (NodeForSearch.calculateG(link.getLength(), link.getWeight(),
                                link.getRadiansTotal(), currentNode.getScoreG()) < newNode.getScoreG()) {
                            openList.get(indexOfNewNode).setParent(currentNode, link.getLength(),
                                    link.getWeight(), link.getRadiansTotal());
                        }
                    }
                }
            }
            //}
        }
        return null;
    }

    private void clean() {
        openList.clear();
        closeList.clear();
        pathInNodes.clear();
        pathInLinks.clear();
    }

    public List<Node> getPath() {
        return pathInNodes;
    }

    public boolean hasPath() {
        return !pathInNodes.isEmpty();
    }

    private void fillPath(NodeForSearch finish) {
        pathInNodes.add(0, finish.getNode());
        NodeForSearch nodeForSearch = finish.getParent();
        while (nodeForSearch != null) {
            pathInNodes.add(0, nodeForSearch.getNode());
            nodeForSearch = nodeForSearch.getParent();
        }
    }

    private void fillPathInLinks() {
        if (!pathInNodes.isEmpty()) {
            Node fromNode = pathInNodes.get(0);
            for (int i = 1; i < pathInNodes.size(); ++i) {
                pathInLinks.add(fromNode.getLinkByChild(pathInNodes.get(i)));
                fromNode = pathInNodes.get(i);
            }
        }
    }

    public List<Link> getPathInLinks() {
        return pathInLinks;
    }

    // возвращает текущий и последующие связи в пути
    public List<Link> getNextLinks(Link currentLink, int amount) {
        int index = pathInLinks.indexOf(currentLink);
        // существует и не последний
        if (index != -1) {
            List<Link> result = new ArrayList<>();
            for (int i = index; i < pathInLinks.size() && amount > 0; ++i, --amount) {
                result.add(pathInLinks.get(i));
            }
            return result;
        }
        return null;
    }

    private static class NodeForSearch {
        private Node node;
        private double scoreF = 0;           //scoreF
        private double scoreH = 0;           //scoreH
        private double scoreG = 0;        //G
        private NodeForSearch parent = null;

        public NodeForSearch(Node node, Node finish, double robotRadius, byte[][] passabilityArray) {
            this.node = node;
            calculateH(finish, robotRadius, passabilityArray);
        }

        public static double calculateG(double distanceToParent, int linkWeight, double radiansTotal, double parentG) {
            return distanceToParent * (double) (linkWeight + 1) + parentG + radiansTotal * (double) (linkWeight + 1);
        }

        public void calculateH(Node finish, double robotRadius, byte[][] passabilityArray) {
            int weight;
            if (passabilityArray == null) {
                weight = 0;
            } else {
                if (0 <= finish.getX() && finish.getX() < passabilityArray.length &&
                        0 <= finish.getY() && finish.getY() < passabilityArray[0].length) {
                    weight = (127 - passabilityArray[(int) finish.getX()]
                            [(int) finish.getY()] + 127 - passabilityArray[(int) node.getX()][(int) node.getY()]) / 2;
                } else {
                    weight = 254;
                }
            }
            Segment[][] segs = new Segment[4][3];
            double minLength = -1;
            double length;
            int minIndex = -1;
            for (int i = 0; i < 4; ++i) {
                boolean isClockwise1 = (i < 2);
                boolean isClockwise2 = (i == 1 || i == 3);
                if ((segs[i] = finish.computeSegments(segs[i], finish, isClockwise1, isClockwise2, robotRadius)) !=
                        null) {
                    length = segs[i][0].getLength() + segs[i][1].getLength() + segs[i][2].getLength();
                } else {
                    length = -1;
                }
                if (length != -1 && (minLength == -1 || minLength > length)) {
                    minIndex = i;
                    minLength = length;
                }
            }
            if (minIndex == -1) {
                scoreH = Double.MAX_VALUE;
            }
            if (segs[minIndex] != null) {
                scoreH = (segs[minIndex][0].getLength() + segs[minIndex][1].getLength() +
                        segs[minIndex][2].getLength()) * (weight + 1);
                // turning penalty
                scoreH += (segs[minIndex][0].getRadiansTotal() + segs[minIndex][2].getRadiansTotal()) * (weight + 1);
            }
            scoreF = scoreG + scoreH;
        }

//        private Segment[] computeSegments(Segment[] segments, Node node, boolean isClockwiseA,
//                                          boolean isClockwiseB, double radius) {
//            Segment seg0 = new Segment();
//            Segment seg1 = new Segment();
//            Segment seg2 = new Segment();
//
//            seg0.setIsStraightLine(false);
//            seg0.setIsClockwise(isClockwiseA);
//            seg0.setRadius(radius);
//            seg2.setIsStraightLine(false);
//            seg2.setIsClockwise(isClockwiseB);
//            seg2.setRadius(radius);
//
//            seg0.setStartAngle(isClockwiseA ? Segment.capRadian(this.node.getDirection() + Segment.halfPI) :
// Segment.capRadian(this.node.getDirection() - Segment.halfPI));
//
//            seg0.setOriginX(this.node.getX() - radius * Math.cos(seg0.getStartAngle()));
//            // changed sign, because of screen coordinates
//            //seg0.setOriginY((double)coordinate.y-radius*Math.sin(seg0.getStartAngle()));
//            seg0.setOriginY(this.node.getY() + radius * Math.sin(seg0.getStartAngle()));
//
//            double radStopB = (isClockwiseB ? Segment.capRadian(node.getDirection() + Segment.halfPI) :
// Segment.capRadian(node.getDirection() - Segment.halfPI));
//
//            seg2.setOriginX(node.getX() - radius * Math.cos(radStopB));
//            // changed sign, because of screen coordinates
//            // seg2.setOriginY((double)node.getY()-radius*Math.sin(radStopB));
//            seg2.setOriginY(node.getY() + radius * Math.sin(radStopB));
//
//            // may be for some optimization
//            double originX0 = seg0.getOriginX();
//            double originY0 = seg0.getOriginY();
//            double originX2 = seg2.getOriginX();
//            double originY2 = seg2.getOriginY();
//            if (!(originX0 == originX2 && originY0 == originY2 && isClockwiseA == isClockwiseB)) {
//                double radStopA = Node.findTouchPoints(originX0, originY0, originX2, originY2,
//                        isClockwiseA, isClockwiseB, radius, seg1);
//                if (seg1.getLength() < 0) {
//                    return null;
//                }
//
//                seg0.setRadiansTotal(isClockwiseA ? Segment.capRadian(seg0.getStartAngle() - radStopA)
//                        : Segment.capRadian(radStopA - seg0.getStartAngle()));
//                seg0.setLength(seg0.getRadiansTotal() * radius);
//                seg2.setStartAngle(isClockwiseA == isClockwiseB ?
//                        radStopA : Segment.capRadian(radStopA + Math.PI));
//                seg2.setRadiansTotal(isClockwiseB ? Segment.capRadian(seg2.getStartAngle() - radStopB)
//                        : Segment.capRadian(radStopB - seg2.getStartAngle()));
//                seg2.setLength(seg2.getRadiansTotal() * radius);
//
//                // Finish information on the straight line segment (length already set above)
//                seg1.setIsStraightLine(true);
//                seg1.setOriginX(originX0 + radius * Math.cos(radStopA));
//                // changed sign, because of screen coordinates
//                //seg1.setOriginY(seg0.getOriginY() + radius* Math.sin(radStopA));
//                seg1.setOriginY(originY0 - radius * Math.sin(radStopA));
//                seg1.setStartAngle(isClockwiseA ? Segment.capRadian(radStopA - Segment.halfPI) :
// Segment.capRadian(radStopA + Segment.halfPI));
//                seg1.setRadiansTotal(0);
//            } else {
//                seg0.setRadiansTotal(isClockwiseA ? Segment.capRadian(seg0.getStartAngle() - radStopB)
//                        : Segment.capRadian(radStopB - seg0.getStartAngle()));
//                seg0.setLength(seg0.getRadiansTotal() * radius);
//                seg2.setStartAngle(isClockwiseA == isClockwiseB ?
//                        radStopB : Segment.capRadian(radStopB + Math.PI));
//                seg2.setRadiansTotal(0);
//                seg2.setLength(0);
//
//                // Finish information on the straight line segment (length already set above)
//                seg1.setIsStraightLine(true);
//                seg1.setOriginX(originX0 + radius * Math.cos(radStopB));
//                // changed sign, because of screen coordinates
//                //seg1.setOriginY(seg0.getOriginY() + radius* Math.sin(radStopA));
//                seg1.setOriginY(originY0 - radius * Math.sin(radStopB));
//                seg1.setStartAngle(isClockwiseA ? Segment.capRadian(radStopB - Segment.halfPI) :
// Segment.capRadian(radStopB + Segment.halfPI));
//                seg1.setRadiansTotal(0);
//                seg1.setLength(0);
//            }
//            segments[0] = seg0;
//            segments[1] = seg1;
//            segments[2] = seg2;
//            return segments;
//        }

//        public double getH() {
//            return scoreH;
//        }

        public double getScoreF() {
            return scoreF;
        }

        public double getScoreG() {
            return scoreG;
        }

        public void setParent(NodeForSearch parent, double distanceToParent, int linkWeight, double linkRadiansTotal) {
            this.parent = parent;
            scoreG = calculateG(distanceToParent, linkWeight, linkRadiansTotal, parent.getScoreG());
            scoreF = scoreG + scoreH;
        }

        public List<Link> getLinks() {
            return node.getLinks();
        }

        public Node getNode() {
            return node;
        }

        public NodeForSearch getParent() {
            return parent;
        }

        @Override
        public boolean equals(Object other) {
            return other != null && (other == this || other instanceof NodeForSearch &&
                    (node == ((NodeForSearch) other).getNode()));
        }
    }
}
