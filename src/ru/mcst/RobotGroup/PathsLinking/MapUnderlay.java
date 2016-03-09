package ru.mcst.RobotGroup.PathsLinking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Created by bocharov_n on 02.03.16.
 */
public class MapUnderlay extends JPanel implements MouseListener, MouseMotionListener, KeyListener{

    private static BufferedImage mapImage, trajectoriesImage, camerasImage;

    public MapUnderlay(){
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        mapImage = null;
        trajectoriesImage = null;
        camerasImage = null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        if(mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, null);
            this.setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
        }
        if(trajectoriesImage != null){
            g2d.drawImage(trajectoriesImage,0,0,null);
        }
        if(camerasImage != null){
            g2d.drawImage(camerasImage, 0, 0, null);
        }
        g2d.dispose();
    }


    public static void changeMapImage(Image image){
        mapImage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        trajectoriesImage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        camerasImage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mapImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
    }

    public static void changeTrajectoriesImage(BufferedImage trajectories) {
//        trajectoriesImage = trajectories;
        Graphics2D g2d = trajectoriesImage.createGraphics();
        g2d.drawImage(trajectories, 0, 0, null);
        g2d.dispose();
    }

    public static void drawAllCameras(){
        Graphics2D g2d = camerasImage.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, camerasImage.getWidth(), camerasImage.getHeight());
        g2d.setComposite(AlphaComposite.Src);
        int size = 10;
        for(Camera curCamera:TrackingSystem.getCameraList()){
            g2d.fillOval(curCamera.getX() - size / 2, curCamera.getY() - size / 2, size, size);
            Dimension dimension = curCamera.getFOVLabel().getPreferredSize();
            g2d.drawImage(curCamera.getFOV(), curCamera.getX() - dimension.width / 2, curCamera.getY() - dimension.height / 2, null);
        }
    }

    public static BufferedImage getMapImage() {
        return mapImage;
    }

    public static BufferedImage getTrajectoriesImage() {
        return trajectoriesImage;
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

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

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
