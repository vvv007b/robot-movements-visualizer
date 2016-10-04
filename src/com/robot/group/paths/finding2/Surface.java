package com.robot.group.paths.finding2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by bocharov_n on 04.10.16.
 */
public class Surface extends JPanel implements MouseListener, MouseMotionListener {

    public static final int PLACE_ROBOT_TOOL = 1;
    public static final int PLACE_FINIST_TOOL = 2;
    public static final int PLACE_FINISH_TO_ALL_TOOL = 3;

    private int selectedTool;

    private ArrayList<Robot> robots;
    private BufferedImage mapImage;
    private Robot curRobot;

    private Image robotImage;
    private Image robotImageOriginal;
    private Image robotImageSelected;
    private Image robotImageSelectedOriginal;
    private Image finishDirectionImage;

    private int robotSize;

    public Surface(){
        super();
        addMouseMotionListener(this);
        addMouseListener(this);
        robots = new ArrayList<>();
        robotImage = new ImageIcon(getClass().getResource("/robot.png")).getImage();
        robotImageOriginal = new ImageIcon(getClass().getResource("/robot.png")).getImage();
        robotImageSelected = new ImageIcon(getClass().getResource("/robot_selected.png")).getImage();
        robotImageSelectedOriginal = new ImageIcon(getClass().getResource("/robot_selected.png")).getImage();
        finishDirectionImage = new ImageIcon(getClass().getResource("/arrow.png")).getImage();
        selectedTool = Surface.PLACE_ROBOT_TOOL;
    }

    @Override
    public void paintComponent(Graphics graphics){
        if (mapImage != null) {
            graphics.drawImage(mapImage, 0, 0, null);
        }
        for (Robot robot:robots){
            graphics.drawImage(robotImage ,(int) robot.getPosition().x - robotImage.getWidth(null) / 2,
                    (int)robot.getPosition().y - robotImage.getHeight(null) / 2, null);
        }
    }

    private void placeRobot(Point position) {

        if (position.x < robotSize / 2) {
            position.x = robotSize / 2;
        } else if (position.x > mapImage.getWidth() - robotSize / 2){
            position.x = mapImage.getWidth() - robotSize / 2;
        }
        if (position.y < robotSize / 2) {
            position.y = robotSize / 2;
        } else if (position.y > mapImage.getHeight() - robotSize / 2){
            position.y = mapImage.getHeight() - robotSize / 2;
        }
        curRobot.setPosition(new Point2D.Double(position.getX(), position.getY()));
        repaint();
    }

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
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(mapImage != null && curRobot != null) {
            placeRobot(new Point(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mapImage != null && curRobot != null) {
            placeRobot(new Point(e.getX(), e.getY()));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public void addRobot(Robot robot){
        robots.add(robot);
    }

    public void setMapImage(BufferedImage mapImage) {
        this.mapImage = mapImage;
    }

    public void setCurRobot(Robot curRobot) {
        this.curRobot = curRobot;
    }

    public void setSelectedTool(int selectedTool) {
        this.selectedTool = selectedTool;
    }

    public int getRobotSize() {
        return robotSize;
    }

    public void setRobotSize(int robotSize) {
        this.robotSize = robotSize;
        resizeRobot(robotSize);
    }

}
