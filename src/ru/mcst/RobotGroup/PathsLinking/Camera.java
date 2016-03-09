package ru.mcst.RobotGroup.PathsLinking;

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
public class Camera{

    private int x, y, azimuth, r, angle;
    private BufferedImage FOV, visibleImage;
    private JLabel FOVLabel, visibleImageLabel;
    private List<VisitedPoint> visiblePoints;
    private Arc2D arc;
    private double accuracy = 1.0;

//    public Camera(String label){
//        super(label);
//
//        x = 1;
//        y = 1;
//        azimuth = 0;
//        r = 3;
//        angle = 90;
//        visiblePoints = new ArrayList<VisitedPoint>();
//        arc = null;
//        this.FOV = new BufferedImage(2 * r + 1, 2 * r + 1, BufferedImage.TYPE_INT_ARGB);
//        this.visibleImage = new BufferedImage(x + r + 1, y + r + 1, BufferedImage.TYPE_INT_ARGB);
//        FOVLabel = new JLabel(new ImageIcon(FOV));
//        visibleImageLabel = new JLabel(new ImageIcon(visibleImage));
//        redrawFOV();
//
//
//    }

    public Camera(int x, int y, int azimuth, int r, int angle){
        this.x = x;
        this.y = y;
        this.azimuth = azimuth;
        this.r = r;
        this.angle = angle;
        visiblePoints = new ArrayList<VisitedPoint>();
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

    public boolean isOnCorner(Point2D p){
        boolean isOnArc = Math.abs(Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2)) - this.r) < this.accuracy;
        boolean isOnLeftLine = Math.abs(p.getY() - this.y -
                Math.tan(Math.toRadians(this.azimuth + this.angle / 2)) * (p.getX() - this.x)) < this.accuracy; // TODO: check this
        boolean isOnRightLine = Math.abs(p.getY() - this.y -
                Math.tan(Math.toRadians(this.azimuth - this.angle / 2)) * (p.getX() - this.x)) < this.accuracy;
//        System.out.println(p.getX()+" "+p.getY());
//        System.out.println(Math.abs(p.getY() - this.y - Math.tan(this.azimuth + this.angle / 2) * (p.getX() - this.x)));
//        System.out.println( Math.abs(Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2)) - this.r));
//        System.out.println(isOnArc + " " + isOnLeftLine + " " + isOnRightLine);
        return isOnArc || isOnLeftLine || isOnRightLine;
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

    public List<VisitedPoint> getVisiblePoints() { return visiblePoints;}

    public void setVisiblePoints(List<VisitedPoint> visiblePoints) {this.visiblePoints = visiblePoints;}

    public void addVisiblePoint(VisitedPoint point){this.visiblePoints.add(point);}

    public Arc2D getArc() {return arc;}

    public void setArc(Arc2D arc) {this.arc = arc;}

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
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
