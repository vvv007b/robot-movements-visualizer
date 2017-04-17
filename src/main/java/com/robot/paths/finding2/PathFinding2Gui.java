package com.robot.paths.finding2;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Created by bocharov_n on 04.10.16.
 */
public class PathFinding2Gui extends JFrame {
    private JPanel rootPanel;
    private JScrollPane mapScrollPane;
    private JButton addRobotButton;
    private JButton loadMapButton;
    private JButton calculatePassabilityButton;
    private JRadioButton placeRobotRadioButton;
    private JRadioButton placeFinishRadioButton;
    private JRadioButton placeFinishToAllRadioButton;
    private JSlider robotSizeSlider;
    private JSpinner selectRobotSpinner;
    private JButton deleteRobotButton;
    private JSlider finishAzimuthSlider;
    private JSlider robotAzimuthSlider;
    private JLabel robotAzimuthLabel;
    private JLabel finishAzimuthLabel;
    private JLabel robotSizeLabel;
    private JLabel selectRobotLabel;
    private javax.swing.JCheckBox showMapCheckBox;
    private javax.swing.JCheckBox showPassabilityMapCheckBox;
    private javax.swing.JCheckBox showRealityMapCheckBox;
    private JSlider robotRotationAngleSlider;
    private JLabel robotRotationAngleLabel;
    private JSlider samplingStepSlider;
    private JLabel samplingStepLabel;
    private Surface surface;

    private JFileChooser fileChooser;

    private static final Logger log = LoggerFactory.getLogger(PathFinding2Gui.class);


    public PathFinding2Gui() {
        super();
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setTitle("Paths finding");
        setSize(1001,720);
        createMyComponents();
    }

    private void createUIComponents() {
        Surface surface = new Surface();
        this.surface = surface;
        surface.setPreferredSize(new Dimension(0, 0));
        mapScrollPane = new JScrollPane(surface);
        surface.requestFocusInWindow();
        log.info("GUI created");
    }

