package com.robot.group.paths.finding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class MapInfo implements Cloneable {
    // ����� ���������
    private BufferedImage image;
    // ����� ������������
    private BufferedImage passabilityMap;
    // ����� ����������
    private BufferedImage realityMap;
    // ������ ����� �����
    //http://infotechgems.blogspot.ru/2011/11/java-collections-performance-time.html
    //private Map<Point, List<Node>> nodes=new HashMap<Point, List<Node>>();
    private Map<Point, List<Node>> nodes = Collections.synchronizedMap(new HashMap<>());
    //http://www.skipy.ru/technics/synchronization.html
    //private Map<Point, List<Node>> nodes=new Hashtable<Point, List<Node>>();
    // ����, �� ������� ����� �����
    // (��� �� �������� ������, ����� ��������� �� �����)
    //private Node standing;

    // ����-�����
    //private Node finish;
    // ������ ������ (�������)
    private int scale;
    // ����������� "����"-"����������� ������������"
    private MapColors mapColors = new MapColors();

    private byte[][] passabilityArray = null;
    private boolean isChanged = false;

    //private boolean passabilityChanged=false;

    private byte[][] realityArray = null;

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

    // ������� ������������
    // ���������� ���������� ��������� �����

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
                // ���������� ��� ����, ����� ����������� ����� ���� ���������
                int oldPassability = 100;
                for (int x = j - scale; x < j; ++x) {
                    for (int y = i - scale; y < i; ++y) {
                        Color color = new Color(image.getRGB(x, y));
                        int passability = mapColors.getPassability(color);
                        if (passability == 0) {
                            int aroundPassability = checkAround(x, y);
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

    // �������� �������� 8 �������� �� ������������
    // ���������� ������� ����������� ������������ 8 �������� ��� 0, ���� ������� ���� ��
    // ���� ������������ �������
    private int checkAround(int x, int y) {
        int average = 0;
        int counter = 0;
        int current;
        for (int xi = x - 1; xi <= x + 1; ++xi) {
            for (int yi = y - 1; yi <= y + 1; ++yi) {
                if (xi < 0 || xi == x || xi >= image.getWidth() || yi < 0 || yi == y || yi >= image.getHeight()) {
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

    // ������� ���� �� ������ ����� ������������
    // ���������� ���������� ��������� �����
    public int makeNodesFromPassability(double radius) {
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

                    newNodes.add(new Node(x, y, 0));
                    newNodes.add(new Node(x, y, Math.PI / 4));
                    newNodes.add(new Node(x, y, Math.PI / 2));
                    newNodes.add(new Node(x, y, 3 * Math.PI / 4));
                    newNodes.add(new Node(x, y, Math.PI));
                    newNodes.add(new Node(x, y, 5 * Math.PI / 4));
                    newNodes.add(new Node(x, y, 3 * Math.PI / 2));
                    newNodes.add(new Node(x, y, 7 * Math.PI / 4));

                    nodes.put(new Point(x, y), newNodes);
                    nodesCounter += 8;
                }
            }
        }

        class MyThread extends Thread {
            private double radius;
            private int first;
            private int last;

            public MyThread(double radius, int first, int last)
            {
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
            int n = Runtime.getRuntime().availableProcessors();
            //System.out.println("availableProcessors = " + n);
            int size = getNodesInList().size();
            MyThread[] t = new MyThread[n];
            for (int i = 0; i < n; i++) {
                t[i] = new MyThread(radius, (i * size) / n, ((i + 1) * size) / n);
                t[i].start();
            }
            for (int i = 0; i < n; i++) {
                t[i].join();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        isChanged = true;
        return nodesCounter;
    }

    // ��������� 24 ������� � ���� n
    public void addLinksAroundCell24(Node n, double radius, boolean doubleConnections) {
        int yBorder = (int) n.getY() + scale * 2;
        int xBorder = (int) n.getX() + scale * 2;
        synchronized (n.getLinks()) {
            for (int yi = (int) n.getY() - scale * 2; yi <= yBorder; yi += scale) {
                for (int xi = (int) n.getX() - scale * 2; xi <= xBorder; xi += scale) {
                    Point cellCenter = getCellCenterPoint(xi, yi);
                    if (cellCenter != null) {
                        ArrayList<Node> neighbours = getNodes(cellCenter.x, cellCenter.y, 0);
                        for (Node ni : neighbours) {
                            n.addNeighbor(ni, radius, scale, passabilityArray);
                            if (doubleConnections) {
                                ni.addNeighbor(n, radius, scale, passabilityArray);
                            }
                        }
                    }
                }
            }
        }
        isChanged = true;
    }

    public List<Node> getNodesByChild24(Node n) {
        int xBorder = (int) n.getX() + scale;
        int yBorder = (int) n.getY() + scale;
        Point nPoint = getCellCenterPoint((int) n.getX(), (int) n.getY());
        List<Node> result = new ArrayList<>();

        for (int yi = (int) n.getY() - scale; yi <= yBorder; yi += scale) {
            for (int xi = (int) n.getX() - scale; xi <= xBorder; xi += scale) {
                Point cellCenter = getCellCenterPoint(xi, yi);
                if (cellCenter != null) {
                    if (!cellCenter.equals(nPoint)) {
                        ArrayList<Node> neighbours = getNodes(cellCenter.x, cellCenter.y, 0);
                        result.addAll(neighbours.stream().filter(ni -> ni.isNeighbor(n)).collect(Collectors.toList()));
                    }
                }
            }
        }

        xBorder = (int) n.getX() + scale * 2;
        yBorder = (int) n.getY() + scale * 2;
        int yLowBorder = (int) n.getY() - scale * 2;
        for (int yi = yLowBorder; yi <= yBorder; yi += scale) {
            for (int xi = (int) n.getX() - scale * 2; xi <= xBorder;
                    xi = yi != yLowBorder && yi != yBorder ? xi + scale : xi + 3 * scale) {
                Point cellCenter = getCellCenterPoint(xi, yi);
                if (cellCenter != null) {
                    ArrayList<Node> neighbours = getNodes(cellCenter.x, cellCenter.y, 0);
                    result.addAll(neighbours.stream().filter(ni -> ni.isNeighbor(n)).collect(Collectors.toList()));
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

    public ArrayList<Node> getNodes(int x, int y, int diameter) {
        //http://www.skipy.ru/technics/synchronization.html
        //public List<Node> getNodes(int x, int y, int diameter) {
        //http://www.skipy.ru/technics/synchronization.html
        ArrayList<Node> result = new ArrayList<>();
        //List<Node> result=new Vector<Node>();

        //http://howtodoinjava.com/2013/03/26/performance-comparison-of-different-for-loops-in-java/
        if (diameter == 0) {
            Point point = new Point(x, y);
            if (nodes.containsKey(point)) {
                result.addAll(nodes.get(point));
            }
        } else {
            for (int i = (-1) * diameter; i <= diameter; ++i) {
                for (int j = (-1) * diameter; j <= diameter; ++j) {
                    Point point = new Point(x + i, y + j);
                    if (nodes.containsKey(point)) {
                        result.addAll(nodes.get(point));
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
                List<Node> n = new ArrayList<>();
                //http://www.skipy.ru/technics/synchronization.html
                n.add(node);
                nodes.put(node.getPoint(), n);
            }
            isChanged = true;
        }
    }

    public void addNodes(int x, int y) {
        List<Node> newNodes = new ArrayList<>();
        //http://www.skipy.ru/technics/synchronization.html
        //List<Node> newNodes=new Vector<Node>();

        for (double direction = 0; direction < 2 * Math.PI; direction += Math.PI / 4) {
            newNodes.add(new Node(x, y, direction));
        }
        Point point = new Point(x, y);
        if (nodes.containsKey(point)) {
            nodes.get(new Point(x, y)).addAll(newNodes);
        } else {
            nodes.put(point, newNodes);
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

    public synchronized boolean removeNode(Node n) {
        if (n == null) {
            return false;
        }
        Point point = n.getPoint();
        if (nodes.containsKey(point)) {
            int yBorder = (int) n.getY() + scale * 2;
            int xBorder = (int) n.getX() + scale * 2;

            for (int yi = (int) n.getY() - scale * 2; yi <= yBorder; yi += scale) {
                for (int xi = (int) n.getX() - scale * 2; xi <= xBorder; xi += scale) {
                    Point cellCenter = getCellCenterPoint(xi, yi);
                    if (cellCenter != null) {
                        ArrayList<Node> neighbours = getNodes(cellCenter.x, cellCenter.y, 0);
                        for (Node ni : neighbours) {
                            ni.removeNeighbor(n);
                        }
                    }
                }
            }

            n.getLinks().clear();
            nodes.get(point).remove(n);
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
        this.realityMap = realityMap;
        if (realityMap == null) {
            realityArray = null;
        } else {
            realityArray = new byte[realityMap.getWidth()][realityMap.getHeight()];
            for (int x = 0; x < realityMap.getWidth(); ++x) {
                for (int y = 0; y < realityMap.getHeight(); ++y) {
                    realityArray[x][y] = (byte) ((realityMap.getRGB(x, y) & 0xFF) - 128);
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

    public void setPassabilityPoint(int x, int y, int weight) {
        if (x < 0 || x >= passabilityMap.getWidth() ||  y < 0 || y >= passabilityMap.getHeight()) {
            return;
        }
        passabilityArray[x][y] = (byte) (127 - weight);
        //passabilityChanged=true;
    }

    public int getRealityWeight(int x, int y) {
        if (realityMap == null) {
            return 255;
        }
        if (x < 0 || x >= realityMap.getWidth() || y < 0 || y >= realityMap.getHeight()) {
            return 255;
        }
        return 127 - realityArray[x][y];
    }

    public int getPassabilityWeight(int x, int y) {
        if (passabilityMap == null) {
            return 255;
        }
        if (x < 0 || x >= passabilityMap.getWidth() || y < 0 || y >= passabilityMap.getHeight()) {
            return 255;
        }
        // first option is may be right, because I use it to take old passability, while changing passability array
        return 255 - (passabilityMap.getRGB(x, y) & 0xFF);
        //return 127-passabilityArray[x][y];
    }

    public Point getCellCenterPoint(int x, int y) {
        if (passabilityMap == null) {
            return null;
        }
        if (x < 0 || x >= passabilityMap.getWidth() || y < 0 || y >= passabilityMap.getHeight()) {
            return null;
        }
        return new Point(scale / 2 + scale * (x / scale), scale / 2 + scale * (y / scale));
    }

    // returns true when cell is passable
    // returns false otherwise
    public boolean calculatePassabilityForCell(int xCenter, int yCenter, double robotRadius) {
        if (xCenter - scale / 2 < 0 || xCenter + scale / 2 >= passabilityMap.getWidth() ||
                yCenter - scale / 2 < 0 || yCenter + scale / 2 >= passabilityMap.getHeight() ||
                passabilityMap == null) {
            return false;
        }

        int average = 0;
        boolean isBlack = false;
        int counter = 0;
        for (int x = xCenter - (scale / 2); x < xCenter + (scale / 2); ++x) {
            for (int y = yCenter - (scale / 2); y < yCenter + (scale / 2); ++y) {
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
        g2.fillRect(xCenter - (scale / 2), yCenter - (scale / 2), scale, scale);

        for (int x = xCenter - scale / 2; x < xCenter + scale / 2; ++x) {
            for (int y = yCenter - scale / 2; y < yCenter + scale / 2; ++y) {
                passabilityArray[x][y] = (byte) (average - 128);
            }
        }

        List<Node> nodesToChange = getNodes(xCenter, yCenter, 0);
        //passabilityChanged=true;
        if (average == 0) {
            nodesToChange.forEach(this::removeNode);
            return false;
        } else {
            if (nodesToChange.size() == 0) {
                addNodes(xCenter, yCenter);
                for (Node n : getNodes(xCenter, yCenter, 0)) {
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



//  public void setPassabilityChanged(boolean passabilityChanged) {this.passabilityChanged=passabilityChanged;}
//  public boolean isPassabilityChanged() {return passabilityChanged;}
}
