package com.mcst.paths.finding;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class Surface extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private static final Logger log = Logger.getLogger(Surface.class.getName());
    public static final int STAGE_PLACE_ROBOT = 6;
    public static final int STAGE_SET_FINISH = 7;
    public static final int STAGE_SET_RECT_WEIGHT = 8;
    public static final int STAGE_SET_FINISH_ALL = 9;

    private int stage;

    private Robot robot;
    private boolean showMap = true;
    private boolean showPassability = true;
    private boolean showReality = true;
    private Image robotImage;
    private Image robotImageOriginal;
    private Image robotImageSelected;
    private Image robotImageSelectedOriginal;

    private final int nodeDiameter;

    private boolean showNodes = false;

    private BufferedImage screen;

    private Image finishDirectionImage;

    private int rectWeight = 0;

    private List<Robot> robots = new ArrayList<>();

    private boolean robotsRunning = false;
    private boolean logging = false;


    public Surface() {
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        stage = 0;
        robot = new Robot();
        robots.add(robot);
        robotImage = new ImageIcon(getClass().getResource("/robotR2D2.png")).getImage();
        robotImageOriginal = new ImageIcon(getClass().getResource("/robotR2D2.png")).getImage();
        robotImageSelected = new ImageIcon(getClass().getResource("/robotR2D2Selected.png")).getImage();
        robotImageSelectedOriginal = new ImageIcon(getClass().getResource("/robotR2D2Selected.png")).getImage();
        finishDirectionImage = new ImageIcon(getClass().getResource("/arrow.png")).getImage();

        screen = null;
        System.out.println("Surface created");
        Hypervisor hypervisor = new Hypervisor(robots);

        GlobalTime globalTime = new GlobalTime(this);
        globalTime.start();
        nodeDiameter = 10;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        doDrawing(graphics);
    }

    private void doDrawing(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        if (showMap) {
            g2d.drawImage(robot.getMap().getImage(), 0, 0, null);
        }
        if ((showMap ? 1 : 0) + (showPassability ? 1 : 0) + (showReality ? 1 : 0) > 1) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        if (showPassability) {
            g2d.drawImage(robot.getMap().getPassabilityMap(), 0, 0, null);
        }
        if (showReality) {
            g2d.drawImage(robot.getMap().getRealityMap(), 0, 0, null);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        if (showNodes) {
            if (robot.getMap().getIsChanged()) {
                robot.getMap().setIsChanged(false);
                refreshScreen();
            }
            g2d.drawImage(screen, 0, 0, null);
        }

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.cyan);
//        if(robot.hasPath() && !robot.isMapChangedSignal() && robot.getSpeed()!=0)
//        {
//            Node from=robot.getSearchAlgorithm().getPath().get(0);
//            for(int i=1; i<robot.getSearchAlgorithm().getPath().size(); ++i)
//            {
//                Node to=robot.getSearchAlgorithm().getPath().get(i);
//                Link link=from.getLinkByChild(to);
//                if(link!=null) {
//                    for(Segment s: link.getSegments()) {
//                        if(s.getIsStraightLine()){
//                            g2d.drawLine((int)s.getOriginX(), (int)s.getOriginY(),
// (int)(s.getOriginX()+s.getLength()*CurvesMath.cos(s.getStartAngle())),
// (int)(s.getOriginY()-s.getLength()*CurvesMath.sin(s.getStartAngle())));
//                        } else {
//                            int degreesTotal=(int)CurvesMath.toDegrees(s.getRadiansTotal());
//                            if(s.getIsClockwise())
//                                degreesTotal*=-1;
//                            g2d.drawArc((int)(s.getOriginX()-s.getRadius()),
// (int)(s.getOriginY()-s.getRadius()), (int)s.getRadius()*2, (int)s.getRadius()*2,
// (int)CurvesMath.toDegrees(s.getStartAngle()), degreesTotal);
//                        }
//                    }
//                }
//                from=to;
//            }
//        }
        for (Robot curRobot : robots) {
            if (curRobot.hasPath() && !curRobot.isMapChangedSignal() && curRobot.getSpeed() != 0) {
                Node from = curRobot.getSearchAlgorithm().getPath().get(0);
                for (int i = 1; i < curRobot.getSearchAlgorithm().getPath().size(); ++i) {
                    Node to = curRobot.getSearchAlgorithm().getPath().get(i);
                    Link link = from.getLinkByChild(to);
                    if (link != null) {
                        for (Segment segment : link.getSegments()) {
                            if (segment.getIsStraightLine()) {
                                g2d.drawLine((int) segment.getOriginX(), (int) segment.getOriginY(),
                                        (int) (segment.getOriginX() + segment.getLength() *
                                                Math.cos(segment.getStartAngle())),
                                        (int) (segment.getOriginY() - segment.getLength() *
                                                Math.sin(segment.getStartAngle())));
                            } else {
                                int degreesTotal = (int) Math.toDegrees(segment.getRadiansTotal());
                                if (segment.getIsClockwise()) {
                                    degreesTotal *= -1;
                                }
                                g2d.drawArc((int) (segment.getOriginX() - segment.getRadius()),
                                        (int) (segment.getOriginY() - segment.getRadius()),
                                        (int) segment.getRadius() * 2, (int) segment.getRadius() * 2,
                                        (int) Math.toDegrees(segment.getStartAngle()), degreesTotal);
                            }
                        }
                    }
                    from = to;
                }
            }
        }
//        AffineTransform at = new AffineTransform();
//        g2d.setColor(Color.green);
//        g2d.drawOval((int) finish.getX() - 5, (int) finish.getY() - 5, 10, 10);
//        at.translate(finish.getX(), finish.getY() - 5);
//        at.rotate((-1) * finish.getDirection(), 0, finishDirectionImage.getHeight(null) / 2);
//        g2d.drawImage(finishDirectionImage, at, null);

//        at = new AffineTransform();
//        at.translate((int)robot.getX()-robotImage.getWidth(null)/2, (int)robot.getY()-robotImage.getHeight(null)/2);
//        at.rotate((-1)*robot.getAzimuth(), robotImage.getWidth(null)/2, robotImage.getHeight(null)/2);
//        g2d.drawImage(robotImage, at, null);


        for (Robot r : robots) {
            AffineTransform at = new AffineTransform();
            if (r == robot) {
                g2d.setColor(Color.green);
            } else {
                g2d.setColor(Color.orange);
            }
            Node robotFinish = r.getFinish();
            g2d.drawOval((int) robotFinish.getX() - 5, (int) robotFinish.getY() - 5, 10, 10);
            at.translate(robotFinish.getX(), robotFinish.getY() - 5);
            at.rotate((-1) * robotFinish.getDirection(), 0, finishDirectionImage.getHeight(null) / 2);
            g2d.drawImage(finishDirectionImage, at, null);

            at = new AffineTransform();
            if (r != robot) {
                at.translate((int) r.getX() - robotImage.getWidth(null) / 2,
                        (int) r.getY() - robotImage.getHeight(null) / 2);
                at.rotate((-1) * r.getAzimuth(), robotImage.getWidth(null) / 2, robotImage.getHeight(null) / 2);
                g2d.drawImage(robotImage, at, null);
            } else {
                at.translate((int) r.getX() - robotImageSelected.getWidth(null) / 2,
                        (int) r.getY() - robotImageSelected.getHeight(null) / 2);
                at.rotate((-1) * r.getAzimuth(), robotImageSelected.getWidth(null) / 2,
                        robotImageSelected.getHeight(null) / 2);
                g2d.drawImage(robotImageSelected, at, null);
            }
        }
    }

    public void refreshScreen() {
        if (robot.getMap().getImage() != null) {
            screen = new BufferedImage(robot.getMap().getImage().getWidth(null),
                    robot.getMap().getImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);
        } else {
            screen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = screen.createGraphics();

        g2d.setColor(Color.blue);
        g2d.setStroke(new BasicStroke(1));

        List<Node> nodes = robot.getMap().getNodesInList();
        for (Node n : nodes) {
            g2d.drawOval((int) n.getX() - 5, (int) n.getY() - 5, nodeDiameter, nodeDiameter);
            for (int i = 0; i < n.getLinks().size(); ++i) {
                Link link = n.getLinks().get(i);
                g2d.drawLine((int) n.getX(), (int) n.getY(), (int) link.getChild().getX(),
                        (int) link.getChild().getY());
            }
        }

    }

    private void clickEvent(MouseEvent event) {
        if (robotsRunning) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(event)) {
            switch (stage) {
                case STAGE_PLACE_ROBOT:
                    placeRobot(new Point(event.getX(), event.getY()));
                    break;
                case STAGE_SET_FINISH:
                    setFinish(new Point(event.getX(), event.getY()));
                    break;
                case STAGE_SET_RECT_WEIGHT:
                    changeRectangle(new Point(event.getX(), event.getY()));
                    break;
                case STAGE_SET_FINISH_ALL:
                    setFinishAll(new Point(event.getX(), event.getY()));
                    break;
                default:
                    break;
            }
        }
    }

    public void mouseClicked(MouseEvent event) {
        clickEvent(event);
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
        requestFocus();
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        clickEvent(event);
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
    }

    private void placeRobot(Point position) {
        int scale = robot.getMap().getScale();
        fixPosition(position);
        // check if robot can stand there
        if (Link.getPointWeight(position, robot.getAzimuth(), scale, robot.getMap().getPassabilityArray()) == 255) {
            return;
        }
        robot.setPosition(position);
        //Control_Panel.setRobotCoordinates(x, y);
        robot.setMapChangedSignal(true);
        repaint();
    }

