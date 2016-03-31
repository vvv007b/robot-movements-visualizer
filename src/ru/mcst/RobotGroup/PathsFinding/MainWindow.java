package ru.mcst.RobotGroup.PathsFinding;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by sapachev_i on 10/20/15.
 */
public class MainWindow extends JFrame implements ChangeListener {
    private JPanel contentPanel;
    //private JPanel surface;
    //private Surface surface;
    private JRadioButton r_robot;
    private JSlider sl_robotAzimuth;
    private JRadioButton r_finish;
    private JSlider sl_finishDirection;
    private JRadioButton r_setRect;
    private JSlider sl_rectWeight;
    private JButton b_go;
    private JButton b_loadMap;
    private JButton b_loadReality;
    private JSlider sl_mapDelta;
    private JSlider sl_robotRadius;
    private JButton b_calculatePassability;
    private JButton b_refreshGraph;
    private JSlider sl_robotSensorsRange;
    private JSlider sl_robotMinSpeed;
    private JSlider sl_robotMaxSpeed;
    private JSlider sl_robotAcceleration;
    private JSlider sl_robotDeceleration;
    private JCheckBox cb_showMap;
    private JCheckBox cb_showPassability;
    private JCheckBox cb_showReality;
    private JCheckBox cb_showNodes;
    private JCheckBox cb_disableDrawing;
    private JButton b_removeMap;
    private JButton b_removePassability;
    private JButton b_removeReality;
    private JLabel l_robotCoordinates;
    private JLabel l_robotAzimuth;
    private JLabel l_finishCoordinates;
    private JLabel l_finishDirection;
    private JLabel l_rectWeight;
    private JLabel l_aboutPassability;
    private JLabel l_robotSize;
    private JLabel l_robotRadius;
    private JLabel l_robotSensorsRange;
    private JLabel l_robotMinSpeed;
    private JLabel l_robotMaxSpeed;
    private JLabel l_robotAcceleration;
    private JLabel l_robotDeceleration;
    private JLabel l_robotCurrentSpeed;
    private JScrollPane scrollForSurface;
    private JScrollPane scrollForControl;
    private JButton addRobotButton;
    private JSpinner sp_robot;
    private JButton goAllButton;
    private JRadioButton r_finishAll;
    private JButton removeRobotButton;
    private JCheckBox cb_logging;
    private Surface surface;

    JFileChooser fileChooser;

