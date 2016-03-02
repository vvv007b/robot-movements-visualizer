package ru.mcst.RobotGroup.PathsLinking;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by bocharov_n on 02.03.16.
 */
public class MapUnderlay extends JPanel{

    public MapUnderlay(){
        super();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(new File("Maps/small.png"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        g2d.drawImage(mapImage, 0, 0, null);
        this.setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
    }
}
