import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by bocharov_n on 13.11.15.
 */
public class Trajectory {
    private List<KeyPoint> keyPointList;
    private List<Point2D> pointList;
    private JLabel trajectoryLabel;
    private Color color;

    public Trajectory(){
        keyPointList = new ArrayList<>();
        pointList = new ArrayList<>();
        trajectoryLabel = new JLabel();
        Random rand = new Random();
        Float r = rand.nextFloat(),
                g = rand.nextFloat(),
                b = rand.nextFloat();
        color = new Color(r, g, b);
        System.out.println(r + " " + g + " " + b);
        System.out.println("new Trajectory created");
    }

    public void generateConnections(){
        if (!keyPointList.isEmpty()){
            int minX = Integer.MAX_VALUE,
                minY = Integer.MAX_VALUE,
                maxX = Integer.MIN_VALUE,
                maxY = Integer.MIN_VALUE;
            for(KeyPoint kp : keyPointList){
                int x = kp.getx(), y = kp.gety();
    //            if (x < minX) minX = x;
                if (x > maxX) maxX = x;
    //            if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
            BufferedImage connectionsImage = new BufferedImage(640, 570, BufferedImage.TYPE_INT_ARGB);
    //        BufferedImage connectionsImage = new BufferedImage();
            Graphics2D g2d = connectionsImage.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, connectionsImage.getWidth(), connectionsImage.getHeight());
            g2d.setComposite(AlphaComposite.Src);
            g2d.setColor(color);
            Stroke stroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            for(int i = 0; i < keyPointList.size() - 1; i++){
                g2d.drawLine(keyPointList.get(i).getx(), keyPointList.get(i).gety(), keyPointList.get(i + 1).getx(), keyPointList.get(i + 1).gety());
            }
            g2d.setStroke(stroke);
            trajectoryLabel.setIcon(new ImageIcon(connectionsImage));
        }
    }

    public void calculatePointList(){
        pointList.clear();
        for(int k = 0; k < this.keyPointList.size() - 1; k++) {
            KeyPoint    kp1 = this.keyPointList.get(k),
                        kp2 = this.keyPointList.get(k + 1);
            int     minX = kp1.getx() > kp2.getx() ? kp2.getx() : kp1.getx(),
                    minY = kp1.gety() > kp2.gety() ? kp2.gety() : kp1.gety(),
                    maxX = kp1.getx() > kp2.getx() ? kp1.getx() : kp2.getx(),
                    maxY = kp1.gety() > kp2.gety() ? kp1.gety() : kp2.gety();
            if(maxX - minX >= maxY - minY){
                int x1, x2, y1, y2;
                if (kp1.getx() > kp2.getx()){
                    x1 = kp2.getx();
                    x2 = kp1.getx();
                    y1 = kp2.gety();
                    y2 = kp1.gety();
                }
                else{
                    x1 = kp1.getx();
                    x2 = kp2.getx();
                    y1 = kp1.gety();
                    y2 = kp2.gety();
                }
//                System.out.println(x1+ " " + x2);
                for(int i = x1; i <= x2; i++){
    //                double  a = 1/((double)x2 - (double)x1),
    //                        b = 1/((double)y1 - (double)y2),
    //                        c = x1 / ((double)x1- (double)x2) + y1/ ((double)y2 - (double)y1);
                    double  x = i,
                            y = y1 == y2 ? y1 : calculateYbyX(x1, x2, y1, y2, i);
//                    System.out.println(x + " " + y);
                    pointList.add(new Point2D.Double(x, y));
                }
            }
            else{
                int x1, x2, y1, y2;
                if (kp1.gety() > kp2.gety()){
                    x1 = kp2.getx();
                    x2 = kp1.getx();
                    y1 = kp2.gety();
                    y2 = kp1.gety();
                }
                else{
                    x1 = kp1.getx();
                    x2 = kp2.getx();
                    y1 = kp1.gety();
                    y2 = kp2.gety();
                }
                for(int i = y1; i <= y2; i++){
                    double  y = i,
                            x = x1 == x2 ? x1 : calculateXbyY(x1, x2, y1, y2, i);
                    pointList.add(new Point2D.Double(x, y));
                }
            }
//            for(int j = minY; j <= maxY; j++){
//
//            }

        }

    }

    private double calculateYbyX(int x1, int x2, int y1, int y2, int x){
        double  a = 1/((double)x2 - (double)x1),
                b = 1/((double)y1 - (double)y2),
                c = x1 / ((double)x1 - (double)x2) + y1 / ((double)y2 - (double)y1);
        return -(c + a * x) / b;
    }

    private double calculateXbyY(int x1, int x2, int y1, int y2, int y){
        double  a = 1/((double)x2 - (double)x1),
                b = 1/((double)y1 - (double)y2),
                c = x1 / ((double)x1 - (double)x2) + y1 / ((double)y2 - (double)y1);
        return -(c + b * y) / a;
    }

    public List<Point2D> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point2D> pointList) {
        this.pointList = pointList;
    }

    public List<KeyPoint> getKeyPointList() {
        return keyPointList;
    }

    public void setKeyPointList(List<KeyPoint> keyPointList) {
        this.keyPointList = keyPointList;
    }

    public JLabel getTrajectoryLabel() {
        return trajectoryLabel;
    }

    public void setTrajectoryLabel(JLabel trajectoryLabel) {
        this.trajectoryLabel = trajectoryLabel;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addKeyPoint(KeyPoint keyPoint){
        this.keyPointList.add(keyPoint);
    }
}
