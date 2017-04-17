package com.robot.paths.finding2;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/***
 * Created by bocharov_n on 04.10.16.
 */
public class Surface extends JPanel implements MouseListener, MouseMotionListener {

    public static final int PLACE_ROBOT_TOOL = 1;
    public static final int PLACE_FINISH_TOOL = 2;
    public static final int PLACE_FINISH_TO_ALL_TOOL = 3;


    private boolean showMap = true;
    private boolean showPassabilityMap = true;
    private boolean showRealityMap = true;

    private int selectedTool;

    private ArrayList<Robot> robots;
    private Robot currentRobot;
    private MapInfo mapInfo;

    //    private Image robotImage;
    private Image robotImageOriginal;
    //    private Image robotImageSelected;
    private Image robotImageSelectedOriginal;
    private Image finishDirectionImage;

    private boolean isRobotsRunning = false;

    public Surface() {
        super();
        addMouseMotionListener(this);
        addMouseListener(this);
        robots = new ArrayList<>();
//        robotImage = new ImageIcon(getClass().getResource("/robot.png")).getImage();
        robotImageOriginal = new ImageIcon(getClass().getResource("/robot.png")).getImage();
//        robotImageSelected = new ImageIcon(getClass().getResource("/robot_selected.png")).getImage();
        robotImageSelectedOriginal = new ImageIcon(getClass().getResource("/robot_selected.png")).getImage();
        finishDirectionImage = new ImageIcon(getClass().getResource("/arrow.png")).getImage();
        selectedTool = Surface.PLACE_ROBOT_TOOL;
        mapInfo = new MapInfo();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        if (mapInfo.getImageMap() != null & showMap) {
            g2d.drawImage(mapInfo.getImageMap(), 0, 0, null);
        }
        if (mapInfo.getPassabilityMap() != null & showPassabilityMap) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.drawImage(mapInfo.getPassabilityMap(), 0, 0, null);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        for (Robot robot : robots) {
            g2d.setColor(robot == currentRobot ? Color.GREEN : Color.YELLOW);
            Image robotImageToDraw = robot == currentRobot ? robot.getImageSelected() : robot.getImage();
            AffineTransform at = new AffineTransform();
            at.translate(robot.getPosition().x - robotImageToDraw.getWidth(null) / 2,
                    robot.getPosition().y - robotImageToDraw.getHeight(null) / 2);
            at.rotate((-1) * robot.getPosition().getAzimuth(),
                    robotImageToDraw.getWidth(null) / 2, robotImageToDraw.getHeight(null) / 2);
            g2d.drawImage(robotImageToDraw, at, null);
            at = new AffineTransform();
            g2d.drawOval((int) robot.getFinish().getX() - 5, (int) robot.getFinish().getY() - 5, 10, 10);
            at.translate(robot.getFinish().getX(), robot.getFinish().getY() - 5);
            at.rotate((-1) * robot.getFinish().getAzimuth(), 0, finishDirectionImage.getHeight(null) / 2);
            g2d.drawImage(finishDirectionImage, at, null);
        }
    }

    private void placeRobot(Point position) {
        if (!fixPosition(position) |
                mapInfo.getPointWeight(
                        position,
                        currentRobot.getSize(),
                        currentRobot.getPosition().getAzimuth()
                ) == 255) {
            return;
        }
//        currentRobot.setPosition(position);
        currentRobot.getPosition().setLocation(position);
    }

    private void placeFinish(Robot robot, Point position) {
        if (!fixPosition(position) |
                mapInfo.getPointWeight(
                        position,
                        currentRobot.getSize(),
                        currentRobot.getFinish().getAzimuth()
                ) == 255) {
            return;
        }
        robot.getFinish().setLocation(position);

    }

    private void placeFinishToAll(Point position) {
        if (!fixPosition(position)) {
            return;
        }
        robots.stream()
                .filter(robot -> mapInfo.getPointWeight(
                        position,
                        robot.getSize(),
                        robot.getFinish().getAzimuth()
                ) == 255)
                .forEach(robot -> placeFinish(robot, position));
    }

    private boolean fixPosition(Point position) {
//        if (mapInfo.getPointWeight(position, currentRobot.getSize(), azimuth) == 255) {
//            return false; //TODO: azimuth fix required
//        }
        int robotSize = mapInfo.getScale();
        if (position.x < robotSize / 2) {
            position.x = robotSize / 2;
        } else if (position.x > mapInfo.getImageMap().getWidth() - robotSize / 2) {
            position.x = mapInfo.getImageMap().getWidth() - robotSize / 2;
        }
        if (position.y < robotSize / 2) {
            position.y = robotSize / 2;
        } else if (position.y > mapInfo.getImageMap().getHeight() - robotSize / 2) {
            position.y = mapInfo.getImageMap().getHeight() - robotSize / 2;
        }
        return true;
    }

    @Override
    public void mouseClicked(MouseEvent event) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        clickHandler(event);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        clickHandler(event);
    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    @Override
    public void mouseExited(MouseEvent event) {

    }

    @Override
    public void mouseDragged(MouseEvent event) {
        clickHandler(event);
    }

    @Override
    public void mouseMoved(MouseEvent event) {

    }

    private void clickHandler(MouseEvent event) {
        Point position = new Point(event.getX(), event.getY());
        if (mapInfo.getImageMap() != null && currentRobot != null) {
            switch (selectedTool) {
                case PLACE_ROBOT_TOOL:
                    placeRobot(position);
                    break;
                case PLACE_FINISH_TOOL:
                    placeFinish(currentRobot, position);
                    break;
                case PLACE_FINISH_TO_ALL_TOOL:
                    placeFinishToAll(position);
                    break;
                default:
                    break;
            }
            repaint();
        }
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public void addRobot(Robot robot) {
        robots.add(robot);
    }

    public Robot getCurrentRobot() {
        return currentRobot;
    }

    public boolean setCurrentRobot(Robot curRobot) {
        this.currentRobot = curRobot;
        return true;
    }

    public boolean setCurrentRobot(int id) {
        if (id < 0 | id >= robots.size()) {
            return false;
        }
        this.currentRobot = robots.get(id);
        return true;
    }

    public void setSelectedTool(int selectedTool) {
        this.selectedTool = selectedTool;
    }

    public void setSamplingStep(int step) {
        mapInfo.setScale(step);
    }

    public boolean isRobotsRunning() {
        return isRobotsRunning;
    }

    public void setRobotsRunning(boolean robotsRunning) {
        isRobotsRunning = robotsRunning;
    }

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public void setShowMap(boolean showMap) {
        this.showMap = showMap;
    }

    public void setShowPassabilityMap(boolean showPassabilityMap) {
        this.showPassabilityMap = showPassabilityMap;
    }

    public void setShowRealityMap(boolean showRealityMap) {
        this.showRealityMap = showRealityMap;
    }

    public void setRobotRotationAngle(int robotRotationAngle) {
        mapInfo.setRotationAngle(robotRotationAngle);
    }

    public Image getRobotImageOriginal() {
        return robotImageOriginal;
    }

    public Image getRobotImageSelectedOriginal() {
        return robotImageSelectedOriginal;
    }
}