package com.robot.paths.finding;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PathsFindingGui extends JFrame implements ChangeListener {
    private JPanel contentPanel;
    //private JPanel surface;
    //private Surface surface;
    private JRadioButton placeRobotRadioButton;
    private JSlider robotAzimuthSlider;
    private JRadioButton placeFinishRadioButton;
    private JSlider finishDirectionSlider;
    private JRadioButton setRectRadioButton;
    private JSlider rectWeightSlider;
    private JButton loadMapButton;
    private JButton loadRealityButton;
    private JSlider robotSizeSlider;
    private JSlider robotRadiusSlider;
    private JButton calculatePassabilityButton;
    private JButton refreshGraphButton;
    private JSlider robotSensorsRangeSlider;
    private JSlider robotMinSpeedSlider;
    private JSlider robotMaxSpeedSlider;
    private JSlider robotAccelerationSlider;
    private JSlider robotDecelerationSlider;
    private JCheckBox showMapCheckBox;
    private JCheckBox showPassabilityCheckBox;
    private JCheckBox showRealityCheckBox;
    private JCheckBox showNodesCheckBox;
    private JCheckBox disableDrawingCheckBox;
    private JButton removeMapButton;
    private JButton removePassabilityButton;
    private JButton removeRealityButton;
    private JLabel robotCoordsLabel;
    private JLabel robotAzimuthLabel;
    private JLabel finishCoordsLabel;
    private JLabel finishDirectionLabel;
    private JLabel rectWeightLabel;
    private JLabel aboutPassabilityLabel;
    private JLabel robotSizeLabel;
    private JLabel robotRadiusLabel;
    private JLabel robotSensorsRangeLabel;
    private JLabel robotMinSpeedLabel;
    private JLabel robotMaxSpeedLabel;
    private JLabel robotAccelerationLabel;
    private JLabel robotDecelerationLabel;
    private JLabel robotCurrentSpeedLabel;
    private JScrollPane surfaceScrollPane;
    private JScrollPane controlScrollPane;
    private JButton addRobotButton;
    private JSpinner selectRobotSpinner;
    private JButton goAllButton;
    private JRadioButton placeFinishAll;
    private JButton removeRobotButton;
    private JCheckBox loggingCheckBox;
    private JScrollPane timeScrollPane;
    private JButton startTimeButton;
    private JRadioButton rainRadioButton;
    private JRadioButton dryRadioButton;
    private JRadioButton normalRadioButton;
    private Surface surface;

    private JFileChooser fileChooser;

    public PathsFindingGui() {

        setContentPane(contentPanel);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(placeRobotRadioButton);
        buttonGroup.add(placeFinishRadioButton);
        buttonGroup.add(setRectRadioButton);
        buttonGroup.add(placeFinishAll);
        placeRobotRadioButton.setSelected(true);

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image file (bmp, png, gif)", "bmp", "png", "gif"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + File.separator + "Maps"));

        setEnabledOther(false);
        setEnabledOperations(false);

        String[] keystrokeNames = {"UP", "DOWN", "LEFT", "RIGHT"};
        for (String keystrokeName : keystrokeNames) {
            surfaceScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                    KeyStroke.getKeyStroke(keystrokeName), "none");
        }
        surfaceScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        surfaceScrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        controlScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        controlScrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        timeScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        timeScrollPane.getHorizontalScrollBar().setUnitIncrement(10);



        this.surface.setScale(robotSizeSlider.getValue());
        this.surface.setRobotsRadius(robotRadiusSlider.getValue());
        this.surface.setRobotsSensorsRange(robotSensorsRangeSlider.getValue());
        this.surface.setRobotsMinSpeed(robotMinSpeedSlider.getValue());
        this.surface.setRobotsMaxSpeed(robotMaxSpeedSlider.getValue());
        this.surface.setRobotsAcceleration(robotAccelerationSlider.getValue());
        this.surface.setRobotsDeceleration(robotDecelerationSlider.getValue());
        this.surface.setStage(6);

        selectRobotSpinner.setValue(1);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Path Finding");
        setSize(1221, 720);
//        setVisible(true);
//        timeScrollPane.setVisible(false);


        createMyComponents();
    }

    private void createUIComponents() {
        Surface surface = new Surface();
        this.surface = surface;
        //surfaceScrollPane.add(surface);

        surface.setPreferredSize(new Dimension(0, 0));
        surfaceScrollPane = new JScrollPane(surface);
        surface.requestFocusInWindow();
    }

    private void createMyComponents() {
        loadMapButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                    surface.setMapImages(image);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                surface.removeRobots();
                surface.refreshScreen();
                MapInfo map = surface.getRobot().getMap();
                if (map.getImage() != null) {
                    surface.setPreferredSize(new Dimension(map.getImage().getWidth(null),
                            map.getImage().getHeight(null)));
                } else {
                    surface.setPreferredSize(new Dimension(0, 0));
                }
                surface.revalidate();
                surface.repaint();
                aboutPassabilityLabel.setText("Нажмите \"Расчитать проходимость\"");
                aboutPassabilityLabel.setForeground(Color.RED);
                setEnabledOther(true);
                setEnabledOperations(false);
                removeMapButton.setEnabled(true);
            }
        });

        loadRealityButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage realityMap = ImageIO.read(fileChooser.getSelectedFile());
                    surface.setRealityMaps(realityMap);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                surface.repaint();
                removeRealityButton.setEnabled(true);
            }
        });

        showMapCheckBox.addActionListener(e -> {
            surface.setShowMap(showMapCheckBox.isSelected());
            surface.repaint();
        });
        showPassabilityCheckBox.addActionListener(e -> {
            surface.setShowPassability(showPassabilityCheckBox.isSelected());
            surface.repaint();
        });
        showRealityCheckBox.addActionListener(e -> {
            surface.setShowReality(showRealityCheckBox.isSelected());
            surface.repaint();
        });
        showNodesCheckBox.addActionListener(e -> {
            surface.setShowNodes(showNodesCheckBox.isSelected());
            surface.repaint();
        });
        loggingCheckBox.addActionListener(e -> surface.setLogging(loggingCheckBox.isSelected()));
        removeMapButton.addActionListener(e -> {
            surface.removeMap();

            aboutPassabilityLabel.setText("Нажмите \"Загрузить карту\"");
            aboutPassabilityLabel.setForeground(Color.RED);
            goAllButton.setEnabled(false);
            setEnabledOther(false);
            setEnabledOperations(false);
            removeMapButton.setEnabled(false);
        });
        removePassabilityButton.addActionListener(e -> {
            surface.removePassabilities();
            aboutPassabilityLabel.setText("Нажмите \"Расчитать проходимость\"");
            aboutPassabilityLabel.setForeground(Color.RED);
            setEnabledOperations(false);
            goAllButton.setEnabled(false);
            removePassabilityButton.setEnabled(false);
        });
        removeRealityButton.addActionListener(e -> {
            surface.setRealityMaps(null);
            removeRealityButton.setEnabled(false);
        });
        calculatePassabilityButton.addActionListener(e -> {
            surface.calculatePassability(surface.getRobot().getRadius());
            aboutPassabilityLabel.setText("Проходимость расчитана");
            aboutPassabilityLabel.setForeground(Color.GREEN);
            setEnabledOperations(true);
            goAllButton.setEnabled(true);
            removePassabilityButton.setEnabled(true);
        });
        refreshGraphButton.addActionListener(e -> surface.refreshGraph());
        placeRobotRadioButton.addActionListener(e -> surface.setStage(Surface.STAGE_PLACE_ROBOT));
        placeFinishRadioButton.addActionListener(e -> surface.setStage(Surface.STAGE_SET_FINISH));
        setRectRadioButton.addActionListener(e -> surface.setStage(Surface.STAGE_SET_RECT_WEIGHT));
        placeFinishAll.addActionListener(e -> surface.setStage(Surface.STAGE_SET_FINISH_ALL));
        robotSizeSlider.addChangeListener(this);
        finishDirectionSlider.addChangeListener(this);
        robotAzimuthSlider.addChangeListener(this);
        rectWeightSlider.addChangeListener(this);
        robotAccelerationSlider.addChangeListener(this);
        robotDecelerationSlider.addChangeListener(this);
        robotMaxSpeedSlider.addChangeListener(this);
        robotMinSpeedSlider.addChangeListener(this);
        robotRadiusSlider.addChangeListener(this);
        robotSensorsRangeSlider.addChangeListener(this);
        addRobotButton.addActionListener(e -> {
            try {
                Robot robot = surface.getRobot().clone();
                robot.setPosition(new Point(Robot.ROBOT_NOWHERE_X, Robot.ROBOT_NOWHERE_Y));
                surface.addRobot(robot);
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            }
        });
        selectRobotSpinner.addChangeListener(e -> {
            int value = (Integer) selectRobotSpinner.getValue();
            if (surface.getRobotCount() < value) {
                value = surface.getRobotCount();
                selectRobotSpinner.setValue(value);
            }
            if (value < 1) {
                value = 1;
                selectRobotSpinner.setValue(value);
            }
            surface.selectRobot(value - 1);
            refreshRobotParameters();
        });
        goAllButton.addActionListener(e -> {
            if (!Objects.equals(goAllButton.getText(), "Стоп")) {
                runRobots();
            } else {
                surface.stopRobots();
            }
        });

        startTimeButton.addActionListener(e -> {
            if (GlobalTime.isTimeRuns()) {
                GlobalTime.setTimeRuns(false);
                startTimeButton.setText("Запустить время");
            } else {
                GlobalTime.setTimeRuns(true);
                startTimeButton.setText("Остановить время");
            }
        });

        ButtonGroup weatherButtonGroup = new ButtonGroup();
        weatherButtonGroup.add(rainRadioButton);
        weatherButtonGroup.add(dryRadioButton);
        weatherButtonGroup.add(normalRadioButton);

        rainRadioButton.addActionListener(e -> GlobalTime.setWeather(GlobalTime.RAIN_WEATHER));
        dryRadioButton.addActionListener(e -> GlobalTime.setWeather(GlobalTime.DRY_WEATHER));
        normalRadioButton.addActionListener(e -> GlobalTime.setWeather(GlobalTime.NORMAL_WEATHER));

        makeExcessInvisible();

        removeRobotButton.addActionListener(e -> selectRobotSpinner.setValue(surface.removeRobot() + 1));
    }

    private void refreshRobotParameters() {
        robotAzimuthLabel.setText("Азимут робота: " +
                Integer.toString((int) Math.toDegrees(surface.getRobot().getAzimuth())));
        robotAzimuthSlider.setValue((int) Math.toDegrees(surface.getRobot().getAzimuth()));

        finishDirectionLabel.setText("Направление финиша: " +
                Integer.toString((int) Math.toDegrees(surface.getRobot().getFinish().getDirection())));
        finishDirectionSlider.setValue((int) Math.toDegrees(surface.getRobot().getFinish().getDirection()));
    }

    private void setEnabledOther(boolean enabled) {
        robotSizeLabel.setEnabled(enabled);
        robotSizeSlider.setEnabled(enabled);
        robotRadiusLabel.setEnabled(enabled);
        robotRadiusSlider.setEnabled(enabled);
        calculatePassabilityButton.setEnabled(enabled);
        refreshGraphButton.setEnabled(enabled);
        robotSensorsRangeLabel.setEnabled(enabled);
        robotSensorsRangeSlider.setEnabled(enabled);
        robotMinSpeedLabel.setEnabled(enabled);
        robotMinSpeedSlider.setEnabled(enabled);
        robotMaxSpeedLabel.setEnabled(enabled);
        robotMaxSpeedSlider.setEnabled(enabled);
        robotAccelerationLabel.setEnabled(enabled);
        robotAccelerationSlider.setEnabled(enabled);
        robotDecelerationLabel.setEnabled(enabled);
        robotDecelerationSlider.setEnabled(enabled);
    }

    private void setEnabledOperations(boolean enabled) {
        addRobotButton.setEnabled(enabled);
        removeRobotButton.setEnabled(enabled);
        selectRobotSpinner.setEnabled(enabled);
        placeRobotRadioButton.setEnabled(enabled);
        robotCoordsLabel.setEnabled(enabled);
        robotAzimuthLabel.setEnabled(enabled);
        robotAzimuthSlider.setEnabled(enabled);
        placeFinishRadioButton.setEnabled(enabled);
        placeFinishAll.setEnabled(enabled);
        finishCoordsLabel.setEnabled(enabled);
        finishDirectionLabel.setEnabled(enabled);
        finishDirectionSlider.setEnabled(enabled);
        setRectRadioButton.setEnabled(enabled);
        rectWeightLabel.setEnabled(enabled);
        rectWeightSlider.setEnabled(enabled);
    }

    public void stateChanged(ChangeEvent event) {
        Object source = event.getSource();
        if (source == robotAzimuthSlider) {
            if (robotAzimuthSlider.isEnabled()) { // if we manually set this slider and not programmatically
                surface.setRobotAzimuth(Math.toRadians(robotAzimuthSlider.getValue()));
                robotAzimuthLabel.setText("Азимут робота: " +
                        Integer.toString((int) Math.toDegrees(surface.getRobot().getAzimuth())));
            }
        } else if (source == finishDirectionSlider) {
            surface.setFinishDirection(Math.toRadians(finishDirectionSlider.getValue()));
            finishDirectionLabel.setText("Направление финиша: " +
                    Integer.toString((int) Math.toDegrees(surface.getFinishDirection())));
        } else if (source == rectWeightSlider) {
            surface.setRectWeight(rectWeightSlider.getValue());
            rectWeightLabel.setText("Коэффициент проходимости: " + Integer.toString(rectWeightSlider.getValue()));
        } else if (source == robotSizeSlider) {
            surface.setScale(robotSizeSlider.getValue());
            robotSizeLabel.setText("Размер робота (масштаб): " + Integer.toString(robotSizeSlider.getValue()));

            aboutPassabilityLabel.setText("Нажмите \"Расчитать проходимость\"");
            aboutPassabilityLabel.setForeground(Color.RED);
            setEnabledOperations(false);
            goAllButton.setEnabled(false);

        } else if (source == robotRadiusSlider) {
            surface.setRobotsRadius(robotRadiusSlider.getValue());
            robotRadiusLabel.setText("Радиус поворота: " + Integer.toString(robotRadiusSlider.getValue()));

            aboutPassabilityLabel.setText("Нажмите \"Расчитать проходимость\"");
            aboutPassabilityLabel.setForeground(Color.RED);
            setEnabledOperations(false);
            goAllButton.setEnabled(false);
        } else if (source == robotSensorsRangeSlider) {
            surface.setRobotsSensorsRange(robotSensorsRangeSlider.getValue());
            robotSensorsRangeLabel.setText("Дальность видимости робота: " +
                    Integer.toString(robotSensorsRangeSlider.getValue()));
        } else if (source == robotMinSpeedSlider) {
            if (robotMinSpeedSlider.getValue() >= robotMaxSpeedSlider.getValue()) {
                robotMinSpeedSlider.setValue(robotMaxSpeedSlider.getValue() - 1);
            }
            surface.setRobotsMinSpeed(robotMinSpeedSlider.getValue());
            robotMinSpeedLabel.setText("Минимальная скорость: " + Integer.toString(robotMinSpeedSlider.getValue()));
        } else if (source == robotMaxSpeedSlider) {
            if (robotMinSpeedSlider.getValue() >= robotMaxSpeedSlider.getValue()) {
                robotMaxSpeedSlider.setValue(robotMinSpeedSlider.getValue() + 1);
            }
            surface.setRobotsMaxSpeed(robotMaxSpeedSlider.getValue());
            robotMaxSpeedLabel.setText("Максимальная скорость: " + Integer.toString(robotMaxSpeedSlider.getValue()));
        } else if (source == robotAccelerationSlider) {
            surface.setRobotsAcceleration(robotAccelerationSlider.getValue());
            robotAccelerationLabel.setText("Ускорение: " + Integer.toString(robotAccelerationSlider.getValue()));
        } else if (source == robotDecelerationSlider) {
            surface.setRobotsDeceleration(robotDecelerationSlider.getValue());
            robotDecelerationLabel.setText("Торможение: " + Integer.toString(robotDecelerationSlider.getValue()));
        }
    }

