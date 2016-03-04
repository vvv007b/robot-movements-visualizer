package ru.mcst.RobotGroup.PathsLinking;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by bocharov_n on 02.03.16.
 */
public class MapUnderlay extends JPanel{

    private static BufferedImage mapImage, trajectoriesImage;

    public MapUnderlay(){
        super();
        mapImage = null;
        trajectoriesImage = null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(mapImage != null) {
            Graphics2D g2d = (Graphics2D) g;
//        BufferedImage mapImage = null;
//        try {
//            mapImage = ImageIO.read(new File("Maps/small.png"));
//        }
//        catch(IOException e){
//            e.printStackTrace();
//        }
            g2d.drawImage(mapImage, 0, 0, null);
            this.setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
        }
        if(trajectoriesImage != null){
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(trajectoriesImage,0,0,null);
            g2d.dispose();
        }
    }


    public static void changeMapImage(Image image){
        mapImage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        trajectoriesImage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mapImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
    }

    public static void changeTrajectoriesImage(BufferedImage trajectories) {
//        trajectoriesImage = trajectories;
        Graphics2D g2d = trajectoriesImage.createGraphics();
        g2d.drawImage(trajectories, 0, 0, null);
        g2d.dispose();
    }

    public static BufferedImage getMapImage() {
        return mapImage;
    }

    public static BufferedImage getTrajectoriesImage() {
        return trajectoriesImage;
    }


}
