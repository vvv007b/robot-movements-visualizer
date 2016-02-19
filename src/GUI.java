import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

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
    private JPanel mapPanel;
    private JCheckBox isAddingCheckBox;
    private JButton removeCameraButton;
    private JButton addTrajectoryButton;
    private JTextField xKeyPointTextField;
    private JTextField yKeyPointTextField;
    private JTextField tKeyPointTextField;
    private JTextField vKeyPointTextField;
    //    private JButton calculatePointsButton;
    private JLabel xLabel;
    private JLabel yLabel;
    private JLabel mapLabel;
    private JLabel loadedPointsLabel;
    private JButton calculateVisibleButton;
    private JButton clearScreenButton;
    private JButton removeKeyPointButton;
    private JButton removeTrajectoryButton;
    private JButton calculateLinesButton;
    private JButton loadTrajectoriesButton;
    private JFileChooser fc = new JFileChooser();

    private Camera currentCamera;

    private KeyPoint currentKeyPoint;
    private boolean isCameraChanging;  //crutch for changing textFields while cur camera changing
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
        createUIComponents();
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createUIComponents() {
        mapPanel.setLayout(null);
        mapPanel.setSize(640,480);
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        final Insets insets = mapPanel.getInsets();

        BufferedImage map = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
//        BufferedImage map1 = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = map.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, map.getWidth(), map.getHeight());
        g2d.setComposite(AlphaComposite.Src);

        g2d.dispose();

        mapLabel = new JLabel();
        mapLabel.setIcon(new ImageIcon(map));
        addMapPicture();

        TrackingSystem.setWidth(mapPanel.getWidth());
        TrackingSystem.setHeight(mapPanel.getHeight());

        Dimension size = mapLabel.getPreferredSize();
        mapLabel.setBounds(insets.left, insets.top, size.width, size.height);
//        mapLabel.setOpaque(true);
        mapPanel.setBackground(Color.green);
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


        mapPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseClickHandler(e);
//                log(TrackingSystem.getCameraList().size());
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        mapPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                xLabel.setText("x:" + e.getX());
                yLabel.setText("y:" + e.getY());
            }
        });

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

        removeCameraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.remove(currentCamera);
                mapPanel.remove(currentCamera.getFOVLabel());
                mapPanel.remove(currentCamera.getVisibleImageLabel());
                TrackingSystem.removeCamera(currentCamera);

                isCameraChanging = true;

                currentCamera = null;
                xTextField.setText("");
                yTextField.setText("");
                azimuthTextField.setText("");
                rTextField.setText("");
                angleTextField.setText("");

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
                    isAddingCheckBox.setSelected(false);
                    isTrajectoryAdding = true;
                    addTrajectoryButton.setText("Press here to finish");
                    calculateVisibleButton.setEnabled(false);
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
                    calculateVisibleButton.setEnabled(true);
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
                mapPanel.removeAll();
                addMapPicture();
                Dimension size = mapLabel.getPreferredSize();
                mapLabel.setBounds(insets.left, insets.top, size.width, size.height);
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
                List<StraightLine> lines = TrackingSystem.findLocalMaximums(TrackingSystem.getAccumulator(), startR, endR);
                log(lines.size());
                for(StraightLine line: lines)
                    drawLine(line);
