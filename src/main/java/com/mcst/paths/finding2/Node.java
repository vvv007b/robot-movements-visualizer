package com.mcst.paths.finding2;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Created by bocharov_n on 01.02.17.
 * Node for search path algorithm
 */
public class Node extends Point2D.Double implements Comparable<Node> {
    private double azimuth = 0;
    //The total cost with heuristic, passability and cost
    private double totalCost = 0;
    //Passability weight
    private double weight = 0;
    //The cost to this node
    private double cost = 0;

    private boolean isReversing;

    public Node(){}

    public Node(Point2D.Double point, double azimuth) {
        super(point.x, point.y);
        this.azimuth = azimuth;
    }

    public Node(double x, double y, double totalCost) {
        super(x, y);
        this.totalCost = totalCost;
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

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isReversing() {
        return isReversing;
    }

    public void setReversing(boolean reversing) {
        isReversing = reversing;
    }

    @Override
    public int compareTo(Node node) {
        double compareTotalCost = node.getTotalCost();
        return (int) (this.totalCost - compareTotalCost);
    }

    @Override
    public String toString() {
        return "Node at (" + x + "," + y + "," + azimuth + ") with total cost " + totalCost;
    }
}
