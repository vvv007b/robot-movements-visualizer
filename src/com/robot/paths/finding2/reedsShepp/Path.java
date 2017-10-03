package main.robot.paths.finding2.reedsShepp;

import main.robot.paths.finding2.math.Vector3;
import main.robot.paths.finding2.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mannimarco on 13/04/2017.
 * One Reeds-Shepp path
 */
public class Path implements Comparable<Path> {

    //The final path data, with waypoint coordinates,
    //headings for collision detection, and if reversing
    private List<Node> pathCoordinates;
    //Circle pos - not always needed
    private Vector3 middleCircleCoordinate;
    private Vector3 middleCircleCoordinate2;

    //Save these for debugging
    //Tangents - not always 3
    private Vector3 startTangent;        //TODO: are they necessary?
    private Vector3 middleTangent;
    private Vector3 goalTangent;

    //The total length of this path so we can find the shortest of all paths
    private float totalLength;
    //Data that belongs to each segment of this path
    private List<Segment> segmentsList;


    //Init
    public Path(int segments) {
        this.segmentsList = new ArrayList<>();

        //Add all the segments we will need
        for (int i = 0; i < segments; i++) {
            segmentsList.add(new Segment());
        }
    }


    public void addIfTurningLeft(boolean[] isTurningLeftArray) {
        for (int i = 0; i < isTurningLeftArray.length; i++) {
            segmentsList.get(i).setTurningLeft(isTurningLeftArray[i]);
        }
    }


    public void addIfReversing(boolean[] isReversingArray) {
        for (int i = 0; i < isReversingArray.length; i++) {
            segmentsList.get(i).setReversing(isReversingArray[i]);
        }
    }


    public void addIfTurning(boolean[] isTurningArray) {
        for (int i = 0; i < isTurningArray.length; i++) {
            segmentsList.get(i).setTurning(isTurningArray[i]);
        }
    }


    //Add the lengths of each segment and calculate the length of the entire path
    public void addPathLengths(double[] lengthsArray) {
        totalLength = 0;

        for (int i = 0; i < segmentsList.size(); i++) {
            segmentsList.get(i).setPathLength(lengthsArray[i]);

            //Calculate the total length of this path
            totalLength += lengthsArray[i];
        }
    }

    public List<Node> getPathCoordinates() {
        return pathCoordinates;
    }

    public Vector3 getMiddleCircleCoordinate() {
        return middleCircleCoordinate;
    }

    public Vector3 getMiddleCircleCoordinate2() {
        return middleCircleCoordinate2;
    }

    public void setMiddleCircleCoordinate(Vector3 middleCircleCoordinate) {
        this.middleCircleCoordinate = middleCircleCoordinate;
    }

    public void setMiddleCircleCoordinate2(Vector3 middleCircleCoordinate2) {
        this.middleCircleCoordinate2 = middleCircleCoordinate2;
    }

    public void setStartTangent(Vector3 startTangent) {
        this.startTangent = startTangent;
    }

    public void setMiddleTangent(Vector3 middleTangent) {
        this.middleTangent = middleTangent;
    }

    public void setGoalTangent(Vector3 goalTangent) {
        this.goalTangent = goalTangent;
    }

    public Vector3 getStartTangent() {
        return startTangent;
    }

    public Vector3 getMiddleTangent() {
        return middleTangent;
    }

    public Vector3 getGoalTangent() {
        return goalTangent;
    }

    public float getTotalLength() {
        return totalLength;
    }

    public List<Segment> getSegmentsList() {
        return segmentsList;
    }

    @Override
    public int compareTo(Path path) {
        double compareTotalLength = path.getTotalLength();
        return (int) (this.totalLength - compareTotalLength);
    }

    public void setPathCoordinates(List<Node> pathCoordinates) {
        this.pathCoordinates = pathCoordinates;
    }
}