//                System.out.println("Points on corner");

                TrackingSystem.findInOutVectors();
            }
        });

        loadTrajectoriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(rootPanel);
                if( returnVal == JFileChooser.APPROVE_OPTION){
                    File file = fc.getSelectedFile();
                    BufferedReader reader = null;
                    try{
                        reader = new BufferedReader(new FileReader(file));
                        String size[] = reader.readLine().split(" ");
                        int newWidth = Integer.parseInt(size[0]),
                            newHeight = Integer.parseInt(size[1]);
                        mapPanel.setSize(newWidth, newHeight);
                        mapPanel.setPreferredSize(new Dimension(newWidth,newHeight));

                        log(mapPanel.getWidth()+" "+ mapPanel.getHeight());
                        rootPanel.repaint();
                        BufferedImage loadedPoints = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = loadedPoints.createGraphics();
                        g2d.setComposite(AlphaComposite.Clear);
                        g2d.fillRect(0, 0, newWidth, newHeight);
                        g2d.setColor(Color.BLACK);
                        g2d.setComposite(AlphaComposite.Src);
                        String line = null;
                        while((line = reader.readLine()) != null){
                            String point[] = line.split(" ");
                            int x = Integer.parseInt(point[0]),
                                    y = Integer.parseInt(point[1]);
                            g2d.fillOval(x, y, 4, 4);
                            TrackingSystem.addPoint(new VisitedPoint((double)x,(double)y));

                        }
                        g2d.dispose();
                        if (loadedPointsLabel!=null) mapPanel.remove(loadedPointsLabel);
                        loadedPointsLabel = new JLabel(new ImageIcon(loadedPoints));
                        mapPanel.add(loadedPointsLabel);
                        loadedPointsLabel.setBounds(insets.left, insets.top,
                                loadedPointsLabel.getPreferredSize().width, loadedPointsLabel.getPreferredSize().height);
//                        pack();
                        repaint();
                        TrackingSystem.setWidth(mapPanel.getWidth());
                        TrackingSystem.setHeight(mapPanel.getHeight());
                        TrackingSystem.calculateVisibleAfterLoading();
                    }
                    catch(IOException ex){
                        ex.printStackTrace();
                    }
                    finally {
                        try{
                            if (reader != null)
                                reader.close();
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
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

    public void addNewCameraToPanel(int x, int y){

        xKeyPointTextField.setEnabled(false);
        yKeyPointTextField.setEnabled(false);
        tKeyPointTextField.setEnabled(false);
        vKeyPointTextField.setEnabled(false);
        if (currentKeyPoint != null) {
            currentKeyPoint.setBackground(Color.WHITE);
            currentKeyPoint = null;
        }
        Insets insets = mapPanel.getInsets();
//        Camera newCamera = new Camera("",e.getX() + insets.left - cameraWidth / 2, e.getY() + insets.top - cameraHeight / 20, 90, 120, 90);
        Camera newCamera = new Camera("", x, y, 90, 120, 90);
        newCamera.setPreferredSize(new Dimension(cameraWidth, cameraHeight));
//                    testButton.setFont(new Font("Arial", Font.PLAIN, 2));
        TrackingSystem.addCamera(newCamera);
//        System.out.print(TrackingSystem.getCameraList().size());
        mapPanel.add(newCamera);
        mapPanel.add(newCamera.getFOVLabel());
        Dimension size = newCamera.getPreferredSize();
        newCamera.setBounds(x + insets.left - size.width / 2, y + insets.top - size.height / 2, size.width, size.height);
        size = newCamera.getFOVLabel().getPreferredSize();
        newCamera.getFOVLabel().setBounds(x + insets.left - size.width / 2, y + insets.top - size.height / 2, size.width, size.height);

        newCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentCamera!=null) {
                    currentCamera.setBackground(Color.WHITE);
                    mapPanel.remove(currentCamera.getVisibleImageLabel());
                }
                log("Camera selected");
//                log("start angle " + ((Camera)e.getSource()).getArc().getAngleStart() +
//                        " end angle " + (((Camera)e.getSource()).getArc().getAngleStart() +
//                        ((Camera)e.getSource()).getArc().getAngleExtent()));
                isCameraChanging = true;


                //DEBUG HERE
//                Point2D startPoint = ((Camera)e.getSource()).getArc().getStartPoint(),
//                        endPoint = ((Camera)e.getSource()).getArc().getEndPoint(),
//                        centerPoint = new Point2D.Float(((Camera)e.getSource()).getx(), ((Camera)e.getSource()).gety());
//                log(startPoint);
//                log(endPoint);
//                log(centerPoint);
//                Camera camera = (Camera)e.getSource();
//                double startx = camera.getx() - camera.getR() + camera.getArc().getStartPoint().getX(),
//                        starty = camera.gety() - camera.getR() + camera.getArc().getStartPoint().getY(),
//                        endx = camera.getx() - camera.getR() + camera.getArc().getEndPoint().getX(),
//                        endy = camera.gety() - camera.getR() + camera.getArc().getEndPoint().getY();
//                Point2D startPoint = new Point2D.Double(startx, starty),
//                        endPoint = new Point2D.Double(endx, endy),
//                        centerPoint = new Point2D.Float(camera.getx(), camera.gety());
//
//                log(startPoint);
//                log(endPoint);
//                log(centerPoint);
//                log(camera.getArc().getStartPoint().getX());
//                log(camera.getArc().getStartPoint().getY());
//                log(camera.getArc().getEndPoint().getX());
//                log(camera.getArc().getEndPoint().getY());
                //DEBUG END

                xTextField.setEnabled(true);
                yTextField.setEnabled(true);
                azimuthTextField.setEnabled(true);
                rTextField.setEnabled(true);
                angleTextField.setEnabled(true);
                removeCameraButton.setEnabled(true);

                currentCamera = (Camera)e.getSource();
                currentCamera.setBackground(Color.RED);

                xTextField.setText(Integer.toString(currentCamera.getx()));
                yTextField.setText(Integer.toString(currentCamera.gety()));
                azimuthTextField.setText(Integer.toString(currentCamera.getAzimuth()));
                rTextField.setText(Integer.toString(currentCamera.getR()));
                angleTextField.setText(Integer.toString(currentCamera.getAngle()));

//                updateCurrentCamera();
                TrackingSystem.calculateVisibleForCamera(currentCamera);
                currentCamera.redrawVisibleImage();
                mapPanel.add(currentCamera.getVisibleImageLabel());
//                mapPanel.setComponentZOrder(currentCamera, 3);
//                mapPanel.setComponentZOrder(currentCamera.getVisibleImageLabel(), getComponentCount() - 1);
                Insets insets = mapPanel.getInsets();
                Dimension size = currentCamera.getVisibleImageLabel().getPreferredSize();
                currentCamera.getVisibleImageLabel().setBounds(insets.left, insets.top, size.width, size.height);
//                log(getComponentZOrder(currentCamera.getVisibleImageLabel()));
                mapPanel.repaint();

                isCameraChanging = false;
            }
        });
    }

    public void updateCurrentCamera(){
        log("Updating camera");
        int     x       = currentCamera.getx(),
                y       = currentCamera.gety(),
                azimuth = currentCamera.getAzimuth(),
                r       = currentCamera.getR(),
                angle   = currentCamera.getAngle();
        boolean isChanged = false;
        try{
//            log(xTextField.getText());
//            log(yTextField.getText());
//            log(azimuthTextField.getText());
//            log(rTextField.getText());
//            log(angleTextField.getText());
            int     newX        = Integer.parseInt(xTextField.getText()),
                    newY        = Integer.parseInt(yTextField.getText()),
                    newAzimuth  = Integer.parseInt(azimuthTextField.getText()),
                    newR        = Integer.parseInt(rTextField.getText()),
                    newAngle    = Integer.parseInt(angleTextField.getText());
//            log(newY);
//            log(mapPanel.getHeight());
            if (x != newX || y != newY || azimuth != newAzimuth || r != newR || angle != newAngle){
                x = newX < 0 ? 0 : newX > mapPanel.getWidth() ? mapPanel.getWidth() : newX;
//                x       = newX;
                y = newY < 0 ? 0 : newY > mapPanel.getHeight() ? mapPanel.getHeight() : newY;
//                y       = newY;
                azimuth = newAzimuth;
                r       = newR;
                angle   = newAngle;
//                log("new x y " + x + " " + y);
                currentCamera.setx(x);
                currentCamera.sety(y);
                currentCamera.setAzimuth(azimuth);
                currentCamera.setR(r);
                currentCamera.setAngle(angle);
                currentCamera.redrawFOV();

                Insets insets = mapPanel.getInsets();
                Dimension size = currentCamera.getPreferredSize();
                currentCamera.setBounds(x + insets.left - size.width / 2, y + insets.top - size.height / 2, size.width, size.height);
                size = currentCamera.getFOVLabel().getPreferredSize();
                currentCamera.getFOVLabel().setBounds(x + insets.left - size.width / 2, y + insets.top - size.height / 2, size.width, size.height);
//                currentCamera.clearVisibleImage();
                TrackingSystem.calculateVisibleForCamera(currentCamera);
//                currentCamera.redrawVisibleImage();
                mapPanel.repaint();
                log("Image was redrawed. New xy "+x+" "+y);
//                log("start angle " + currentCamera.getArc().getAngleStart() +
//                        " end angle " + (currentCamera.getArc().getAngleStart() +
//                        currentCamera.getArc().getAngleExtent()));
//                System.out.print(TrackingSystem.getCameraList().get(TrackingSystem.getCameraList().indexOf(currentCamera)).getx());
            }
        }
        catch (IllegalArgumentException ex){
        }
    }

    private void updateCurrentKeyPoint(){
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

    public void addMapPicture(){
//        mapPanel.add(mapLabel);
//        mapPanel.setComponentZOrder(mapLabel, 0);
//        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
//        JLabel jl = new JLabel(new ImageIcon(bi));
//        mapPanel.add(jl);
//        Insets insets = mapPanel.getInsets();
//        Dimension size = jl.getPreferredSize();
//        jl.setBounds(insets.left, insets.top, size.width, size.height);
//        mapPanel.setComponentZOrder(jl, 1);
//        jl = new JLabel(new ImageIcon(bi));
//        mapPanel.add(jl);
//        jl.setBounds(insets.left, insets.top, size.width, size.height);
//        mapPanel.setComponentZOrder(jl,1);

    }

    public void mouseClickHandler(MouseEvent e){
        if (isAddingCheckBox.isSelected()) {
            addNewCameraToPanel(e.getX(), e.getY());
        }
        else if(isTrajectoryAdding){
            KeyPoint newKeyPoint = new KeyPoint(e.getX(), e.getY(), 10);
            newKeyPoint.setPreferredSize(new Dimension(keyPointWidth, keyPointHeight));
            Insets insets = mapPanel.getInsets();
            Dimension size = newKeyPoint.getPreferredSize();
            mapPanel.add(newKeyPoint);
//            mapPanel.setComponentZOrder(newKeyPoint, 1);
            currentTrajectory.addKeyPoint(newKeyPoint);
            newKeyPoint.setBounds(e.getX() + insets.left - size.width / 2, e.getY() + insets.left - size.height / 2, size.width, size.height);
            newKeyPoint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isKeyPointChanging = true;

                    xKeyPointTextField.setEnabled(true);
                    yKeyPointTextField.setEnabled(true);
                    tKeyPointTextField.setEnabled(true);
                    vKeyPointTextField.setEnabled(true);
                    if (currentKeyPoint != null) currentKeyPoint.setBackground(Color.WHITE);
                    currentKeyPoint = (KeyPoint)e.getSource();
                    currentTrajectory = currentKeyPoint.getParentTrajectory();
                    currentKeyPoint.setBackground(Color.RED);

                    xKeyPointTextField.setText(Integer.toString(currentKeyPoint.getx()));
                    yKeyPointTextField.setText(Integer.toString(currentKeyPoint.gety()));
                    vKeyPointTextField.setText(Double.toString(round(currentKeyPoint.getV(), 2)));
                    tKeyPointTextField.setText(Double.toString(round(currentKeyPoint.getT(), 2)));

                    isKeyPointChanging = false;
                }
            });
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
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
