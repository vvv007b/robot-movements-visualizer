import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by bocharov_n on 14.10.15.
 */
public class Map extends JPanel {

    private BufferedImage map;

    public Map() {
        super();
        map = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = map.createGraphics();
        g2d.setPaint(new Color(255, 0, 0));
        g2d.fillRect(0, 0, map.getWidth(), map.getHeight());
        this.add(new JLabel(new ImageIcon(map)));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(map,0,0,null);
    }

    public BufferedImage getMap() {
        return map;
    }

    public void setMap(BufferedImage map) {
        this.map = map;
        repaint();
    }

}