//    public int placeRobotSilent(int x, int y){
//        int scale = robot.getMap().getScale();
//        if (robot.getMap().getImage() != null) {
//            if (x < scale / 2)
//                x = scale / 2;
//            else if (x > robot.getMap().getWidth() - scale / 2)
//                x = robot.getMap().getWidth() - scale / 2;
//            if (y < scale / 2)
//                y = scale / 2;
//            else if (y > robot.getMap().getHeight() - scale / 2)
//                y = robot.getMap().getHeight() - scale / 2;
//        }
//        // check if robot can stand there
//        if (Link.getPointWeight(new Point(x, y), robot.getAzimuth(), scale, robot.getMap().getPassabilityArray())
// == 255)
//            return 0;
//        robot.setX(x);
//        robot.setY(y);
//        //Control_Panel.setRobotCoordinates(x, y);
//        robot.setMapChangedSignal(true);
//        return 1;
//    }

//    private void setFinish(Point position) {
//        int scale = robot.getMap().getScale();
//        fixPosition(position);
//        for (Robot r : robots) {
//            Node finish = r.getFinish();
//            // check if robot can stand there
//            if (Link.getPointWeight(position, finish.getDirection(), scale, r.getMap().getPassabilityArray()) == 255)
//                return;
//            finish.setX(position.x);
//            finish.setY(position.y);
//
//            if (r.getSpeed() != 0) {
//                //synchronized (robot.getMap().getFinish()) {
//                synchronized (r.getMap().getNodes()) {
//                    Node n = new Node(finish.getX(), finish.getY(), finish.getDirection());
//                    n.setIsRobotMade(true);
//                    r.getMap().addNode(n);
//                    r.getMap().addLinksAroundCell24(n, r.getRadius(), true);
//                    r.addNodeToDelete(r.getFinish());
//                    r.setFinish(n);
//                }
//            }
//            robot.setMapChangedSignal(true);
//        }
//        repaint();
//    }

    private void fixPosition(Point position) {
        int scale = robot.getMap().getScale();
        if (robot.getMap().getImage() != null) {
            if (position.x < scale / 2) {
                position.x = scale / 2;
            } else if (position.x > robot.getMap().getWidth() - scale / 2) {
                position.x = robot.getMap().getWidth() - scale / 2;
            }
            if (position.y < scale / 2) {
                position.y = scale / 2;
            } else if (position.y > robot.getMap().getHeight() - scale / 2) {
                position.y = robot.getMap().getHeight() - scale / 2;
            }
        }
    }

    private void setFinish(Point position) {
        fixPosition(position);
        setFinishToRobot(this.robot, position);
        robot.setMapChangedSignal(true);
        //Control_Panel.setFinishCoordinates(x, y);
        //robot.setMapChangedSignal(true);
        repaint();
    }


    private void setFinishAll(Point position) {
        fixPosition(position);
        for (Robot r : robots) {
            setFinishToRobot(r, position);
            robot.setMapChangedSignal(true);
        }
        repaint();
    }

    private void setFinishToRobot(Robot robot, Point position) {
        int scale = robot.getMap().getScale();
        Node finish = robot.getFinish();
        // check if robot can stand there
        if (Link.getPointWeight(position, finish.getDirection(), scale, robot.getMap().getPassabilityArray()) == 255) {
            return;
        }
//        finish.setX(position.x);
//        finish.setY(position.y);
        finish.setPosition(new Point2D.Double(position.x, position.y));

        if (robot.getSpeed() != 0) {
            //synchronized (robot.getMap().getFinish()) {
            synchronized (robot.getMap().getNodes()) {
                Node node = new Node(new Point2D.Double(finish.getX(), finish.getY()), finish.getDirection());
                node.setIsRobotMade(true);
                robot.getMap().addNode(node);
                robot.getMap().addLinksAroundCell24(node, robot.getRadius(), true);
                robot.addNodeToDelete(robot.getFinish());
                robot.setFinish(node);
            }
        }
    }


