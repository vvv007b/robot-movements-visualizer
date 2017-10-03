package com.mcst.launch;

import javax.swing.SwingUtilities;


class Launcher {
    public Launcher() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LauncherGui launcherGui = new LauncherGui();
            launcherGui.setVisible(true);
        });
    }
}
