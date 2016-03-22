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
    private JButton addTrajectoryButton;
    private JTextField xKeyPointTextField;
    private JTextField yKeyPointTextField;
    private JTextField tKeyPointTextField;
    private JTextField vKeyPointTextField;
    private JLabel xLabel;
    private JLabel yLabel;
    private JLabel loadedPointsLabel;
    private JButton calculateVisibleButton;
    private JButton clearScreenButton;
    private JButton removeKeyPointButton;
    private JButton removeTrajectoryButton;
    private JButton calculateLinesButton;
    private JButton startButton;
    private JScrollPane mapScrollPane;
    private JRadioButton selectCameraRadioButton;
    private JRadioButton addCameraRadioButton;
    private JFileChooser fc = new JFileChooser();
    private static MapUnderlay mapPanel;


    private static Camera currentCamera;

    private KeyPoint currentKeyPoint;
    private static boolean isCameraChanging;  //crutch for changing textFields while cur camera changing
    private boolean isKeyPointChanging; //same
    private boolean isTrajectoryAdding;

    private int cameraWidth, cameraHeight;
    private int keyPointWidth, keyPointHeight;

    private Trajectory currentTrajectory;

    public GUI(){
        super();
        currentCamera   = null;
        currentKeyPoint = null;
        cameraHeight    = 20;
        cameraWidth     = 20;
        keyPointHeight  = 10;
        keyPointWidth   = 10;
//        isAddCameraButtonPressed = false;
        isCameraChanging = false;
        isKeyPointChanging = false;
        isTrajectoryAdding = false;
//        createUIComponents();
        createMyComponents();
        startMapListenerDaemon();
//        Tracker tracker = new Tracker();
//        tracker.setDaemon(true);
//        tracker.start();
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
                while(true) {
                    int[] mapSize = Hypervisor.getMapSize();
                    Image mapImage = Hypervisor.getMapImage();
                    if (mapSize != null && mapSize[0] != mapPanel.getPreferredSize().width &
                            mapSize[1] != mapPanel.getPreferredSize().height) {
                        mapPanel.setPreferredSize(new Dimension(mapSize[0], mapSize[1]));
                        if (mapImage != null){
                            MapUnderlay.changeMapLayer(mapImage);
                        }
                        mapPanel.repaint();

                    }
                    try {
                        this.sleep(1000);
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
//        mapPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        final Insets insets = mapPanel.getInsets();


        TrackingSystem.setWidth(mapPanel.getWidth());                       //TODO: remove this shit
        TrackingSystem.setHeight(mapPanel.getHeight());

        ButtonGroup cameraToolsGroup = new ButtonGroup();
        cameraToolsGroup.add(selectCameraRadioButton);
        cameraToolsGroup.add(addCameraRadioButton);



//        mapPanel.setBackground(Color.green);
        xTextField.setEnabled(false);
        yTextField.setEnabled(false);
        azimuthTextField.setEnabled(false);
        rTextField.setEnabled(false);
        angleTextField.setEnabled(false);
        removeCameraButton.setEnabled(false);

        xKeyPointTextField.setEnabled(false);
        yKeyPointTextField.setEnabled(false);
        tKeyPointTextField.setEnabled(false);
        vKeyPointTextField.setEnabled(false);


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

        DocumentListener KeyPointChangerDL = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isKeyPointChanging) updateCurrentKeyPoint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isKeyPointChanging) updateCurrentKeyPoint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isKeyPointChanging) updateCurrentKeyPoint();
            }
        };

        xKeyPointTextField.getDocument().addDocumentListener(KeyPointChangerDL);
        yKeyPointTextField.getDocument().addDocumentListener(KeyPointChangerDL);
        vKeyPointTextField.getDocument().addDocumentListener(KeyPointChangerDL);
        tKeyPointTextField.getDocument().addDocumentListener(KeyPointChangerDL);


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

        addTrajectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xKeyPointTextField.setEnabled(false);
                yKeyPointTextField.setEnabled(false);
                tKeyPointTextField.setEnabled(false);
                vKeyPointTextField.setEnabled(false);
                if (currentKeyPoint != null) {
                    currentKeyPoint.setBackground(Color.WHITE);
                    currentKeyPoint = null;
                }
                if(!isTrajectoryAdding) {
                    currentTrajectory = new Trajectory();
//                    isAddingCheckBox.setSelected(false);
                    for(Component c:rootPanel.getComponents()){
                        try{
                            JButton b = (JButton)c;
                            if(b != addTrajectoryButton)
                                b.setEnabled(false);
                        }
                        catch(ClassCastException ex){
                            //
                        }
                    }
                    isTrajectoryAdding = true;
                    addTrajectoryButton.setText("Press here to finish");
//                    isAddingCheckBox.setEnabled(false);
                    clearScreenButton.setEnabled(false);
                }
                else{
                    if(!currentTrajectory.getKeyPointList().isEmpty()){
                        currentTrajectory.generateConnections();
                        currentTrajectory.calculateTime();
                        JLabel connectionsLabel = currentTrajectory.getTrajectoryLabel();
                        mapPanel.add(connectionsLabel);
//                        mapPanel.setComponentZOrder(connectionsLabel, 1);
                        Dimension size = connectionsLabel.getPreferredSize();
                        connectionsLabel.setBounds(insets.left, insets.top, size.width, size.height);
                        for (KeyPoint kp: currentTrajectory.getKeyPointList()){
                            kp.setParentTrajectory(currentTrajectory);
                        }
                        TrackingSystem.addTrajectory(currentTrajectory);

                    }
                    else System.out.print("Empty trajectory");
                    isTrajectoryAdding = false;
                    addTrajectoryButton.setText("Add trajectory");
//                    isAddingCheckBox.setEnabled(true);
                    for(Component c:rootPanel.getComponents()){
                        try{
                            JButton b = (JButton)c;
                            if(b != addTrajectoryButton)
                                b.setEnabled(true);
                        }
                        catch(ClassCastException ex){
                            //
                        }
                    }
                    clearScreenButton.setEnabled(true);
                }
            }
        });

        calculateVisibleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackingSystem.setWidth(mapPanel.getWidth());
                TrackingSystem.setHeight(mapPanel.getHeight());
                TrackingSystem.calculateVisible();
                mapPanel.repaint();
            }
        });

        clearScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentCamera = null;
                currentKeyPoint = null;
                currentTrajectory = null;
                TrackingSystem.getCameraList().clear();
                TrackingSystem.getTrajectoryList().clear();
                mapPanel.clearTrajectoriesLayer();
                mapPanel.repaint();
            }
        });

        removeKeyPointButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentKeyPoint!=null && currentTrajectory != null) {
                    currentTrajectory.getKeyPointList().remove(currentKeyPoint);
                    mapPanel.remove(currentKeyPoint);
                    currentKeyPoint = null;
                    currentTrajectory.generateConnections();
                    mapPanel.repaint();
                    currentTrajectory = null;
                }
                else log("null keypoint or trajectory");

            }
        });

        removeTrajectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentTrajectory != null){
                    TrackingSystem.removeTrajectory(currentTrajectory);
                    for(KeyPoint keyPoint:currentTrajectory.getKeyPointList()){
                        mapPanel.remove(keyPoint);
                    }
                    mapPanel.remove(currentTrajectory.getTrajectoryLabel());
                    mapPanel.repaint();
                    currentKeyPoint = null;
                    currentKeyPoint = null;
                }
                else log("null keypoint or trajectory");
            }
        });

        calculateLinesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackingSystem.mergeVisible();
                double startR = 0,
                        endR = Math.sqrt(Math.pow(mapPanel.getWidth(), 2) + Math.pow(mapPanel.getHeight(), 2));
                TrackingSystem.setAccumulator(TrackingSystem.calculateLines(TrackingSystem.getVisiblePoints(), startR, endR));
                java.util.List<StraightLine> lines = TrackingSystem.findLocalMaximums(TrackingSystem.getAccumulator(), startR, endR);
                log(lines.size());
                for(StraightLine line: lines)
                    drawLine(line);