//    public void setRobotCoordinates(int x, int y) {
//        if (x != -1000 && y != -1000)
//            robotCoordsLabel.setText("Координаты робота: " + x + ", " + y);
//        else
//            robotCoordsLabel.setText("Координаты робота: ?, ?");
//    }

//    public void setFinishCoordinates(int x, int y) {
//        if (x != -1000 && y != -1000)
//            finishCoordsLabel.setText("Координаты финиша: " + x + ", " + y);
//        else
//            finishCoordsLabel.setText("Координаты финиша: ?, ?");
//    }

    private void runRobots() {
        final Thread t = new Thread() {
            public void run() {
                surface.runRobots();
            }
        };
        final Thread t_draw = new Thread() {
            public void run() {
                robotAzimuthSlider.setEnabled(false);
                goAllButton.setText("Стоп");
                loadMapButton.setEnabled(false);
                loadRealityButton.setEnabled(false);
                robotSizeSlider.setEnabled(false);
                robotRadiusSlider.setEnabled(false);
                robotDecelerationSlider.setEnabled(false);
                calculatePassabilityButton.setEnabled(false);
                refreshGraphButton.setEnabled(false);
                removeMapButton.setEnabled(false);
                removePassabilityButton.setEnabled(false);
                removeRealityButton.setEnabled(false);
                addRobotButton.setEnabled(false);
                removeRobotButton.setEnabled(false);
                selectRobotSpinner.setEnabled(false);
                placeRobotRadioButton.setEnabled(false);
                placeFinishRadioButton.setEnabled(false);
                placeFinishAll.setEnabled(false);
                setRectRadioButton.setEnabled(false);
                robotAzimuthSlider.setEnabled(false);
                finishDirectionSlider.setEnabled(false);
                loggingCheckBox.setEnabled(false);

                rectWeightSlider.setEnabled(false);
                while (t.isAlive()) {
                    if (!disableDrawingCheckBox.isSelected()) {
                        try {
                            Thread.sleep(25);        //40 fps
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robotCurrentSpeedLabel.setText("Текущая скорость: " +
                                Integer.toString((int) surface.getRobotSpeed()));
                        robotCoordsLabel.setText("Координаты робота: " +
                                Integer.toString((int) surface.getRobot().getX()) + ", " +
                                Integer.toString((int) surface.getRobot().getY()));
                        robotAzimuthLabel.setText("Азимут робота: " +
                                Integer.toString((int) Math.toDegrees(surface.getRobot().getAzimuth())));
                        robotAzimuthSlider.setValue((int) Math.toDegrees(surface.getRobot().getAzimuth()));
                        surface.repaint();
                    }
                }
                robotAzimuthSlider.setEnabled(true);
                goAllButton.setText("Поехали все");
                loadMapButton.setEnabled(true);
                loadRealityButton.setEnabled(true);
                robotSizeSlider.setEnabled(true);
                robotRadiusSlider.setEnabled(true);
                robotDecelerationSlider.setEnabled(true);
                calculatePassabilityButton.setEnabled(true);
                refreshGraphButton.setEnabled(true);
                removeMapButton.setEnabled(true);
                removePassabilityButton.setEnabled(true);
                removeRealityButton.setEnabled(true);
                addRobotButton.setEnabled(true);
                removeRobotButton.setEnabled(true);
                selectRobotSpinner.setEnabled(true);
                placeRobotRadioButton.setEnabled(true);
                placeFinishRadioButton.setEnabled(true);
                placeFinishAll.setEnabled(true);
                setRectRadioButton.setEnabled(true);
                robotAzimuthSlider.setEnabled(true);
                finishDirectionSlider.setEnabled(true);
                rectWeightSlider.setEnabled(true);
                loggingCheckBox.setEnabled(true);
                surface.repaint();
                repaint();
            }
        };

        t.start();
        t_draw.start();
    }

//    public void setStage() {
//        if (placeRobotRadioButton.isSelected())
//            surface.setStage(6);
//        if (placeFinishRadioButton.isSelected())
//            surface.setStage(7);
//        if (setRectRadioButton.isSelected())
//            surface.setStage(8);
//    }



    private void makeExcessInvisible() {
        robotCoordsLabel.setVisible(false);
        finishCoordsLabel.setVisible(false);
        setRectRadioButton.setVisible(false);
        rectWeightLabel.setVisible(false);
        rectWeightSlider.setVisible(false);
    }
}