    private void createMyComponents() {
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image file (bmp, png, gif)", "bmp", "png", "gif"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + File.separator + "Maps"));

        ButtonGroup toolsButtonGroup = new ButtonGroup();
        toolsButtonGroup.add(placeRobotRadioButton);
        toolsButtonGroup.add(placeFinishRadioButton);
        toolsButtonGroup.add(placeFinishToAllRadioButton);
        placeRobotRadioButton.setSelected(true);

        placeRobotRadioButton.addActionListener(e -> surface.setSelectedTool(Surface.PLACE_ROBOT_TOOL));
        placeFinishRadioButton.addActionListener(e -> surface.setSelectedTool(Surface.PLACE_FINISH_TOOL));
        placeFinishToAllRadioButton.addActionListener(e -> surface.setSelectedTool(Surface.PLACE_FINISH_TO_ALL_TOOL));

        addRobotButton.addActionListener(e -> {
            Robot robot = new Robot(surface);
            surface.addRobot(robot);
            if (surface.getRobots().size() == 1) {
                selectRobotSpinner.setValue(1);
                surface.setCurrentRobot(0);
                enableGui(true);
            }
//            surface.setCurrentRobot(robot);
        });
        deleteRobotButton.addActionListener(e -> {
            if (surface.getCurrentRobot() != null) {
                surface.getRobots().remove(surface.getCurrentRobot());
            }
            if (surface.getRobots().size() == 0) {
                enableGui(false);
            } else {
                //mark 1st robot as current
//                surface.setCurrentRobot(0);
                selectRobotSpinner.setValue(1);
            }
            surface.repaint();
        });
        selectRobotSpinner.addChangeListener(e -> {
            int value = (int) selectRobotSpinner.getValue();
            if (surface.getRobots().size() < value | value < 1) {
                value = (int) selectRobotSpinner.getPreviousValue();
                selectRobotSpinner.setValue(value);
            }
            surface.setCurrentRobot(value - 1);
            refreshRobotParameters();
            repaint();
        });
        loadMapButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                    surface.getMapInfo().setImageMap(image);
                    surface.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                    log.info("Map loaded");
                    robotSizeSlider.setEnabled(true);
                    robotSizeLabel.setEnabled(true);
                    calculatePassabilityButton.setEnabled(true);
                    samplingStepSlider.setEnabled(true);
                    samplingStepLabel.setEnabled(true);
                    enableGui(false);
                    addRobotButton.setEnabled(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    log.error("Can't load file. Error {}", ex);
                }

                surface.revalidate();
                surface.repaint();
            }
        });
        robotSizeSlider.addChangeListener(e -> {
            int value = robotSizeSlider.getValue();
            surface.getCurrentRobot().setSize(value);
//            surface.setRobotSize(robotSizeSlider.getValue());
            robotSizeLabel.setText("Robot size: " + robotSizeSlider.getValue());
//            enableGui(false);
//            addRobotButton.setEnabled(false);
//            surface.getRobots().clear();
        });
        robotRotationAngleSlider.addChangeListener(e -> {
            int value = robotRotationAngleSlider.getValue();
            surface.getCurrentRobot().setRotationAngle(Math.toRadians(value));
//            surface.setRobotRotationAngle(value);
            robotRotationAngleLabel.setText("Robot rotation angle: " + value);
        });
        //TODO: check point weight
        robotAzimuthSlider.addChangeListener(e -> {
            if (!surface.isRobotsRunning()) {
                surface.getCurrentRobot().getPosition().setAzimuth(Math.toRadians(robotAzimuthSlider.getValue()));
                robotAzimuthLabel.setText("Robot azimuth: " + robotAzimuthSlider.getValue());
                surface.repaint();
            }
        });
        finishAzimuthSlider.addChangeListener(e -> {
            if (!surface.isRobotsRunning()) {
                surface.getCurrentRobot().getFinish().setAzimuth(Math.toRadians(finishAzimuthSlider.getValue()));
                finishAzimuthLabel.setText("Finish azimuth: " + finishAzimuthSlider.getValue());
                surface.repaint();
            }
        });
        calculatePassabilityButton.addActionListener(e -> {
            long startTime = System.nanoTime();
            if (surface.getMapInfo().calculatePassability() == 1) {
//                enableGui(true);
                addRobotButton.setEnabled(true);
                log.info("Passability calculated in " + (System.nanoTime() - startTime) / 1_000_000 + " ms");
                surface.repaint();
            } else {
                log.error("Passability calculation error");
            }
        });
        showMapCheckBox.addActionListener(e ->
                surface.setShowMap(showMapCheckBox.isSelected()));
        showPassabilityMapCheckBox.addActionListener(e ->
                surface.setShowPassabilityMap(showPassabilityMapCheckBox.isSelected()));
        showRealityMapCheckBox.addActionListener(e ->
                surface.setShowRealityMap(showRealityMapCheckBox.isSelected()));
    }

    private void enableGui(boolean isEnable) {
        deleteRobotButton.setEnabled(isEnable);
        selectRobotSpinner.setEnabled(isEnable);
        selectRobotLabel.setEnabled(isEnable);
        placeRobotRadioButton.setEnabled(isEnable);
        robotAzimuthSlider.setEnabled(isEnable);
        robotAzimuthLabel.setEnabled(isEnable);
        placeFinishRadioButton.setEnabled(isEnable);
        placeFinishToAllRadioButton.setEnabled(isEnable);
        finishAzimuthLabel.setEnabled(isEnable);
        finishAzimuthSlider.setEnabled(isEnable);
        robotSizeLabel.setEnabled(isEnable);
        robotSizeSlider.setEnabled(isEnable);
        robotRotationAngleLabel.setEnabled(isEnable);
        robotRotationAngleSlider.setEnabled(isEnable);
    }

    private void refreshRobotParameters() {
        robotAzimuthSlider.setValue((int) Math.toDegrees(surface.getCurrentRobot().getPosition().getAzimuth()));
        finishAzimuthSlider.setValue((int) Math.toDegrees(surface.getCurrentRobot().getFinish().getAzimuth()));
        robotRotationAngleSlider.setValue((int) Math.toDegrees(surface.getCurrentRobot().getRotationAngle()));
        robotSizeSlider.setValue(surface.getCurrentRobot().getSize());

    }


}