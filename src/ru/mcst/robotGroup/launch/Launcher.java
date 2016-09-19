package ru.mcst.robotGroup.launch;

import javax.swing.*;


public class Launcher {
    public Launcher() {}

    public static void main(String args[]){
        SwingUtilities.invokeLater(() -> {
            LauncherGUI launcherGUI = new LauncherGUI();
            launcherGUI.setVisible(true);
        });
        }
    }
