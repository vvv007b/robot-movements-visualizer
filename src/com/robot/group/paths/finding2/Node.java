package com.robot.group.paths.finding2;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Created by bocharov_n on 01.02.17.
 */
public class Node extends Point2D.Double {
    private double azimuth;
    private double weight;

    public Node(Point2D.Double point, double azimuth) {
        super(point.x, point.y);
        this.azimuth = azimuth;
        this.weight = 0;
    }

    public Node(Point2D.Double point, double azimuth, double weight) {
        super(point.x, point.y);
        this.azimuth = azimuth;
        this.weight = weight;
    }

    public Point getPoint() {
        return new Point((int) this.x, (int) this.y);
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }
}