//                System.out.println("Points on corner");

                TrackingSystem.findInOutVectors();
            }
        });


        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });

        System.out.print("ui");
    }

    public void start(){
        for(Component c:rootPanel.getComponents()){
            try{
                JButton b = (JButton)c;
                b.setEnabled(true);
            }
            catch(ClassCastException ex){
                //
            }
        }

//        Tracki

        for(Component c:rootPanel.getComponents()){
            try{
                JButton b = (JButton)c;
                b.setEnabled(true);
            }
            catch(ClassCastException ex){
                //
            }
        }
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
        log("Updating camera");
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
                log("Image was redrawed. New xy "+x+" "+y);
            }
        }
        catch (IllegalArgumentException ex){
        }
    }

    private void updateCurrentKeyPoint() {
        log("updating keypoint");
        int     x = currentKeyPoint.getx(),
                y = currentKeyPoint.gety();
        double  v = currentKeyPoint.getV(),
                t = currentKeyPoint.getT();
        try{
            int     newX = Integer.parseInt(xKeyPointTextField.getText()),
                    newY = Integer.parseInt(yKeyPointTextField.getText());
            double  newV = Double.parseDouble(vKeyPointTextField.getText()),
                    newT = Double.parseDouble(tKeyPointTextField.getText());
            if (x != newX || y != newY || v != newV || t !=newT){
                x = newX < 0 ? 0 : newX > mapPanel.getWidth() ? mapPanel.getWidth() : newX;
                y = newY < 0 ? 0 : newY > mapPanel.getHeight() ? mapPanel.getHeight() : newY;
                v = newV;
                t = newT;

                currentKeyPoint.setx(x);
                currentKeyPoint.sety(y);
                currentKeyPoint.setV(v);
                currentKeyPoint.setT(t);

                Insets insets = mapPanel.getInsets();
                Dimension size = currentKeyPoint.getPreferredSize();
                currentKeyPoint.setBounds(x + insets.left - size.width / 2, y + insets.left - size.height / 2, size.width, size.height);

                Trajectory currentTrajectory = currentKeyPoint.getParentTrajectory();
                mapPanel.remove(currentTrajectory.getTrajectoryLabel());
                currentTrajectory.generateConnections();
                mapPanel.add(currentTrajectory.getTrajectoryLabel());
//                mapPanel.setComponentZOrder(currentTrajectory.getTrajectoryLabel(), 1);
                mapPanel.repaint();
            }
        }
        catch(IllegalArgumentException e){

        }

    }