    public MainWindow(final Surface surface1) {
        this.surface =surface1;
        //scrollForSurface.add(surface);

        surface.setPreferredSize(new Dimension(0, 0));
        setContentPane(contentPanel);

        ButtonGroup g = new ButtonGroup();
        g.add(r_robot);
        g.add(r_finish);
        g.add(r_setRect);
        g.add(r_finishAll);
        r_robot.setSelected(true);

        fileChooser=new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image file (bmp, png, gif)", "bmp", "png", "gif"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        setEnabledOther(false);
        setEnabledOperations(false);

        String[] keystrokeNames = {"UP","DOWN","LEFT","RIGHT"};
        for(int i=0; i<keystrokeNames.length; ++i)
            scrollForSurface.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keystrokeNames[i]), "none");
        scrollForSurface.getVerticalScrollBar().setUnitIncrement(10);
        scrollForSurface.getHorizontalScrollBar().setUnitIncrement(10);
        scrollForControl.getVerticalScrollBar().setUnitIncrement(10);
        scrollForControl.getHorizontalScrollBar().setUnitIncrement(10);

        surface.requestFocusInWindow();


        this.surface.setScale(sl_mapDelta.getValue());
        this.surface.setRobotsRadius(sl_robotRadius.getValue());
        this.surface.setRobotsSensorsRange(sl_robotSensorsRange.getValue());
        this.surface.setRobotsMinSpeed(sl_robotMinSpeed.getValue());
        this.surface.setRobotsMaxSpeed(sl_robotMaxSpeed.getValue());
        this.surface.setRobotsAcceleration(sl_robotAcceleration.getValue());
        this.surface.setRobotsDeceleration(sl_robotDeceleration.getValue());
        this.surface.setStage(6);

        sp_robot.setValue(1);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Robot");
        setSize(1001, 720);
        setVisible(true);

        b_go.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (b_go.getText() != "Стоп") {
                    runRobot();
                } else {
                    surface.stopRobots();
                }
            }
        });
        b_loadMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                    if (map.getImage() != null)
                        surface.setPreferredSize(new Dimension(map.getImage().getWidth(null), map.getImage().getHeight(null)));
                    else
                        surface.setPreferredSize(new Dimension(0, 0));
                    surface.revalidate();
                    surface.repaint();
                    l_aboutPassability.setText("Нажмите \"Расчитать проходимость\"");
                    l_aboutPassability.setForeground(Color.RED);
                    setEnabledOther(true);
                    setEnabledOperations(false);
                    b_removeMap.setEnabled(true);
                }
            }
        });

        b_loadReality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage realityMap = ImageIO.read(fileChooser.getSelectedFile());
                        surface.setRealityMaps(realityMap);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    surface.repaint();
                    b_removeReality.setEnabled(true);
                }
            }
        });

        cb_showMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setShowMap(cb_showMap.isSelected());
                surface.repaint();
            }
        });
        cb_showPassability.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setShowPassability(cb_showPassability.isSelected());
                surface.repaint();
            }
        });
        cb_showReality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setShowReality(cb_showReality.isSelected());
                surface.repaint();
            }
        });
        cb_showNodes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setShowNodes(cb_showNodes.isSelected());
                surface.repaint();
            }
        });
        cb_logging.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setLogging(cb_logging.isSelected());
            }
        });
        b_removeMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.removeMap();

                l_aboutPassability.setText("Нажмите \"Загрузить карту\"");
                l_aboutPassability.setForeground(Color.RED);
                b_go.setEnabled(false);
                goAllButton.setEnabled(false);
                setEnabledOther(false);
                setEnabledOperations(false);
                b_removeMap.setEnabled(false);
            }
        });
        b_removePassability.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.removePassabilities();
                l_aboutPassability.setText("Нажмите \"Расчитать проходимость\"");
                l_aboutPassability.setForeground(Color.RED);
                setEnabledOperations(false);
                b_go.setEnabled(false);
                goAllButton.setEnabled(false);
                b_removePassability.setEnabled(false);
            }
        });
        b_removeReality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setRealityMaps(null);
                b_removeReality.setEnabled(false);
            }
        });
        b_calculatePassability.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.calculatePassability(surface.getRobot().getRadius());
                l_aboutPassability.setText("Проходимость расчитана");
                l_aboutPassability.setForeground(Color.GREEN);
                setEnabledOperations(true);
                b_go.setEnabled(true);
                goAllButton.setEnabled(true);
                b_removePassability.setEnabled(true);
            }
        });
        b_refreshGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.refreshGraph();
            }
        });
        r_robot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setStage(Surface.STAGE_PLACE_ROBOT);
            }
        });
        r_finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setStage(Surface.STAGE_SET_FINISH);
            }
        });
        r_setRect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setStage(Surface.STAGE_SET_RECT_WEIGHT);
            }
        });
        r_finishAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surface.setStage(Surface.STAGE_SET_FINISH_ALL);
            }
        });
        sl_mapDelta.addChangeListener(this);
        sl_finishDirection.addChangeListener(this);
        sl_robotAzimuth.addChangeListener(this);
        sl_rectWeight.addChangeListener(this);
        sl_robotAcceleration.addChangeListener(this);
        sl_robotDeceleration.addChangeListener(this);
        sl_robotMaxSpeed.addChangeListener(this);
        sl_robotMinSpeed.addChangeListener(this);
        sl_robotRadius.addChangeListener(this);
        sl_robotSensorsRange.addChangeListener(this);
        addRobotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Robot robot = surface.getRobot().clone();
                    robot.setX(Robot.ROBOT_NOWHERE_X);
                    robot.setY(Robot.ROBOT_NOWHERE_Y);
                    surface.addRobot(robot);
                } catch (CloneNotSupportedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        sp_robot.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (Integer) sp_robot.getValue();
                if (surface.getRobotCount() < value) {
                    value = surface.getRobotCount();
                    sp_robot.setValue(value);
                }
                if (value < 1) {
                    value = 1;
                    sp_robot.setValue(value);
                }
                surface.selectRobot(value - 1);
                refreshRobotParameters();
            }
        });
        goAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (goAllButton.getText() != "Стоп")
                    runRobots();
                else
                    surface.stopRobots();
            }
        });

        makeExcessUnvisible();

        removeRobotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sp_robot.setValue(surface.removeRobot()+1);
            }
        });

    }

    private void refreshRobotParameters() {
        l_robotAzimuth.setText("Азимут робота: " + Integer.toString((int) Math.toDegrees(surface.getRobot().getAzimuth())));
        sl_robotAzimuth.setValue((int) Math.toDegrees(surface.getRobot().getAzimuth()));

        l_finishDirection.setText("Направление финиша: " + Integer.toString((int) Math.toDegrees(surface.getRobot().getFinish().getDirection())));
        sl_finishDirection.setValue((int) Math.toDegrees(surface.getRobot().getFinish().getDirection()));
    }

    private void setEnabledOther(boolean enabled) {
        l_robotSize.setEnabled(enabled);
        sl_mapDelta.setEnabled(enabled);
        l_robotRadius.setEnabled(enabled);
        sl_robotRadius.setEnabled(enabled);
        b_calculatePassability.setEnabled(enabled);
        b_refreshGraph.setEnabled(enabled);
        l_robotSensorsRange.setEnabled(enabled);
        sl_robotSensorsRange.setEnabled(enabled);
        l_robotMinSpeed.setEnabled(enabled);
        sl_robotMinSpeed.setEnabled(enabled);
        l_robotMaxSpeed.setEnabled(enabled);
        sl_robotMaxSpeed.setEnabled(enabled);
        l_robotAcceleration.setEnabled(enabled);
        sl_robotAcceleration.setEnabled(enabled);
        l_robotDeceleration.setEnabled(enabled);
        sl_robotDeceleration.setEnabled(enabled);
    }
    private void setEnabledOperations(boolean enabled) {
        addRobotButton.setEnabled(enabled);
        removeRobotButton.setEnabled(enabled);
        sp_robot.setEnabled(enabled);
        r_robot.setEnabled(enabled);
        l_robotCoordinates.setEnabled(enabled);
        l_robotAzimuth.setEnabled(enabled);
        sl_robotAzimuth.setEnabled(enabled);
        r_finish.setEnabled(enabled);
        r_finishAll.setEnabled(enabled);
        l_finishCoordinates.setEnabled(enabled);
        l_finishDirection.setEnabled(enabled);
        sl_finishDirection.setEnabled(enabled);
        r_setRect.setEnabled(enabled);
        l_rectWeight.setEnabled(enabled);
        sl_rectWeight.setEnabled(enabled);
    }

    public void stateChanged(ChangeEvent e) {
        Object source=e.getSource();
        if(source==sl_robotAzimuth) {
            if(sl_robotAzimuth.isEnabled()) { // if we manually set this slider and not programmatically
                surface.setRobotAzimuth(Math.toRadians(sl_robotAzimuth.getValue()));
                l_robotAzimuth.setText("Азимут робота: " + Integer.toString((int) Math.toDegrees(surface.getRobot().getAzimuth())));
            }
        } else if(source==sl_finishDirection) {
            surface.setFinishDirection(Math.toRadians(sl_finishDirection.getValue()));
            l_finishDirection.setText("Направление финиша: " + Integer.toString((int)Math.toDegrees(surface.getFinishDirection())));
        } else if(source==sl_rectWeight) {
            surface.setRectWeight(sl_rectWeight.getValue());
            l_rectWeight.setText("Коэффициент проходимости: "+Integer.toString(sl_rectWeight.getValue()));
        } else if(source==sl_mapDelta) {
            surface.setScale(sl_mapDelta.getValue());
            l_robotSize.setText("Размер робота (масштаб): " + Integer.toString(sl_mapDelta.getValue()));

            l_aboutPassability.setText("Нажмите \"Расчитать проходимость\"");
            l_aboutPassability.setForeground(Color.RED);
            setEnabledOperations(false);
            b_go.setEnabled(false);
            goAllButton.setEnabled(false);

        } else if(source==sl_robotRadius) {
            surface.setRobotsRadius(sl_robotRadius.getValue());
            l_robotRadius.setText("Радиус поворота: " + Integer.toString(sl_robotRadius.getValue()));

            l_aboutPassability.setText("Нажмите \"Расчитать проходимость\"");
            l_aboutPassability.setForeground(Color.RED);
            setEnabledOperations(false);
            b_go.setEnabled(false);
            goAllButton.setEnabled(false);
        } else if(source==sl_robotSensorsRange) {
            surface.setRobotsSensorsRange(sl_robotSensorsRange.getValue());
            l_robotSensorsRange.setText("Дальность видимости робота: " + Integer.toString(sl_robotSensorsRange.getValue()));
        } else if(source==sl_robotMinSpeed) {
            if(sl_robotMinSpeed.getValue()>=sl_robotMaxSpeed.getValue())
                sl_robotMinSpeed.setValue(sl_robotMaxSpeed.getValue()-1);
            surface.setRobotsMinSpeed(sl_robotMinSpeed.getValue());
            l_robotMinSpeed.setText("Минимальная скорость: " + Integer.toString(sl_robotMinSpeed.getValue()));
        } else if(source==sl_robotMaxSpeed) {
            if(sl_robotMinSpeed.getValue()>=sl_robotMaxSpeed.getValue())
                sl_robotMaxSpeed.setValue(sl_robotMinSpeed.getValue()+1);
            surface.setRobotsMaxSpeed(sl_robotMaxSpeed.getValue());
            l_robotMaxSpeed.setText("Максимальная скорость: " + Integer.toString(sl_robotMaxSpeed.getValue()));
        } else if(source==sl_robotAcceleration) {
            surface.setRobotsAcceleration(sl_robotAcceleration.getValue());
            l_robotAcceleration.setText("Ускорение: " + Integer.toString(sl_robotAcceleration.getValue()));
        } else if(source==sl_robotDeceleration) {
            surface.setRobotsDeceleration(sl_robotDeceleration.getValue());
            l_robotDeceleration.setText("Торможение: " + Integer.toString(sl_robotDeceleration.getValue()));
        }
    }
    public void setRobotCoordinates(int x, int y) {
        if(x!=-1000 && y!=-1000)
            l_robotCoordinates.setText("Координаты робота: " + x + ", " + y);
        else
            l_robotCoordinates.setText("Координаты робота: ?, ?");
    }
    public void setFinishCoordinates(int x, int y) {
        if(x!=-1000 && y!=-1000)
            l_finishCoordinates.setText("Координаты финиша: "+x+", "+y);
        else
            l_finishCoordinates.setText("Координаты финиша: ?, ?");
    }

    private void runRobot() {
        final Thread t=new Thread()
        {
            public void run()
            {
                long time=System.currentTimeMillis();
                surface.runRobot();
            }
        };
        final Thread t_draw=new Thread()
        {
            public void run()
            {
                sl_robotAzimuth.setEnabled(false);
                b_go.setText("Стоп");
                b_loadMap.setEnabled(false);
                b_loadReality.setEnabled(false);
                sl_mapDelta.setEnabled(false);
                sl_robotRadius.setEnabled(false);
                sl_robotDeceleration.setEnabled(false);
                b_calculatePassability.setEnabled(false);
                b_refreshGraph.setEnabled(false);
                b_removeMap.setEnabled(false);
                b_removePassability.setEnabled(false);
                b_removeReality.setEnabled(false);
                cb_logging.setEnabled(false);

                while (t.isAlive())
                {
                    if(!cb_disableDrawing.isSelected()) {
                        try {
                            Thread.sleep(25);		//40 fps
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        l_robotCurrentSpeed.setText("Текущая скорость: " + Integer.toString((int)surface.getRobotSpeed()));
                        l_robotCoordinates.setText("Координаты робота: " + Integer.toString((int)surface.getRobot().getX()) + ", " + Integer.toString((int)surface.getRobot().getY()));
                        l_robotAzimuth.setText("Азимут робота: " + Integer.toString((int)Math.toDegrees(surface.getRobot().getAzimuth())));
                        sl_robotAzimuth.setValue((int)Math.toDegrees(surface.getRobot().getAzimuth()));
                        surface.repaint();
                    }
                }
                sl_robotAzimuth.setEnabled(true);
                b_go.setText("Поехали");
                b_loadMap.setEnabled(true);
                b_loadReality.setEnabled(true);
                sl_mapDelta.setEnabled(true);
                sl_robotRadius.setEnabled(true);
                sl_robotDeceleration.setEnabled(true);
                b_calculatePassability.setEnabled(true);
                b_refreshGraph.setEnabled(true);
                b_removeMap.setEnabled(true);
                b_removePassability.setEnabled(true);
                b_removeReality.setEnabled(true);
                cb_logging.setEnabled(true);
                surface.repaint();
                repaint();
            }
        };

        t.setDaemon(true);
        t_draw.setDaemon(true);
        t.start();
        t_draw.start();
    }
    private void runRobots() {
        final Thread t=new Thread()
        {
            public void run()
            {
                long time=System.currentTimeMillis();
                surface.runRobots();
            }
        };
        final Thread t_draw=new Thread()
        {
            public void run()
            {
                sl_robotAzimuth.setEnabled(false);
                goAllButton.setText("Стоп");
                b_loadMap.setEnabled(false);
                b_loadReality.setEnabled(false);
                sl_mapDelta.setEnabled(false);
                sl_robotRadius.setEnabled(false);
                sl_robotDeceleration.setEnabled(false);
                b_calculatePassability.setEnabled(false);
                b_refreshGraph.setEnabled(false);
                b_removeMap.setEnabled(false);
                b_removePassability.setEnabled(false);
                b_removeReality.setEnabled(false);
                addRobotButton.setEnabled(false);
                removeRobotButton.setEnabled(false);
                sp_robot.setEnabled(false);
                r_robot.setEnabled(false);
                r_finish.setEnabled(false);
                r_finishAll.setEnabled(false);
                r_setRect.setEnabled(false);
                sl_robotAzimuth.setEnabled(false);
                sl_finishDirection.setEnabled(false);
                cb_logging.setEnabled(false);

                sl_rectWeight.setEnabled(false);

                while (t.isAlive())
                {
                    if(!cb_disableDrawing.isSelected()) {
                        try {
                            Thread.sleep(25);		//40 fps
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        l_robotCurrentSpeed.setText("Текущая скорость: " + Integer.toString((int)surface.getRobotSpeed()));
                        l_robotCoordinates.setText("Координаты робота: " + Integer.toString((int)surface.getRobot().getX()) + ", " + Integer.toString((int)surface.getRobot().getY()));
                        l_robotAzimuth.setText("Азимут робота: " + Integer.toString((int)Math.toDegrees(surface.getRobot().getAzimuth())));
                        sl_robotAzimuth.setValue((int)Math.toDegrees(surface.getRobot().getAzimuth()));
                        surface.repaint();
                    }
                }
                sl_robotAzimuth.setEnabled(true);
                goAllButton.setText("Поехали все");
                b_loadMap.setEnabled(true);
                b_loadReality.setEnabled(true);
                sl_mapDelta.setEnabled(true);
                sl_robotRadius.setEnabled(true);
                sl_robotDeceleration.setEnabled(true);
                b_calculatePassability.setEnabled(true);
                b_refreshGraph.setEnabled(true);
                b_removeMap.setEnabled(true);
                b_removePassability.setEnabled(true);
                b_removeReality.setEnabled(true);
                addRobotButton.setEnabled(true);
                removeRobotButton.setEnabled(true);
                sp_robot.setEnabled(true);
                r_robot.setEnabled(true);
                r_finish.setEnabled(true);
                r_finishAll.setEnabled(true);
                r_setRect.setEnabled(true);
                sl_robotAzimuth.setEnabled(true);
                sl_finishDirection.setEnabled(true);
                sl_rectWeight.setEnabled(true);
                cb_logging.setEnabled(true);
                surface.repaint();
                repaint();
            }
        };

        t.start();
        t_draw.start();
    }
    public void setStage() {
        if (r_robot.isSelected())
            surface.setStage(6);
        if (r_finish.isSelected())
            surface.setStage(7);
        if(r_setRect.isSelected())
            surface.setStage(8);
    }

    private void createUIComponents() {
        scrollForSurface=new JScrollPane(surface);
    }

    void makeExcessUnvisible() {
        b_go.setVisible(false);
        l_robotCoordinates.setVisible(false);
        l_finishCoordinates.setVisible(false);
        r_setRect.setVisible(false);
        l_rectWeight.setVisible(false);
        sl_rectWeight.setVisible(false);
    }
}