//    public int setFinishSilent(int x, int y){
//        int scale = robot.getMap().getScale();
//        if (robot.getMap().getImage() != null) {
//            if (x < scale / 2)
//                x = scale / 2;
//            else if (x > robot.getMap().getWidth() - scale / 2)
//                x = robot.getMap().getWidth() - scale / 2;
//            if (y < scale / 2)
//                y = scale / 2;
//            else if (y > robot.getMap().getHeight() - scale / 2)
//                y = robot.getMap().getHeight() - scale / 2;
//        }
//        Node finish = robot.getFinish();
//        // check if robot can stand there
//        if (Link.getPointWeight(new Point(x, y), finish.getDirection(),
// scale, robot.getMap().getPassabilityArray()) == 255)
//            return 0;
//        finish.setX(x);
//        finish.setY(y);
//
//        if (robot.getSpeed() != 0) {
//            //synchronized (robot.getMap().getFinish()) {
//            synchronized (robot.getMap().getNodes()) {
//                Node n = new Node(finish.getX(), finish.getY(), finish.getDirection());
//                n.setIsRobotMade(true);
//                robot.getMap().addNode(n);
//                robot.getMap().addLinksAroundCell24(n, robot.getRadius(), true);
//                robot.addNodeToDelete(robot.getFinish());
//                robot.setFinish(n);
//            }
//        }
//        robot.setMapChangedSignal(true);
//        return 1;
//    }

