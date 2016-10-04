package com.robot.group.launch;

import com.robot.group.paths.finding.PathsFindingGui;
import com.robot.group.paths.RobotsState;
import com.robot.group.paths.finding2.PathFinding2Gui;
import com.robot.group.paths.linking.PathsLinkingGui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LauncherGui extends JFrame {
    private JPanel rootPanel;
    private JButton pathsFindingButton;
    private JButton pathsLinkingButton;
    private JButton pathFindingLinkingButton;
    private JLabel ipLabel;
    private JButton testButton;

    public LauncherGui() {
        super();
        final long startTime = System.currentTimeMillis();
        setContentPane(rootPanel);
        setTitle("RobotGroup Launcher");
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            ipLabel.setText("IP:" + InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            ipLabel.setText("IP: unknown. Check your connection");
        }
        pathsFindingButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            RobotsState.getInstance().setMode(RobotsState.NETWORK_MODE);
            PathsFindingGui pathsFindingGui = new PathsFindingGui();
            pathsFindingGui.setVisible(true);
            this.dispose();
        }));
        pathsLinkingButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            RobotsState.getInstance().setMode(RobotsState.NETWORK_MODE);
            PathsLinkingGui pathsLinkingGui = new PathsLinkingGui();
            pathsLinkingGui.setVisible(true);
            this.dispose();
        }));
        pathFindingLinkingButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            RobotsState.getInstance().setMode(RobotsState.LOCAL_MODE);
            PathsFindingGui pathsFindingGui = new PathsFindingGui();
            PathsLinkingGui pathsLinkingGui = new PathsLinkingGui();
            pathsFindingGui.setVisible(true);
            pathsLinkingGui.setVisible(true);
            this.dispose();
        }));
        testButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            RobotsState.getInstance().setMode(RobotsState.LOCAL_MODE);
            PathFinding2Gui pathFinding2Gui = new PathFinding2Gui();
            pathFinding2Gui.setVisible(true);
            this.dispose();
        }));
        System.out.println("UI load time: " + (System.currentTimeMillis() - startTime));
    }

    private void createUIComponents() {

    }
}
