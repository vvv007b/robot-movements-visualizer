package com.robot.paths.linking;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

class Surface extends JPanel implements MouseListener, MouseMotionListener {
    public static final int SELECT_CAMERA_TOOL = 0;
    public static final int ADD_CAMERA_TOOL = 1;
    public static final int SELECT_INOUT_VECTOR = 2;
    public static final int MOVE_CAMERA = 3;

    private final PathsLinkingGui parentGui;

    private static BufferedImage mapLayer;
    private static BufferedImage trajectoriesLayer;
    private static BufferedImage camerasLayer;
    private static BufferedImage linksLayer;
    private int selectedTool;
    private Camera currentCamera;
    private InOutVector currentVector;
    private final int cameraSize;
    private final int vectorCircleSize;


    public Surface(PathsLinkingGui gui) {
        super();
        mapLayer = null;
        trajectoriesLayer = null;
        camerasLayer = null;
        linksLayer = null;
        currentCamera = null;
        currentVector = null;
        addMouseListener(this);
        addMouseMotionListener(this);
        parentGui = gui;
        vectorCircleSize = 20;
        cameraSize = 16;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        if (mapLayer != null) {
            g2d.drawImage(mapLayer, 0, 0, null);
            this.setPreferredSize(new Dimension(mapLayer.getWidth(), mapLayer.getHeight()));
        }
        if (camerasLayer != null) {
            updateCamerasLayer();
            g2d.drawImage(camerasLayer, 0, 0, null);
        }
        if (trajectoriesLayer != null) {
            g2d.drawImage(trajectoriesLayer, 0, 0, null);
        }
        if (linksLayer != null) {
            updateLinksLayer();
            g2d.drawImage(linksLayer, 0, 0, null);
        }
        g2d.dispose();
        parentGui.updateStatus();
    }