//    public void render() {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                repaint();  // repaint(), etc. according to changed states
//            }
//        });
//    }

    private void resizeRobot(int size) {
        BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(robotImageOriginal, 0, 0, size, size, null);
        robotImage = resized;

        resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        g2d = resized.createGraphics();
        g2d.drawImage(robotImageSelectedOriginal, 0, 0, size, size, null);
        robotImageSelected = resized;
        g2d.dispose();
    }

    public void setScale(int scale) {
        resizeRobot(scale);
        robot.getMap().setScale(scale);
        repaint();
    }

    public void setStage(int stage) {
        this.stage = stage;
        repaint();
    }

    public void runRobot() {
        long time = System.currentTimeMillis();
        long searchTime = robot.move();
        JOptionPane.showMessageDialog(this, "Robot stopped after " + (System.currentTimeMillis() - time) +
                " ms\nFirst AStar search lasted " + searchTime + " ms");
    }

//    public void stopRobot() {
//        robot.setStopSignal(true);
//    }

    public void stopRobots() {
        for (Robot r : robots) {
            r.setStopSignal(true);
        }
    }

    public Robot getRobot() {
        return robot;
    }

    public void removeRobots() {
        for (Robot r : robots) {
            r.setStanding(null);
            robot.setPosition(new Point(Robot.ROBOT_NOWHERE_X, Robot.ROBOT_NOWHERE_Y));
            r.setMapChangedSignal(true);
        }
    }


    private void removeFinishes() {
        for (Robot r : robots) {
            r.getFinish().setPosition(new Point2D.Double(Robot.ROBOT_NOWHERE_X, Robot.ROBOT_NOWHERE_Y));
            r.setMapChangedSignal(true);
        }
    }

