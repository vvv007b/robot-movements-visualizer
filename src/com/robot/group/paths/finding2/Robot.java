package com.robot.group.paths.finding2;

import java.awt.geom.Point2D;

/***
 * Created by bocharov_n on 04.10.16.
 */
public class Robot {
    private Node position;
    private Node finish;

    public Robot() {
        position = new Node(new Point2D.Double(-1000, -1000), 0);
        finish = new Node(new Point2D.Double(-1000, -1000), 0);
    }

    public Node getPosition() {
        return position;
    }

    public void setPosition(Node position) {
        this.position = position;
    }

    public Node getFinish() {
        return finish;
    }

    public void setFinish(Node finish) {
        this.finish = finish;
    }

}