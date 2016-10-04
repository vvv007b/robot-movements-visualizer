package com.robot.group.paths.finding2;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by bocharov_n on 04.10.16.
 */
public class PathFinding2Gui extends JFrame{
    private JPanel rootPanel;
    private JScrollPane mapScrollPane;
    private JButton addRobotButton;
    private JButton loadMapButton;
    private JButton calculatePassabilityButton;
    private JRadioButton placeRobotRadioButton;
    private JRadioButton placeFinishRadioButton;
    private JRadioButton placeFinishToAllRadioButton;
    private JSlider robotSizeSlider;
    private Surface surface;

    private JFileChooser fileChooser;


    public PathFinding2Gui(){
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
    }

    private void createMyComponents() {
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image file (bmp, png, gif)", "bmp", "png", "gif"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        ButtonGroup toolsButtonGroup = new ButtonGroup();
        toolsButtonGroup.add(placeRobotRadioButton);
        toolsButtonGroup.add(placeFinishRadioButton);
        toolsButtonGroup.add(placeFinishToAllRadioButton);
        placeRobotRadioButton.setSelected(true);

        placeRobotRadioButton.addActionListener(e -> surface.setSelectedTool(Surface.PLACE_ROBOT_TOOL));
        placeFinishRadioButton.addActionListener(e -> surface.setSelectedTool(Surface.PLACE_FINIST_TOOL));
        placeFinishToAllRadioButton.addActionListener(e -> surface.setSelectedTool(Surface.PLACE_FINISH_TO_ALL_TOOL));

        addRobotButton.addActionListener(e -> {
            Robot robot = new Robot();
            surface.addRobot(robot);
            surface.setCurRobot(robot);
        });
        loadMapButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                    surface.setMapImage(image);
                    surface.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
//                surface.setPreferredSize(new Dimension(map.getImage().getWidth(null), map.getImage().getHeight(null)));

                surface.revalidate();
                surface.repaint();
            }
        });
        robotSizeSlider.addChangeListener(e -> surface.setRobotSize(robotSizeSlider.getValue()));
    }


}