//    public void removeAllNodes() {
//        robot.getMap().getNodes().clear();
//        removeRobots();
//        removeFinishes();
//        refreshScreen();
//        repaint();
//    }

    public void setShowMap(boolean showMap) {
        this.showMap = showMap;
    }

    public void setShowPassability(boolean showPassability) {
        this.showPassability = showPassability;
    }

    public void setShowReality(boolean showReality) {
        this.showReality = showReality;
    }

    public void setShowNodes(boolean showNodes) {
        this.showNodes = showNodes;
    }

    public void setRobotsSensorsRange(int sensorsRange) {
        for (Robot r : robots) {
            r.setSensorsRange(sensorsRange);
        }
    }

//    public void setRobotsSpeed(int speed) {
//        for (Robot r : robots) {
//            r.setSpeed(speed);
//        }
//    }

    public void setRealityMaps(BufferedImage realityMap) {
        for (Robot r : robots) {
            r.getMap().setRealityMap(realityMap);
        }
    }

    public void setMapImages(BufferedImage image) {
        for (Robot r : robots) {
            r.getMap().setImage(image);
            removePassabilities();
        }
    }

    public void removePassabilities() {
        for (Robot r : robots) {
            r.getMap().removePassability();
        }
        removeRobots();
        removeFinishes();
    }

    public void setRobotsMinSpeed(int minSpeed) {
        for (Robot r : robots) {
            r.setMinSpeed(minSpeed);
        }
    }

    public void setRobotsMaxSpeed(int maxSpeed) {
        for (Robot r : robots) {
            r.setMaxSpeed(maxSpeed);
        }
    }

    public double getRobotSpeed() {
        return robot.getSpeed();
    }

    public void setRobotsAcceleration(int acceleration) {
        for (Robot r : robots) {
            r.setAcceleration(acceleration);
        }
    }

    public void setRobotsDeceleration(int deceleration) {
        for (Robot r : robots) {
            r.setDeceleration(deceleration);
        }
    }

    public void setRobotAzimuth(double azimuth) {
        if (Link.getPointWeight(new Point((int) robot.getX(), (int) robot.getY()),
                azimuth, robot.getMap().getScale(), robot.getMap().getPassabilityArray()) == 255) {
            return;
        }
        robot.setAzimuth(azimuth);
        robot.setMapChangedSignal(true);
        repaint();
    }

    public void setFinishDirection(double direction) {
        if (stage != STAGE_SET_FINISH_ALL) {
            Node finish = robot.getFinish();
            if (Link.getPointWeight(new Point((int) finish.getX(), (int) finish.getY()), direction,
                    robot.getMap().getScale(), robot.getMap().getPassabilityArray()) == 255) {
                return;
            }
            finish.setDirection(direction);
            robot.setMapChangedSignal(true);
        } else {
            for (Robot r : robots) {
                Node finish = r.getFinish();
                if (Link.getPointWeight(new Point((int) finish.getX(), (int) finish.getY()),
                        direction, r.getMap().getScale(), r.getMap().getPassabilityArray()) == 255) {
                    return;
                }
                finish.setDirection(direction);
                r.setMapChangedSignal(true);
            }
        }
        repaint();
    }

    public double getFinishDirection() {
        return robot.getFinish().getDirection();
    }

    public void setRobotsRadius(double radius) {
        for (Robot r : robots) {
            r.setRadius(radius);
        }
    }

    public void refreshGraph() {
        robot.getMap().refreshGraph();
        for (Robot r : robots) {
            r.setMapChangedSignal(true);
        }
    }

    public void setRectWeight(int rectWeight) {
        this.rectWeight = rectWeight;
    }

    private void changeRectangle(Point position) {
        if (robot.getMap().getPassabilityMap() == null) {
            return;
        }
        Point centerPoint = robot.getMap().getCellCenterPoint(position);
        if (centerPoint == null) {
            return;
        }
        int scale = robot.getMap().getScale();
        for (int yi = centerPoint.y - scale / 2, borderY = centerPoint.y + scale / 2; yi < borderY; ++yi) {
            for (int xi = centerPoint.x - scale / 2, borderX = centerPoint.x + scale / 2; xi < borderX; ++xi) {
                robot.getMap().setPassabilityPoint(new Point(xi, yi), rectWeight);
            }
        }
        robot.getMap().calculatePassabilityForCell(centerPoint, robot.getRadius());

        for (Robot r : robots) {
            r.setMapChangedSignal(true);
        }
        repaint();
    }

    public void calculatePassability(double robotRadius) {
        final long time = System.currentTimeMillis();
        final int nodesCount = robot.getMap().calculatePassability(robotRadius);

        removeRobots();
        removeFinishes();
        refreshScreen();
        repaint();

        JOptionPane.showMessageDialog(this, "Map generation completed in " +
                (System.currentTimeMillis() - time) + " ms\nThere are " + nodesCount + " nodes");
    }

