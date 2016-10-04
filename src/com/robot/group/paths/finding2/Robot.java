package com.robot.group.paths.finding2;

import java.awt.geom.Point2D;

/**
 * Created by bocharov_n on 04.10.16.
 */
public class Robot {
    private Point2D.Double position;

    public Robot(){
        position = new Point2D.Double(-1000, -1000);
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }
}
