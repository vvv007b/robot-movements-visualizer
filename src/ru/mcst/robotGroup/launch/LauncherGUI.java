package ru.mcst.robotGroup.launch;

import ru.mcst.robotGroup.paths.RobotsState;
import ru.mcst.robotGroup.paths.finding.PathsFindingGUI;
import ru.mcst.robotGroup.paths.linking.PathsLinkingGUI;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LauncherGUI extends JFrame{
    private JPanel rootPanel;
    private JButton pathsFindingButton;
    private JButton pathsLinkingButton;
    private JButton pathFindingLinkingButton;
    private JLabel ipLabel;

    public LauncherGUI(){
        super();
        long startUITime = System.currentTimeMillis();
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
            PathsFindingGUI pathsFindingGUI = new PathsFindingGUI();
            pathsFindingGUI.setVisible(true);
            this.dispose();
        }));
        pathsLinkingButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            RobotsState.getInstance().setMode(RobotsState.NETWORK_MODE);
            PathsLinkingGUI pathsLinkingGUI = new PathsLinkingGUI();
            pathsLinkingGUI.setVisible(true);
            this.dispose();
        }));
        pathFindingLinkingButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            RobotsState.getInstance().setMode(RobotsState.LOCAL_MODE);
            PathsFindingGUI pathsFindingGUI = new PathsFindingGUI();
            PathsLinkingGUI pathsLinkingGUI = new PathsLinkingGUI();
            pathsFindingGUI.setVisible(true);
            pathsLinkingGUI.setVisible(true);
            this.dispose();
        }));
        System.out.println("UI load time: " + (System.currentTimeMillis() - startUITime));
    }

    private void createUIComponents() {

    }
}