//    public void calculatePassabilitySilent(double robotRadius) {
//        long time = System.currentTimeMillis();
//        int nodesCount = robot.getMap().calculatePassability(robotRadius);
//        System.out.println("Map generation completed in " + (System.currentTimeMillis() - time) +
//                " ms\nThere are " + nodesCount + " nodes");
//    }

    public void removeMap() {
        setMapImages(null);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (robotsRunning) {
            return;
        }
        Node finish = robot.getFinish();
        switch (arg0.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (stage == 6) {
                    if (robot.getX() != -1000 && robot.getY() != -1000) {
                        placeRobot(new Point((int) robot.getX(), (int) robot.getY() - 1));
                    }
                } else if (stage == 7 && finish.getX() != -1000 && finish.getY() != -1000) {
                    setFinish(new Point((int) finish.getX(), (int) finish.getY() - 1));
                }
                break;
            case KeyEvent.VK_DOWN:
                if (stage == 6) {
                    if (robot.getX() != -1000 && robot.getY() != -1000) {
                        placeRobot(new Point((int) robot.getX(), (int) robot.getY() + 1));
                    }
                } else if (stage == 7 && finish.getX() != -1000 && finish.getY() != -1000) {
                    setFinish(new Point((int) finish.getX(), (int) finish.getY() + 1));
                }
                break;
            case KeyEvent.VK_LEFT:
                if (stage == 6) {
                    if (robot.getX() != -1000 && robot.getY() != -1000) {
                        placeRobot(new Point((int) robot.getX() - 1, (int) robot.getY()));
                    }
                } else if (stage == 7 && finish.getX() != -1000 && finish.getY() != -1000) {
                    setFinish(new Point((int) finish.getX() - 1, (int) finish.getY()));
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (stage == 6) {
                    if (robot.getX() != -1000 && robot.getY() != -1000) {
                        placeRobot(new Point((int) robot.getX() + 1, (int) robot.getY()));
                    }
                } else if (stage == 7 && finish.getX() != -1000 && finish.getY() != -1000) {
                    setFinish(new Point((int) finish.getX() + 1, (int) finish.getY()));
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    public void addRobot(Robot robot) {
        robots.add(robot);
    }

    // returns index of selected robot
    public int removeRobot() {
        if (robots.size() < 2) {
            return 1;
        }
        int index = robots.indexOf(robot) - 1;
        if (index < 0) {
            index = 0;
        }
        robots.remove(robot);
        robot = robots.get(index);
        repaint();
        return index;
    }

    public void runRobots() {
        long startTime = System.nanoTime();
        robotsRunning = true;
        class MyThread extends Thread {
            private Robot robot;

            private MyThread(Robot robot) {
                this.robot = robot;
            }

            public void run() {
                robot.move();
            }
        }

        try {
            int size = robots.size();

            MyThread[] threads = new MyThread[size];
            for (int i = 0; i < size; i++) {
                threads[i] = new MyThread(robots.get(i));
                threads[i].start();
            }
            if (logging) {
                Hypervisor.startLog();
            }
            for (int i = 0; i < size; i++) {
                threads[i].join();
            }
            Hypervisor.stopLog();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        robotsRunning = false;
//        System.out.println();
        JOptionPane.showMessageDialog(this, "All robots have stopped. " +
                "Path finding time(ms): " + (System.nanoTime() - startTime) / 1_000_000);
    }

    public boolean selectRobot(int index) {
        if (index >= robots.size() || index < 0) {
            return false;
        }
        robot = robots.get(index);
        //finish=robot.getFinish();
        repaint();
        return true;
    }

    public int getRobotCount() {
        return robots.size();
    }

    public void setLogging(boolean selected) {
        logging = selected;
    }
}
