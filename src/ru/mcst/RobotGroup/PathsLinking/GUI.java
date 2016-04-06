package ru.mcst.RobotGroup.PathsLinking;

import ru.mcst.RobotGroup.PathsFinding.Hypervisor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bocharov_n on 22.10.15.
 */
public class GUI extends JFrame{
    private JPanel rootPanel;
    private JTextField xTextField;
    private JTextField yTextField;
    private JTextField azimuthTextField;
    private JTextField rTextField;
    private JTextField angleTextField;
    private JButton removeCameraButton;
    private JLabel xLabel;
    private JLabel yLabel;
    private JScrollPane mapScrollPane;
    private JRadioButton selectCameraRadioButton;
    private JRadioButton addCameraRadioButton;
    private JButton linkTrajectoriesButton;
    private JButton clearTrajectoriesButton;
    private static MapUnderlay mapPanel;

    private static Camera currentCamera;
    private static boolean isCameraChanging;  //crutch for changing textFields while cur camera changing


    public GUI(){
        super();
        currentCamera   = null;
        isCameraChanging = false;
        createMyComponents();
        startMapListenerDaemon();
        setContentPane(rootPanel);
        setTitle("Paths linking");
        pack();
        setSize(1001, 720);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void startMapListenerDaemon(){
        Thread mapSizeListener = new Thread(){
            @Override
            public void run(){
                while(this.isAlive()) {
                    int[] mapSize = Hypervisor.getMapSize();
                    Image mapImage = Hypervisor.getMapImage();
                    if (mapSize != null && mapSize[0] != mapPanel.getPreferredSize().width &
                            mapSize[1] != mapPanel.getPreferredSize().height) {
                        mapPanel.setPreferredSize(new Dimension(mapSize[0], mapSize[1]));
                        if (mapImage != null){
                            MapUnderlay.changeMapLayer(mapImage);
                        }
                        mapPanel.setSize(mapPanel.getPreferredSize());
                        mapPanel.repaint();


                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mapSizeListener.setDaemon(true);
        mapSizeListener.start();
    }

    private void createUIComponents() {
        mapPanel = new MapUnderlay(this);
//        mapScrollPane.add(mapPanel);
        mapScrollPane=new JScrollPane(mapPanel);


    }

    private void createMyComponents(){

        mapPanel.setLayout(new BorderLayout());

        ButtonGroup cameraToolsGroup = new ButtonGroup();
        cameraToolsGroup.add(selectCameraRadioButton);
        cameraToolsGroup.add(addCameraRadioButton);

        xTextField.setEnabled(false);
        yTextField.setEnabled(false);
        azimuthTextField.setEnabled(false);
        rTextField.setEnabled(false);
        angleTextField.setEnabled(false);
        removeCameraButton.setEnabled(false);


        final DocumentListener cameraChangerDL = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
//                log("insert");
                if (!isCameraChanging) updateCurrentCamera();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                log("remove");
                if (!isCameraChanging) updateCurrentCamera();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                log("change");
                if (!isCameraChanging) updateCurrentCamera();
            }
        };

        xTextField.getDocument().addDocumentListener(cameraChangerDL);
        yTextField.getDocument().addDocumentListener(cameraChangerDL);
        angleTextField.getDocument().addDocumentListener(cameraChangerDL);
        rTextField.getDocument().addDocumentListener(cameraChangerDL);
        azimuthTextField.getDocument().addDocumentListener(cameraChangerDL);

        selectCameraRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setSelectedTool(mapPanel.SELECT_CAMERA_TOOL);
            }
        });
        addCameraRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setSelectedTool(mapPanel.ADD_CAMERA_TOOL);
            }
        });

        removeCameraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackingSystem.removeCamera(currentCamera);
                currentCamera.setExist(false);
                isCameraChanging = true;

                currentCamera = null;
                xTextField.setText("");
                yTextField.setText("");
                azimuthTextField.setText("");
                rTextField.setText("");
                angleTextField.setText("");
                xTextField.setEnabled(false);
                yTextField.setEnabled(false);
                rTextField.setEnabled(false);
                angleTextField.setEnabled(false);
                azimuthTextField.setEnabled(false);
                isCameraChanging = false;
                mapPanel.repaint();

                removeCameraButton.setEnabled(false);
            }
        });

        clearTrajectoriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Camera camera:TrackingSystem.getCameraList()){
                    camera.getTracker().clear();
                }
                TrackingSystem.getTrajectoriesList().clear();
                mapPanel.clearTrajectoriesLayer();
                mapPanel.clearLinksLayer();
            }
        });

        linkTrajectoriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackingSystem.linkTrajectories();
                ArrayList<RobotTrajectory> toDrawList = new ArrayList<RobotTrajectory>(TrackingSystem.getTrajectoriesList());
                while (!toDrawList.isEmpty()){
                    RobotTrajectory rt = toDrawList.get(0);
                    if (rt.getInVector() != null)
                        mapPanel.fillCircle((int)rt.getInVector().startPoint.getX(), (int)rt.getInVector().startPoint.getY(), rt.getConnectionsColor());
                    if(rt.getOutVector() != null)
                        mapPanel.fillCircle((int)rt.getOutVector().startPoint.getX(), (int)rt.getOutVector().startPoint.getY(), rt.getConnectionsColor());
                    for(RobotTrajectory connectedRT:rt.getConnectedTrajectories()){
                        if (connectedRT.getInVector() != null)
                            mapPanel.fillCircle((int)connectedRT.getInVector().startPoint.getX(), (int)connectedRT.getInVector().startPoint.getY(), rt.getConnectionsColor());
                        if(connectedRT.getOutVector() != null)
                            mapPanel.fillCircle((int)connectedRT.getOutVector().startPoint.getX(), (int)connectedRT.getOutVector().startPoint.getY(), rt.getConnectionsColor());
                        if (toDrawList.indexOf(connectedRT) >= 0)
                            toDrawList.remove(connectedRT);
                    }
                    if (toDrawList.indexOf(rt) >= 0)
                        toDrawList.remove(rt);
                }
            }
        });

        System.out.print("ui");
    }


    public void drawLine(StraightLine line){
        double a = line.getA();
        double r = line.getR();

        Point2D point1 = new Point2D.Double(0, r / Math.sin(Math.toRadians(a))),
                point2 = new Point2D.Double(r / Math.cos(Math.toRadians(a)), 0),
                point3 = new Point2D.Double(mapPanel.getWidth(), (r - mapPanel.getWidth() *
                        Math.cos(Math.toRadians(a))) / Math.sin(Math.toRadians(a))),
                point4 = new Point2D.Double((r - mapPanel.getHeight() * Math.sin(Math.toRadians(a))) /
                        Math.cos(Math.toRadians(a)), mapPanel.getHeight());
        Point2D firstPoint = null, secondPoint = null;
        if(point1.getY() >= 0) firstPoint = point1;
        else firstPoint = point2;
        if(point4.getX() >= 0 && point4.getY() >= 0) secondPoint = point4;
        if(point3.getX() >= 0 && point3.getY() >= 0) secondPoint = point3;
        if(point2.getX() >= 0 && point2.getY() >= 0) secondPoint = point2;
        BufferedImage lineImage = new BufferedImage(mapPanel.getWidth(), mapPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = lineImage.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0,0,lineImage.getWidth(),lineImage.getHeight());
        g2d.setComposite(AlphaComposite.Src);
        g2d.setColor(Color.RED);
        g2d.drawLine((int)firstPoint.getX(),(int)firstPoint.getY(),(int)secondPoint.getX(),(int)secondPoint.getY());
        g2d.dispose();
        JLabel lineLabel = new JLabel(new ImageIcon(lineImage));
        mapPanel.add(lineLabel);
        Insets insets = mapPanel.getInsets();
        Dimension size = lineLabel.getPreferredSize();
        lineLabel.setBounds(insets.left, insets.top, size.width, size.height);
    }

    public void updateCurrentCamera(){
        System.out.println("Updating camera");
        int     x       = currentCamera.getX(),
                y       = currentCamera.getY(),
                azimuth = currentCamera.getAzimuth(),
                r       = currentCamera.getR(),
                angle   = currentCamera.getAngle();
        try{
            int     newX        = Integer.parseInt(xTextField.getText()),
                    newY        = Integer.parseInt(yTextField.getText()),
                    newAzimuth  = Integer.parseInt(azimuthTextField.getText()),
                    newR        = Integer.parseInt(rTextField.getText()),
                    newAngle    = Integer.parseInt(angleTextField.getText());
            if (x != newX || y != newY || azimuth != newAzimuth || r != newR || angle != newAngle){
                x = newX < 0 ? 0 : newX > mapPanel.getWidth() ? mapPanel.getWidth() : newX;
                y = newY < 0 ? 0 : newY > mapPanel.getHeight() ? mapPanel.getHeight() : newY;
                azimuth = newAzimuth;
                r       = newR;
                angle   = newAngle;
                currentCamera.setX(x);
                currentCamera.setY(y);
                currentCamera.setAzimuth(azimuth);
                currentCamera.setR(r);
                currentCamera.setAngle(angle);
                currentCamera.redrawFOV();
                mapPanel.repaint();
                System.out.println("Image was redrawed. New xy "+x+" "+y);
            }
        }
        catch (IllegalArgumentException ex){
            System.out.println("Incorrect value(s) in camera parameters fields");
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static MapUnderlay getMapPanel() {
        return mapPanel;
    }

    public void setCurrentCamera(Camera camera) {
        GUI.currentCamera = camera;

        isCameraChanging = true;
        xTextField.setText(Integer.toString(camera.getX()));
        yTextField.setText(Integer.toString(camera.getY()));
        rTextField.setText(Integer.toString(camera.getR()));
        azimuthTextField.setText(Integer.toString(camera.getAzimuth()));
        angleTextField.setText(Integer.toString(camera.getAngle()));
        xTextField.setEnabled(true);
        yTextField.setEnabled(true);
        rTextField.setEnabled(true);
        azimuthTextField.setEnabled(true);
        angleTextField.setEnabled(true);
        removeCameraButton.setEnabled(true);
        isCameraChanging = false;
    }

    public JLabel getxLabel() {
        return xLabel;
    }

    public JLabel getyLabel() {
        return yLabel;
    }

    public JScrollPane getMapScrollPane() {
        return mapScrollPane;
    }
}