    private void updateLinksLayer() {
        Graphics2D g2d = linksLayer.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, linksLayer.getWidth(), linksLayer.getHeight());
        g2d.setComposite(AlphaComposite.Src);
        if (currentVector != null) {
            for (InOutVector vector : TrackingSystem.getInOutVectorsList()) {
                vector.drawVector(g2d, vector == currentVector, true);
            }
            for (InOutVector vector : currentVector.getOrientation() == 0 ?
                    currentVector.getPrev() : currentVector.getNext()) {
                vector.drawVector(g2d, true, false);
            }
        } else {
            for (InOutVector vector : TrackingSystem.getInOutVectorsList()) {
                vector.drawVector(g2d, false, false);
            }
        }
        g2d.dispose();
    }

    private void updateCamerasLayer() {
        Graphics2D g2d = camerasLayer.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, camerasLayer.getWidth(), camerasLayer.getHeight());
        g2d.setComposite(AlphaComposite.Src);
        g2d.setColor(Color.WHITE);
        for (Camera camera : TrackingSystem.getCameraList()) {
            if (camera != currentCamera) {
                g2d.setColor(Color.WHITE);
            } else {
                g2d.setColor(Color.RED);
            }
            g2d.fillOval(camera.getX() - cameraSize / 2, camera.getY() - cameraSize / 2, cameraSize, cameraSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(camera.getX() - cameraSize / 2, camera.getY() - cameraSize / 2, cameraSize, cameraSize);

            //Draw FOV
            int radius = camera.getRadius();
            int azimuth = camera.getAzimuth();
            int angle = camera.getAngle();
            Arc2D arc = new Arc2D.Double(0.0, 0.5, radius, radius, 0.0, 60.0, Arc2D.CHORD);

            arc.setArcByCenter(camera.getX(), camera.getY(), radius, azimuth - angle / 2, angle, Arc2D.OPEN);

            g2d.setComposite(AlphaComposite.Src);
            g2d.draw(arc);
            if (angle < 360) {
                g2d.drawLine(camera.getX(), camera.getY(), new Double(arc.getStartPoint().getX()).intValue(),
                        new Double(arc.getStartPoint().getY()).intValue());
                g2d.drawLine(camera.getX(), camera.getY(), new Double(arc.getEndPoint().getX()).intValue(),
                        new Double(arc.getEndPoint().getY()).intValue());
            }
        }
        g2d.dispose();
    }

    public static void changeMapLayer(Image image) {
        mapLayer = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        trajectoriesLayer = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        camerasLayer = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        linksLayer = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mapLayer.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

    }

    public static void changeTrajectoriesLayer(BufferedImage trajectories) {
        Graphics2D g2d = trajectoriesLayer.createGraphics();
        g2d.drawImage(trajectories, 0, 0, null);
        g2d.dispose();
    }

    public static BufferedImage getMapLayer() {
        return mapLayer;
    }

    public static BufferedImage getTrajectoriesLayer() {
        return trajectoriesLayer;
    }

    public void clearTrajectoriesLayer() {
        Graphics2D g2d = trajectoriesLayer.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, trajectoriesLayer.getWidth(), trajectoriesLayer.getHeight());
        g2d.dispose();
    }

    public void clearLinksLayer() {
        Graphics2D g2d = linksLayer.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, linksLayer.getWidth(), linksLayer.getHeight());
        g2d.dispose();
    }

    public void setSelectedTool(int selectedTool) {
        this.selectedTool = selectedTool;
    }

    public void setCurrentVectorNull() {
        this.currentVector = null;
    }

    private void moveCamera(Point center) {
        currentCamera.setCenter(center);
        currentCamera.redrawFov();
        parentGui.getCameraXLabel().setText("X: " + center.getX());
        parentGui.getCameraYLabel().setText("Y: " + center.getY());
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        long startTime = System.currentTimeMillis();
        double minDistance;
        switch (selectedTool) {
            case SELECT_CAMERA_TOOL:
                Camera currentCamera = null;
                minDistance = cameraSize / 2 + 1;
                for (Camera camera : TrackingSystem.getCameraList()) {
                    double distance = Math.sqrt(Math.pow(camera.getX() - event.getX(), 2) +
                            Math.pow(camera.getY() - event.getY(), 2));
                    if (distance < minDistance) {
                        minDistance = distance;
                        currentCamera = camera;
                    }
                }
                if (currentCamera != null) {
                    System.out.println("Camera " + currentCamera.getIndex() + " at " +
                            currentCamera.getX() + " " + currentCamera.getY() + " selected");
                    this.currentCamera = currentCamera;
                    parentGui.setCurrentCamera(currentCamera);
                }
                break;
            case ADD_CAMERA_TOOL:
                System.out.println("Add new camera");
                System.out.println(event.getX() + " " + event.getY());
                Camera newCamera = new Camera(new Point(event.getX(), event.getY()));
                TrackingSystem.addCamera(newCamera);
                Tracker tracker = new Tracker(newCamera);
                newCamera.setTracker(tracker);
                tracker.setDaemon(true);
                tracker.start();
                break;
            case SELECT_INOUT_VECTOR:
                InOutVector currentVector = null;
                minDistance = vectorCircleSize / 2 + 1;
                for (InOutVector inOutVector : TrackingSystem.getInOutVectorsList()) {
                    double distance = Math.sqrt(Math.pow(inOutVector.getX() - event.getX(), 2) +
                            Math.pow(inOutVector.getY() - event.getY(), 2));
                    if (distance < minDistance) {
                        minDistance = distance;
                        currentVector = inOutVector;
                    }
                }
                if (currentVector != null) {
                    System.out.println("Vector " + (int) currentVector.getX() + " " +
                            (int) currentVector.getY() + " selected");
                    this.currentVector = currentVector;
                    parentGui.inOutVectorNotification(currentVector);
                }
                break;
            case MOVE_CAMERA:
                moveCamera(new Point(event.getX(), event.getY()));
                break;
            default:
                System.out.println("Error while choosing tool");
                break;
        }
        repaint();
        System.out.println("Click event handling(ms): " + (System.currentTimeMillis() - startTime));
    }

    @Override
    public void mousePressed(MouseEvent event) {

    }

    @Override
    public void mouseReleased(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    @Override
    public void mouseExited(MouseEvent event) {

    }


    @Override
    public void mouseDragged(MouseEvent event) {
        if (selectedTool == MOVE_CAMERA) {
            moveCamera(new Point(event.getX(), event.getY()));

        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        parentGui.getMouseXLabel().setText("x:" + event.getX());
        parentGui.getMouseYLabel().setText("y:" + event.getY());
        parentGui.repaint();
    }
}
