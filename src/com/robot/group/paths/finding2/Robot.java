package com.robot.group.paths.finding2;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/***
 * Created by bocharov_n on 04.10.16.
 */
public class Robot {
    private Node position;
    private Node finish;
    private int size;
    private double rotationAngle; //radians

    private Surface surface;

    private Image image;
    private Image imageSelected;


    public Robot(Surface surface) {
        this.surface = surface;
        position = new Node(new Point2D.Double(-1000, -1000), 0);
        finish = new Node(new Point2D.Double(-1000, -1000), 0);
        size = 60;
        resizeRobotImage();
        rotationAngle = 15;


    }


    private void resizeRobotImage() {
        BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(surface.getRobotImageOriginal(), 0, 0, size, size, null);
        image = resized;

        resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        g2d = resized.createGraphics();
        g2d.drawImage(surface.getRobotImageSelectedOriginal(), 0, 0, size, size, null);
        imageSelected = resized;
        g2d.dispose();
        surface.repaint();
    }

    public Node getPosition() {
        return position;
    }

    public void setPosition(Node position) {
        this.position = position;
    }

    public Node getFinish() {
        return finish;
    }

    public void setFinish(Node finish) {
        this.finish = finish;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        resizeRobotImage();
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public Image getImage() {
        return image;
    }

    public Image getImageSelected() {
        return imageSelected;
    }
}