//    public void mouseClickHandler(MouseEvent e){
//        int[] mapSize = Hypervisor.getMapSize();
//        if(mapSize ==null || mapSize != null && (e.getX() <= mapSize[0] & e.getY() <= mapSize[1]) ) {
//            if (isAddingCheckBox.isSelected()) {
////                addNewCameraToPanel(e.getX(), e.getY());
//            } else if (isTrajectoryAdding) {
//                KeyPoint newKeyPoint = new KeyPoint(e.getX(), e.getY(), 10);
//                newKeyPoint.setPreferredSize(new Dimension(keyPointWidth, keyPointHeight));
//                Insets insets = mapPanel.getInsets();
//                Dimension size = newKeyPoint.getPreferredSize();
//                mapPanel.add(newKeyPoint);
////            mapPanel.setComponentZOrder(newKeyPoint, 1);
//                currentTrajectory.addKeyPoint(newKeyPoint);
//                newKeyPoint.setBounds(e.getX() + insets.left - size.width / 2, e.getY() + insets.left - size.height / 2, size.width, size.height);
//                newKeyPoint.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        isKeyPointChanging = true;
//
//                        xKeyPointTextField.setEnabled(true);
//                        yKeyPointTextField.setEnabled(true);
//                        tKeyPointTextField.setEnabled(true);
//                        vKeyPointTextField.setEnabled(true);
//                        if (currentKeyPoint != null) currentKeyPoint.setBackground(Color.WHITE);
//                        currentKeyPoint = (KeyPoint) e.getSource();
//                        currentTrajectory = currentKeyPoint.getParentTrajectory();
//                        currentKeyPoint.setBackground(Color.RED);
//
//                        xKeyPointTextField.setText(Integer.toString(currentKeyPoint.getx()));
//                        yKeyPointTextField.setText(Integer.toString(currentKeyPoint.gety()));
//                        vKeyPointTextField.setText(Double.toString(round(currentKeyPoint.getV(), 2)));
//                        tKeyPointTextField.setText(Double.toString(round(currentKeyPoint.getT(), 2)));
//
//                        isKeyPointChanging = false;
//                    }
//                });
//            }
//        }
//        else{
//            System.out.println("Point out of range");
//        }
//    }

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



    public void setMapPanel(MapUnderlay mapPanel) {
        this.mapPanel = mapPanel;
    }

    public void log(String s){
        System.out.println(s);
    }
    public void log(int x){
        System.out.println(x);
    }
    public void log(Point2D p){System.out.println(p.getX()+" "+p.getY());}
    public void log(Double d) { System.out.println(d);}
}
