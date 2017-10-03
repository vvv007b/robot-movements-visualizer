package com.mcst.paths.finding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class MapInfo implements Cloneable {
    private BufferedImage image;
    private BufferedImage passabilityMap;
    private BufferedImage realityMap;
    //http://infotechgems.blogspot.ru/2011/11/java-collections-performance-time.html
    //private Map<Point, List<Node>> nodes=new HashMap<Point, List<Node>>();
    private Map<Point, List<Node>> nodes = Collections.synchronizedMap(new HashMap<>());
    //http://www.skipy.ru/technics/synchronization.html
    //private Map<Point, List<Node>> nodes=new Hashtable<Point, List<Node>>();

    private int scale;
    private MapColors mapColors = new MapColors();

    private byte[][] passabilityArray = null;
    private boolean isChanged = false;

    //private boolean passabilityChanged=false;

    private byte[][] realityArray;

    public final Object realityMapLock = new Object();

    public MapInfo() {
        image = null;
        passabilityMap = null;
        realityMap = null;
        scale = 60;
    }

    public MapInfo clone() throws CloneNotSupportedException {
        MapInfo obj = (MapInfo) super.clone();
        if (image != null) {
            ColorModel cmImage = image.getColorModel();
            boolean isAlphaImage = cmImage.isAlphaPremultiplied();
            WritableRaster rasterImage = image.copyData(null);
            obj.image = new BufferedImage(cmImage, rasterImage, isAlphaImage, null);
        }
        if (passabilityMap != null) {
            ColorModel cmPassability = passabilityMap.getColorModel();
            boolean isAlphaPassability = cmPassability.isAlphaPremultiplied();
            WritableRaster rasterPassability = passabilityMap.copyData(null);
            obj.passabilityMap = new BufferedImage(cmPassability, rasterPassability, isAlphaPassability, null);
        }
        if (realityMap != null) {
            ColorModel cmReality = realityMap.getColorModel();
            boolean isAlphaReality = cmReality.isAlphaPremultiplied();
            WritableRaster rasterReality = realityMap.copyData(null);
            obj.realityMap = new BufferedImage(cmReality, rasterReality, isAlphaReality, null);
        }
        if (passabilityArray != null) {
            obj.passabilityArray = passabilityArray.clone();
        }
        if (realityArray != null) {
            obj.realityArray.clone();
        }

        obj.nodes = new HashMap<>(nodes);

        return obj;
    }

    public Image getImage() {
        return image;
    }

    public BufferedImage getPassabilityMap() {
        return passabilityMap;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int calculatePassability(double radius) {
        if (image == null) {
            return 0;
        }
        passabilityMap = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = passabilityMap.createGraphics();
        int average = 0;
        boolean isBlack = false;
        passabilityArray = new byte[image.getWidth()][image.getHeight()];
        for (int i = 0; i < passabilityArray.length; ++i) {
            for (int j = 0; j < passabilityArray[0].length; ++j) {
                passabilityArray[i][j] = -128;
            }
        }
        for (int i = scale; i <= image.getHeight(); i += scale) {
            for (int j = scale; j <= image.getWidth(); j += scale) {
                int oldPassability = 100;
                for (int x = j - scale; x < j; ++x) {
                    for (int y = i - scale; y < i; ++y) {
                        Color color = new Color(image.getRGB(x, y));
                        int passability = mapColors.getPassability(color);
                        if (passability == 0) {
                            int aroundPassability = checkAround(new Point(x, y));
                            if (aroundPassability == 0) {
                                average = 0;
                                isBlack = true;
                                break;
                            } else {
                                average += aroundPassability;
                            }
                        } else if (passability == -1) {
                            average += oldPassability;
                        } else if (passability > 0) {
                            oldPassability = passability;
                            average += passability;
                        }
                    }
                    if (isBlack) {
                        isBlack = false;
                        break;
                    }
                }

                average = average / (scale * scale);

                g2.setColor(new Color(average, average, average));
                g2.fillRect(j - scale, i - scale, scale, scale);

                for (int x = j - scale; x < j; ++x) {
                    for (int y = i - scale; y < i; ++y) {
                        passabilityArray[x][y] = (byte) (average - 128);
                    }
                }
                average = 0;
            }
        }

        g2.dispose();
        //passabilityChanged=true;
        return makeNodesFromPassability(radius);
    }


    private int checkAround(Point point) {
        int average = 0;
        int counter = 0;
        int current;
        for (int xi = point.x - 1; xi <= point.x + 1; ++xi) {
            for (int yi = point.y - 1; yi <= point.y + 1; ++yi) {
                if (xi < 0 || xi == point.x || xi >= image.getWidth() || yi < 0 ||
                        yi == point.y || yi >= image.getHeight()) {
                    continue;
                }
                Color color = new Color(image.getRGB(xi, yi));
                if ((current = mapColors.getPassability(color)) == 0) {
                    return 0;
                }
                average += current;
                counter++;
            }
        }
        return average / counter;
    }

    private int makeNodesFromPassability(double radius) {
        if (passabilityMap == null) {
            return 0;
        }

        nodes.clear();

        int nodesCounter = 0;
        for (int y = scale / 2; y < passabilityMap.getHeight() - scale / 2; y += scale) {
            for (int x = scale / 2; x < passabilityMap.getWidth() - scale / 2; x += scale) {
                int weight = 255 - passabilityMap.getRGB(x, y) & 0xFF;
                if (weight != 255) {
                    List<Node> newNodes = new ArrayList<>();
                    //http://www.skipy.ru/technics/synchronization.html
                    //List<Node> newNodes=new Vector<Node>();

                    newNodes.add(new Node(new Point2D.Double(x, y), 0));
                    newNodes.add(new Node(new Point2D.Double(x, y), Math.PI / 4));
                    newNodes.add(new Node(new Point2D.Double(x, y), Math.PI / 2));
                    newNodes.add(new Node(new Point2D.Double(x, y), 3 * Math.PI / 4));
                    newNodes.add(new Node(new Point2D.Double(x, y), Math.PI));
                    newNodes.add(new Node(new Point2D.Double(x, y), 5 * Math.PI / 4));
                    newNodes.add(new Node(new Point2D.Double(x, y), 3 * Math.PI / 2));
                    newNodes.add(new Node(new Point2D.Double(x, y), 7 * Math.PI / 4));

                    nodes.put(new Point(x, y), newNodes);
                    nodesCounter += 8;
                }
            }
        }

        class LinkingThread extends Thread {
            private double radius;
            private int first;
            private int last;

            private LinkingThread(double radius, int first, int last) {
                this.radius = radius;
                this.first = first;
                this.last = last;
            }

            public void run() {
                List<Node> nodeList = getNodesInList();
                int last = this.last;
                for (int i = first; i < last; i++) {
                    addLinksAroundCell24(nodeList.get(i), radius, false);
                }
            }
        }

        try {
            int processors = Runtime.getRuntime().availableProcessors();
            //System.out.println("availableProcessors = " + processors);
            int size = getNodesInList().size();
            LinkingThread[] threads = new LinkingThread[processors];
            for (int i = 0; i < processors; i++) {
                threads[i] = new LinkingThread(radius, (i * size) / processors, ((i + 1) * size) / processors);
                threads[i].start();
            }
            for (int i = 0; i < processors; i++) {
                threads[i].join();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        isChanged = true;
        return nodesCounter;
    }

    public void addLinksAroundCell24(Node node, double radius, boolean doubleConnections) {
        int borderY = (int) node.getY() + scale * 2;
        int borderX = (int) node.getX() + scale * 2;
        synchronized (node.getLinks()) {
            for (int yi = (int) node.getY() - scale * 2; yi <= borderY; yi += scale) {
                for (int xi = (int) node.getX() - scale * 2; xi <= borderX; xi += scale) {
                    Point cellCenter = getCellCenterPoint(new Point(xi, yi));
                    if (cellCenter != null) {
                        ArrayList<Node> neighbours = getNodes(cellCenter, 0);
                        for (Node ni : neighbours) {
                            node.addNeighbor(ni, radius, scale, passabilityArray);
                            if (doubleConnections) {
                                ni.addNeighbor(node, radius, scale, passabilityArray);
                            }
                        }
                    }
                }
            }
        }
        isChanged = true;
    }

    public List<Node> getNodesByChild24(Node node) {
        int borderX = (int) node.getX() + scale;
        int borderY = (int) node.getY() + scale;
        Point centerPoint = getCellCenterPoint(new Point((int) node.getX(), (int) node.getY()));
        List<Node> result = new ArrayList<>();

        for (int yi = (int) node.getY() - scale; yi <= borderY; yi += scale) {
            for (int xi = (int) node.getX() - scale; xi <= borderX; xi += scale) {
                Point cellCenter = getCellCenterPoint(new Point(xi, yi));
                if (cellCenter != null) {
                    if (!cellCenter.equals(centerPoint)) {
                        ArrayList<Node> neighbours = getNodes(cellCenter, 0);
                        result.addAll(neighbours.stream().filter(ni ->
                                ni.isNeighbor(node)).collect(Collectors.toList()));
                    }
                }
            }
        }

        borderX = (int) node.getX() + scale * 2;
        borderY = (int) node.getY() + scale * 2;
        int lowBorderY = (int) node.getY() - scale * 2;
        for (int yi = lowBorderY; yi <= borderY; yi += scale) {
            for (int xi = (int) node.getX() - scale * 2; xi <= borderX; xi = yi != lowBorderY && yi != borderY ?
                    xi + scale : xi + 3 * scale) {
                Point cellCenter = getCellCenterPoint(new Point(xi, yi));
                if (cellCenter != null) {
                    ArrayList<Node> neighbours = getNodes(cellCenter, 0);
                    result.addAll(neighbours.stream().filter(ni -> ni.isNeighbor(node)).collect(Collectors.toList()));
                }
            }
        }
        return result;
    }
//    public void addLinksAroundCell8(Node n, double radius, boolean doubleConnections) {
//        int yBorder = (int) n.getY() + scale;
//        int xBorder = (int) n.getX() + scale;
//        for (int yi = (int) n.getY() - scale; yi <= yBorder; yi += scale) {
//            for (int xi = (int) n.getX() - scale; xi <= xBorder; xi += scale) {
//                Point cellCenter = getCellCenterPoint(xi, yi);
//                if (cellCenter != null) {
//                    ArrayList<Node> neighbours = getNodes(cellCenter.x, cellCenter.y, 0);
//                    for (Node ni : neighbours) {
//                        n.addNeighbor(ni, radius, scale, passabilityArray);
//                        if (doubleConnections)
//                            ni.addNeighbor(n, radius, scale, passabilityArray);
//                    }
//                }
//            }
//        }
//        isChanged = true;
//    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public Map<Point, List<Node>> getNodes() {
        return nodes;
    }

    private ArrayList<Node> getNodes(Point point, int diameter) {
        //http://www.skipy.ru/technics/synchronization.html
        //public List<Node> getNodes(int x, int y, int diameter) {
        //http://www.skipy.ru/technics/synchronization.html
        ArrayList<Node> result = new ArrayList<>();
        //List<Node> result=new Vector<Node>();

        //http://howtodoinjava.com/2013/03/26/performance-comparison-of-different-for-loops-in-java/
        if (diameter == 0) {
            if (nodes.containsKey(point)) {
                result.addAll(nodes.get(point));
            }
        } else {
            for (int i = (-1) * diameter; i <= diameter; ++i) {
                for (int j = (-1) * diameter; j <= diameter; ++j) {
                    Point newPoint = new Point(point.x + i, point.y + j);
                    if (nodes.containsKey(newPoint)) {
                        result.addAll(nodes.get(newPoint));
                    }
                }
            }
        }
        return result;
    }

    public List<Node> getNodesInList() {
        List<Node> result = new ArrayList<>();
        synchronized (nodes) {
            for (Map.Entry<Point, List<Node>> newNodes : nodes.entrySet()) {
                result.addAll(newNodes.getValue());
            }
        }
        return result;
    }

    public void addNode(Node node) {
        synchronized (nodes) {
            if (nodes.containsKey(node.getPoint())) {
                nodes.get(node.getPoint()).add(node);
            } else {
                List<Node> nodesToAdd = new ArrayList<>();
                //http://www.skipy.ru/technics/synchronization.html
                nodesToAdd.add(node);
                nodes.put(node.getPoint(), nodesToAdd);
            }
            isChanged = true;
        }
    }

    private void addNodes(Point center) {
        List<Node> newNodes = new ArrayList<>();
        //http://www.skipy.ru/technics/synchronization.html
        //List<Node> newNodes=new Vector<Node>();

        for (double direction = 0; direction < 2 * Math.PI; direction += Math.PI / 4) {
            newNodes.add(new Node(new Point2D.Double(center.x, center.y), direction));
        }
//        Point point = new Point(x, y);
        if (nodes.containsKey(center)) {
            nodes.get(center).addAll(newNodes);
        } else {
            nodes.put(center, newNodes);
        }
        isChanged = true;
    }

//    public boolean containsNode(Node node) {
//        if (node == null)
//            return false;
//        Point point = node.getPoint();
//        if (nodes.containsKey(point)) {
//            return nodes.get(point).contains(node);
//        }
//        return false;
//    }

    public synchronized boolean removeNode(Node node) {
        if (node == null) {
            return false;
        }
        Point point = node.getPoint();
        if (nodes.containsKey(point)) {
            int borderY = (int) node.getY() + scale * 2;
            int borderX = (int) node.getX() + scale * 2;

            for (int yi = (int) node.getY() - scale * 2; yi <= borderY; yi += scale) {
                for (int xi = (int) node.getX() - scale * 2; xi <= borderX; xi += scale) {
                    Point cellCenter = getCellCenterPoint(new Point(xi, yi));
                    if (cellCenter != null) {
                        ArrayList<Node> neighbours = getNodes(cellCenter, 0);
                        for (Node ni : neighbours) {
                            ni.removeNeighbor(node);
                        }
                    }
                }
            }

            node.getLinks().clear();
            nodes.get(point).remove(node);
            if (nodes.get(point).size() == 0) {
                nodes.remove(point);
            }
            isChanged = true;
            return true;
        }
        return false;
    }

//    public boolean isThereANode(int x, int y, int diameter) {
//        if (diameter == 0) {
//            return nodes.containsKey(new Point(x, y));
//        } else {
//            for (int i = (-1) * diameter; i <= diameter; ++i) {
//                for (int j = (-1) * diameter; j <= diameter; ++j) {
//                    Point point = new Point(x + i, y + j);
//                    if (nodes.containsKey(point))
//                        return true;
//                }
//            }
//            return false;
//        }
//    }

    public int getScale() {
        return scale;
    }

    public void setRealityMap(BufferedImage realityMap) {
        synchronized (realityMapLock) {
            this.realityMap = realityMap;
            if (realityMap != null) {
                realityArray = new byte[realityMap.getWidth()][realityMap.getHeight()];
                for (int x = 0; x < realityMap.getWidth(); ++x) {
                    for (int y = 0; y < realityMap.getHeight(); ++y) {
                        Color color = new Color(realityMap.getRGB(x, y));
                        if (mapColors.isColorsSimilar(MapColors.PUDDLE_COLOR, color)) {
                            realityArray[x][y] = -127;
                        } else {
//                        System.out.println( (byte) ((realityMap.getRGB(x, y) & 0xFF) - 128));
                            realityArray[x][y] = (byte) ((realityMap.getRGB(x, y) & 0xFF) - 128);
                        }
                    }
                }
            }
        }
    }

    public BufferedImage getRealityMap() {
        return realityMap;
    }

    public byte[][] getPassabilityArray() {
        return passabilityArray;
    }

    public void removePassability() {
        passabilityMap = null;
        passabilityArray = null;
    }

    public void setPassabilityPoint(Point point, int weight) {
        if (point.x < 0 || point.x >= passabilityMap.getWidth() ||
                point.y < 0 || point.y >= passabilityMap.getHeight()) {
            return;
        }
        passabilityArray[point.x][point.y] = (byte) (127 - weight);
        //passabilityChanged=true;
    }


    public int getRealityWeight(Point point) {
        if (realityMap == null) {
            return 255;
        }
        if (point.x < 0 || point.x >= realityMap.getWidth() || point.y < 0 || point.y >= realityMap.getHeight()) {
            return 255;
        }
        int weight = 127;
        synchronized (realityMapLock) {         //lock should be at start of this method
            weight -= realityArray[point.x][point.y];
        }
//        if (weight == 0) {
//            System.out.println(0);
//        }
        return weight;
    }

    public int getPassabilityWeight(Point point) {
        if (passabilityMap == null) {
            return 255;
        }
        if (point.x < 0 || point.x >= passabilityMap.getWidth() ||
                point.y < 0 || point.y >= passabilityMap.getHeight()) {
            return 255;
        }
        // first option is may be right, because I use it to take old passability, while changing passability array
        return 255 - (passabilityMap.getRGB(point.x, point.y) & 0xFF);
        //return 127-passabilityArray[x][y];
    }

    public Point getCellCenterPoint(Point point) {
        if (passabilityMap == null) {
            return null;
        }
        if (point.x < 0 || point.x >= passabilityMap.getWidth() ||
                point.y < 0 || point.y >= passabilityMap.getHeight()) {
            return null;
        }
        return new Point(scale / 2 + scale * (point.x / scale), scale / 2 + scale * (point.y / scale));
    }

    // returns true when cell is passable
    // returns false otherwise
    public boolean calculatePassabilityForCell(Point center, double robotRadius) {
        if (center.x - scale / 2 < 0 || center.x + scale / 2 >= passabilityMap.getWidth() ||
                center.y - scale / 2 < 0 || center.y + scale / 2 >= passabilityMap.getHeight() ||
                passabilityMap == null) {
            return false;
        }

        int average = 0;
        boolean isBlack = false;
        int counter = 0;
        for (int x = center.x - (scale / 2); x < center.x + (scale / 2); ++x) {
            for (int y = center.y - (scale / 2); y < center.y + (scale / 2); ++y) {
                int passability = passabilityArray[x][y] + 128;
                if (passability == 0) {
                    average = 0;
                    isBlack = true;
                    counter++;
                    break;
                }
                average += passability;
                counter++;
            }
            if (isBlack) {
                break;
            }
        }
        average = average / counter;
        Graphics2D g2 = passabilityMap.createGraphics();
        g2.setColor(new Color(average, average, average));
        g2.fillRect(center.x - (scale / 2), center.y - (scale / 2), scale, scale);

        for (int x = center.x - scale / 2; x < center.x + scale / 2; ++x) {
            for (int y = center.y - scale / 2; y < center.y + scale / 2; ++y) {
                passabilityArray[x][y] = (byte) (average - 128);
            }
        }

        List<Node> nodesToChange = getNodes(center, 0);
        //passabilityChanged=true;
        if (average == 0) {
            nodesToChange.forEach(this::removeNode);
            return false;
        } else {
            if (nodesToChange.size() == 0) {
                addNodes(center);
                for (Node n : getNodes(center, 0)) {
                    addLinksAroundCell24(n, robotRadius, true);
                }
            }
        }
        return true;
    }

    public void refreshGraph() {
        if (nodes.size() == 0) {
            return;
        }
        for (Node node : getNodesInList()) {
            for (Iterator<Link> it = node.getLinks().iterator(); it.hasNext(); ) {
                Link link = it.next();
                if (Link.isSegmentsBlocked(link.getSegments(), scale, passabilityArray)) {
                    it.remove();
                }
            }
        }
    }

//    public int getNodesMapSize() {
//        return nodes.size();
//    }

    public boolean getIsChanged() {
        return isChanged;
    }

    public void setIsChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    private byte[][] getRealityArray() {
        return realityArray;
    }

    //  public void setPassabilityChanged(boolean passabilityChanged) {this.passabilityChanged=passabilityChanged;}
//  public boolean isPassabilityChanged() {return passabilityChanged;}
}
