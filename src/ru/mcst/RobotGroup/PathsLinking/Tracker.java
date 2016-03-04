package ru.mcst.RobotGroup.PathsLinking;

import ru.mcst.RobotGroup.PathsFinding.Hypervisor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by bocharov_n on 02.03.16.
 */
class Tracker extends Thread{
    public Tracker(){
        super();
    }

    public void run(){
        while (true){
            ArrayList<double[]> allCoordinates = Hypervisor.getAllCoordinates();
            int[] mapSize = Hypervisor.getMapSize();
            if(mapSize != null && MapUnderlay.getMapImage() != null && MapUnderlay.getTrajectoriesImage() != null) {
                BufferedImage trajectories = new BufferedImage(mapSize[0],mapSize[1], BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = trajectories.createGraphics();
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, trajectories.getWidth(), trajectories.getHeight());
                g2d.setComposite(AlphaComposite.Src);
                g2d.setColor(Color.RED);

                for(double[] coord:allCoordinates){
                    if (TrackingSystem.isVisible(new Point2D.Double(coord[0], coord[1])));
                    g2d.fillOval((int)coord[0], (int)coord[1], 5, 5);
                }
                g2d.dispose();
                MapUnderlay.changeTrajectoriesImage(trajectories);
                GUI.getMapPanel().repaint();
            }
            try{
                this.sleep(100);
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }

        }
    }
}
