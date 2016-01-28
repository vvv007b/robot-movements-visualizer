import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bocharov_n on 14.10.15.
 */
public class Camera extends RoundButton{

    private int x, y, azimuth, r, angle;
    private BufferedImage FOV, visibleImage;
    private JLabel FOVLabel, visibleImageLabel;
    private List<Point2D> visiblePoints;
    private Arc2D arc;

    public Camera(String label){
        super(label);
        
        x = 1;
        y = 1;
        azimuth = 0;
        r = 3;
        angle = 90;
        visiblePoints = new ArrayList<>();
        arc = null;
        this.FOV = new BufferedImage(2 * r + 1, 2 * r + 1, BufferedImage.TYPE_INT_ARGB);
        this.visibleImage = new BufferedImage(x + r + 1, y + r + 1, BufferedImage.TYPE_INT_ARGB);
        FOVLabel = new JLabel(new ImageIcon(FOV));
        visibleImageLabel = new JLabel(new ImageIcon(visibleImage));
        redrawFOV();


    }

    public Camera(String label, int x, int y, int azimuth, int r, int angle){
        super(label);

        this.x = x;
        this.y = y;
        this.azimuth = azimuth;
        this.r = r;
        this.angle = angle;
        visiblePoints = new ArrayList<>();
        arc = null;
        this.FOV = new BufferedImage(2 * r + 1, 2 * r + 1, BufferedImage.TYPE_INT_ARGB);
        this.visibleImage = new BufferedImage(x + r + 1, y + r + 1, BufferedImage.TYPE_INT_ARGB);
        FOVLabel = new JLabel(new ImageIcon(FOV));
        visibleImageLabel = new JLabel(new ImageIcon(visibleImage));
        redrawFOV();



    }

    public void redrawFOV(){
        this.FOV = new BufferedImage(2 * r + 1, 2 * r + 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = this.FOV.createGraphics();

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, FOV.getWidth(), FOV.getHeight());

        arc = new Arc2D.Double(0.0, 0.5, r, r, 0.0, 60.0, Arc2D.CHORD);

        arc.setArcByCenter(r, r, r, azimuth - angle / 2, angle, Arc2D.OPEN);

        g2d.setComposite(AlphaComposite.Src);
        g2d.draw(arc);
        if(angle < 360) {
            g2d.drawLine(r, r, new Double(arc.getStartPoint().getX()).intValue(), new Double(arc.getStartPoint().getY()).intValue());
            g2d.drawLine(r, r, new Double(arc.getEndPoint().getX()).intValue(), new Double(arc.getEndPoint().getY()).intValue());
        }
        FOVLabel.removeAll();
        FOVLabel.setIcon(new ImageIcon(FOV));
        FOVLabel.repaint();

    }

    public void redrawVisibleImage(){
        this.visibleImage = new BufferedImage(x + r + 1, y + r + 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = visibleImage.createGraphics();

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, visibleImage.getWidth(), visibleImage.getHeight());
        g2d.setComposite(AlphaComposite.Src);
        g2d.setColor(Color.RED);
        for(Point2D point:visiblePoints){
            g2d.fillOval((int)point.getX() - 3, (int)point.getY() - 3, 6, 6);
        }
        g2d.dispose();
        visibleImageLabel.removeAll();
        visibleImageLabel.setIcon(new ImageIcon(visibleImage));
        visibleImageLabel.repaint();
    }

    public void clearVisibleImage(){
        this.visibleImage = new BufferedImage(x + r + 1, y + r + 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = visibleImage.createGraphics();

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, visibleImage.getWidth(), visibleImage.getHeight());
        g2d.dispose();
        visibleImageLabel.removeAll();
        visibleImageLabel.setIcon(new ImageIcon(visibleImage));
        visibleImageLabel.repaint();
    }

    public JLabel getVisibleImageLabel() {
        return visibleImageLabel;
    }

    public void setVisibleImageLabel(JLabel visibleImageLabel) {
        this.visibleImageLabel = visibleImageLabel;
    }

    public BufferedImage getVisibleImage() {
        return visibleImage;
    }

    public void setVisibleImage(BufferedImage visibleImage) {
        this.visibleImage = visibleImage;
    }

    public List<Point2D> getVisiblePoints() { return visiblePoints;}

    public void setVisiblePoints(List<Point2D> visiblePoints) {this.visiblePoints = visiblePoints;}

    public void addVisiblePoint(Point2D point){this.visiblePoints.add(point);}

    public Arc2D getArc() {return arc;}

    public void setArc(Arc2D arc) {this.arc = arc;}

    public int getx() {
        return x;
    }

    public void setx(int x) {
        this.x = x;
    }

    public int gety() {
        return y;
    }

    public void sety(int y) {
        this.y = y;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public BufferedImage getFOV() {
        return FOV;
    }

    public void setFOV(BufferedImage FOV) {
        this.FOV = FOV;
    }

    public JLabel getFOVLabel() {
        return FOVLabel;
    }

    public void setFOVLabel(JLabel FOVLabel) {
        this.FOVLabel = FOVLabel;
    }
}
