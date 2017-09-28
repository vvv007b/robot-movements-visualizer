package com.mcst.paths.finding2.reedsShepp;

import com.mcst.paths.finding2.math.CurvesMath;
import com.mcst.paths.finding2.math.Vector3;
import com.mcst.paths.finding2.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by mannimarco on 13/04/2017.
 * Generates all possible Reeds-Shepp paths
 */
public class PathsGenerator {
    //The 4 different circles we have that sits to the left/right of the start/goal
    public Vector3 startLeftCircle;
    public Vector3 startRightCircle;
    public Vector3 goalLeftCircle;
    public Vector3 goalRightCircle;

    //The position we want to drive from
    Vector3 startPos;
    //Heading in radians
    double startHeading;
    //The position we want to drive to
    Vector3 goalPos;
    //Heading in radians
    double goalHeading;

    //Store the final paths here
    TreeSet<Path> allReedsSheppPaths = new TreeSet<>();


    //The object that includes the curvesMath
    CurvesMath curvesMath;


    public PathsGenerator(double turningRadius) {
        curvesMath = new CurvesMath(turningRadius);
    }

    //reset before we can generate a paths
    void reset(Vector3 startPos, double startHeading, Vector3 goalPos, double goalHeading) {
        //Assign the latest positions and headings of the cars
        this.startPos = startPos;

        this.startHeading = startHeading;

        this.goalPos = goalPos;

        this.goalHeading = goalHeading;

        //Find the positions of the left/right circles of the start/goal
        positionLeftRightCircles();

        //Remove the old paths
        allReedsSheppPaths.clear();
    }


    //Position the left and right circles that are to the left/right of the target and the car
    void positionLeftRightCircles() {
        //Goal pos
        goalRightCircle = curvesMath.getRightCircleCenterPos(goalPos, goalHeading);

        goalLeftCircle = curvesMath.getLeftCircleCenterPos(goalPos, goalHeading);

        //Start pos
        startRightCircle = curvesMath.getRightCircleCenterPos(startPos, startHeading);

        startLeftCircle = curvesMath.getLeftCircleCenterPos(startPos, startHeading);
    }


    //Get the coordinate data of the shortest Reed Shepp path
    public List<Node> GetShortestReedSheppPath(Vector3 startPos,
                                               double startHeading,
                                               Vector3 goalPos,
                                               double goalHeading) {

        //reset before we can begin generating paths
        reset(startPos, startHeading, goalPos, goalHeading);

        //Generate all paths
        calculatePathLengths();

        //If we have any paths
        if (allReedsSheppPaths.size() > 0) {

            //Get the final coordinates of the path = the waypoints the car will follow
            generatePathCoordinates(allReedsSheppPaths.first());

            //Return the coordinates of the shortest path
            return allReedsSheppPaths.first().getPathCoordinates();
        } else {
            return null;
        }
    }


    //Generate all paths
    public TreeSet<Path> GetAllReedSheppPaths(Vector3 startPos, double startHeading, Vector3 goalPos, double goalHeading) {
        //reset before we can begin generating paths
        reset(startPos, startHeading, goalPos, goalHeading);

        //Generate all paths
        calculatePathLengths();
        allReedsSheppPaths.forEach(this::generatePathCoordinates);
        if (allReedsSheppPaths.size() > 0) {
            //Get the final coordinates of the path
//            for (int i = 0; i < allReedsSheppPaths.size(); i++) {
//                generatePathCoordinates(allReedsSheppPaths[i]);
//            }
            allReedsSheppPaths.forEach(this::generatePathCoordinates);


            //Debug.Log(allReedsSheppPaths[0].pathCoordinates.Count);

            return allReedsSheppPaths;
        } else {
            return null;
        }
    }


    //
    // Get the length of each path
    //

    //Get the path lengths of all paths and store them in a list together with other data
    void calculatePathLengths() {
        calculatePathLengthsCcc();

        calculatePathLengthsCsc();

        calculatePathLengthCcTurnCc();
    }


    //
    // CCC
    //
    void calculatePathLengthsCcc() {
        //
        // With the CCC paths, the distance between the start and goal have to be less than 4 * r
        //
        double maxDist = 4f * curvesMath.getTurningRadius();            //TODO: sqr removed, check it

        //The number of segments is always 3
        int segments = 3;

        //
        // RLR
        //
        if (Vector3.sub(startRightCircle, goalRightCircle).length() < maxDist) {
            List<Path> tmpPathList = new ArrayList<>();

            //Add all data that's the same for all 6 paths
            for (int i = 0; i < 5; i++) {
                Path path = new Path(segments);

                path.addIfTurningLeft(new boolean[]{false, true, false});

                path.addIfTurning(new boolean[]{true, true, true});

                tmpPathList.add(path);
            }


            //R+ L- R+
            tmpPathList.get(0).addIfReversing(new boolean[]{false, true, false});

            //R+ L+ R-
            tmpPathList.get(1).addIfReversing(new boolean[]{false, false, true});

            //R- L- R+
            tmpPathList.get(2).addIfReversing(new boolean[]{true, true, false});

            //R+ L- R-
            tmpPathList.get(3).addIfReversing(new boolean[]{false, true, true});

            //R- L+ R+
            tmpPathList.get(4).addIfReversing(new boolean[]{true, false, false});


            //Get all path lengths
            for (int i = 0; i < tmpPathList.size(); i++) {
                //Unsure if all should be true but gives better result because no have the same length if all are true
                getCccLength(startRightCircle, goalRightCircle, true, tmpPathList.get(i));
            }
        }


        //
        // LRL
        //
        if (Vector3.sub(startLeftCircle, goalLeftCircle).length() < maxDist) {
            List<Path> tmpPathList = new ArrayList<>();

            //Add all data that's the same for all 6 paths
            for (int i = 0; i < 5; i++) {
                Path pathData = new Path(segments);

                pathData.addIfTurningLeft(new boolean[]{true, false, true});

                pathData.addIfTurning(new boolean[]{true, true, true});

                tmpPathList.add(pathData);
            }


            //L+ R- L+
            tmpPathList.get(0).addIfReversing(new boolean[]{false, true, false});

            //L+ R+ L-
            tmpPathList.get(1).addIfReversing(new boolean[]{false, false, true});

            //L- R- L+
            tmpPathList.get(2).addIfReversing(new boolean[]{true, true, false});

            //L+ R- L-
            tmpPathList.get(3).addIfReversing(new boolean[]{false, true, true});

            //L- R+ L+
            tmpPathList.get(4).addIfReversing(new boolean[]{true, false, false});

            for (int i = 0; i < tmpPathList.size(); i++) {
                getCccLength(startLeftCircle, goalLeftCircle, false, tmpPathList.get(i));
            }
        }
    }


    //
    // CSC
    //
    void calculatePathLengthsCsc() {
        boolean isOuterTangent = false;
        boolean isBottomTangent = false;

        int segments = 3;

        Path pathData = null;

        //
        //LSL and RSR is only working if the circles don't have the same position
        //

        //LSL
        if (!startLeftCircle.equals(goalLeftCircle)) {
            isOuterTangent = true;


            //L+ S+ L+
            isBottomTangent = true;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{true, false, true});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{false, false, false});

            getCscLength(startLeftCircle, goalLeftCircle, isOuterTangent, isBottomTangent, pathData);


            //L- S- L-
            isBottomTangent = false;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{true, false, true});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{true, true, true});

            getCscLength(startLeftCircle, goalLeftCircle, isOuterTangent, isBottomTangent, pathData);
        }


        //RSR
        if (!startRightCircle.equals(goalRightCircle)) {
            isOuterTangent = true;


            //R+ S+ R+
            isBottomTangent = false;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{false, false, false});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{false, false, false});

            getCscLength(startRightCircle, goalRightCircle, isOuterTangent, isBottomTangent, pathData);


            //R- S- R-
            isBottomTangent = true;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{false, false, false});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{true, true, true});

            getCscLength(startRightCircle, goalRightCircle, isOuterTangent, isBottomTangent, pathData);
        }


        //
        // LSR and RSL is only working of the circles don't intersect
        //
        double comparison = curvesMath.getTurningRadius() * 2f;

        //LSR
        if (Vector3.sub(startLeftCircle, goalRightCircle).length() > comparison) {
            isOuterTangent = false;


            //L+ S+ R+
            isBottomTangent = true;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{true, false, false});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{false, false, false});

            getCscLength(startLeftCircle, goalRightCircle, isOuterTangent, isBottomTangent, pathData);


            //L- S- R-
            isBottomTangent = false;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{true, false, false});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{true, true, true});

            getCscLength(startLeftCircle, goalRightCircle, isOuterTangent, isBottomTangent, pathData);
        }


        //RSL
        if (Vector3.sub(startRightCircle, goalLeftCircle).length() > comparison) {
            isOuterTangent = false;


            //R+ S+ L+
            isBottomTangent = false;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{false, false, true});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{false, false, false});

            getCscLength(startRightCircle, goalLeftCircle, isOuterTangent, isBottomTangent, pathData);


            //R- S- L-
            isBottomTangent = true;

            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{false, false, true});

            pathData.addIfTurning(new boolean[]{true, false, true});

            pathData.addIfReversing(new boolean[]{true, true, true});

            getCscLength(startRightCircle, goalLeftCircle, isOuterTangent, isBottomTangent, pathData);
        }
    }


    //
    // CC turn CC
    //
    void calculatePathLengthCcTurnCc() {
        //Is only valid if the two circles intersect?
        double comparison = curvesMath.getTurningRadius() * 2f;

        //Always 4 segments
        int segments = 4;

        boolean isBottom = false;

        Path pathData = null;

        //RLRL
        if (Vector3.sub(startRightCircle, goalLeftCircle).length() < comparison) {
            //R+ L+ R- L-
            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{false, true, false, true});

            pathData.addIfTurning(new boolean[]{true, true, true, true});

            pathData.addIfReversing(new boolean[]{false, false, true, true});

            isBottom = false;

            getCcTurnCcLength(startRightCircle, goalLeftCircle, isBottom, pathData);


            //R- L- R+ L+
            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{false, true, false, true});

            pathData.addIfTurning(new boolean[]{true, true, true, true});

            pathData.addIfReversing(new boolean[]{true, true, false, false});

            isBottom = false;

            getCcTurnCcLength(startRightCircle, goalLeftCircle, isBottom, pathData);
        }


        //LRLR
        if (Vector3.sub(startLeftCircle, goalRightCircle).length() < comparison) {
            //L+ R+ L- R-
            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{true, false, true, false});

            pathData.addIfTurning(new boolean[]{true, true, true, true});

            pathData.addIfReversing(new boolean[]{false, false, true, true});

            isBottom = true;

            getCcTurnCcLength(startLeftCircle, goalRightCircle, isBottom, pathData);


            //L- R- L+ R+
            pathData = new Path(segments);

            pathData.addIfTurningLeft(new boolean[]{true, false, true, false});

            pathData.addIfTurning(new boolean[]{true, true, true, true});

            pathData.addIfReversing(new boolean[]{true, true, false, false});

            //Should maybe be false?
            isBottom = true;

            getCcTurnCcLength(startLeftCircle, goalRightCircle, isBottom, pathData);
        }
    }


    //One path CC turn CC
    void getCcTurnCcLength(Vector3 startCircle, Vector3 goalCircle, boolean isBottom, Path pathData) {
        //Find the 3 tangent points and the 2 middle circle positions
        curvesMath.ccTurnCc(
                startCircle,
                goalCircle,
                isBottom,
                pathData);

        //Calculate the total length of each path
        double length1 = curvesMath.getArcLength(
                startCircle,
                startPos,
                pathData.getStartTangent(),
                pathData.getSegmentsList().get(0));

        double length2 = curvesMath.getArcLength(
                pathData.getMiddleCircleCoordinate(),
                pathData.getStartTangent(),
                pathData.getMiddleTangent(),
                pathData.getSegmentsList().get(1));

        double length3 = curvesMath.getArcLength(
                pathData.getMiddleCircleCoordinate2(),
                pathData.getMiddleTangent(),
                pathData.getGoalTangent(),
                pathData.getSegmentsList().get(2));

        double length4 = curvesMath.getArcLength(
                goalCircle,
                pathData.getGoalTangent(),
                goalPos,
                pathData.getSegmentsList().get(3));

        //Save the lengths
        pathData.addPathLengths(new double[]{length1, length2, length3, length4});

        //Add the final path
        allReedsSheppPaths.add(pathData);
    }


    //One path CCC
    void getCccLength(Vector3 startCircle, Vector3 goalCircle, boolean isRightToRight, Path path) {
        //Find both tangent positions and the position of the 3rd circles
        curvesMath.getCccTangents(
                startCircle,
                goalCircle,
                isRightToRight,
                path);

        //Calculate the total length of this path
        double length1 = curvesMath.getArcLength(
                startCircle,
                startPos,
                path.getStartTangent(),
                path.getSegmentsList().get(0));

        double length2 = curvesMath.getArcLength(
                path.getMiddleCircleCoordinate(),
                path.getStartTangent(),
                path.getGoalTangent(),
                path.getSegmentsList().get(1));

        double length3 = curvesMath.getArcLength(
                goalCircle,
                path.getGoalTangent(),
                goalPos,
                path.getSegmentsList().get(2));

        //Save the data
        path.addPathLengths(new double[]{length1, length2, length3});

        //Add the path to the collection of all paths
        allReedsSheppPaths.add(path);
    }


    //One path CSC
    void getCscLength(
            Vector3 startCircle,
            Vector3 goalCircle,
            boolean isOuterTangent,
            boolean isBottomTangent,
            Path path) {
        //Find both tangent positions
        if (isOuterTangent) {
            curvesMath.LSLorRSR(startCircle, goalCircle, isBottomTangent, path);
        } else {
            curvesMath.RSLorLSR(startCircle, goalCircle, isBottomTangent, path);
        }


        //Calculate the total length of this path
        double length1 = curvesMath.getArcLength(
                startCircle,
                startPos,
                path.getStartTangent(),
                path.getSegmentsList().get(0));

        double length2 = Vector3.sub(path.getStartTangent(), path.getGoalTangent()).length();

        double length3 = curvesMath.getArcLength(
                goalCircle,
                path.getGoalTangent(),
                goalPos,
                path.getSegmentsList().get(2));

        //Save the data
        path.addPathLengths(new double[]{length1, length2, length3});

        //Add the path to the collection of all paths
        allReedsSheppPaths.add(path);
    }


    //
    // Generate the final path from the tangent points
    //

    //When we have found the shortest path we need to get the individual coordinates
    //so we can travel along the path
    void generatePaths(List<Path> pathDataList) {
        for (int i = 0; i < pathDataList.size(); i++) {
            generatePathCoordinates(pathDataList.get(i));
        }
    }


    //Find the coordinates of the entire path from the 2 tangents
    void generatePathCoordinates(Path pathData) {
        //Store the waypoints of the final path here
        List<Node> finalPath = new ArrayList<>();

        //Start position of the car
        Vector3 currentPos = startPos;
        //Start heading of the car
        double theta = startHeading;

        //Loop through all segments and generate the waypoints
        for (int i = 0; i < pathData.getSegmentsList().size(); i++) {
            curvesMath.addCoordinatesToPath(
                    currentPos,
                    theta,
                    finalPath,
                    pathData.getSegmentsList().get(i));
        }

        //Add the final goal coordinate
        Vector3 finalPos = new Vector3(goalPos.x, currentPos.y, goalPos.z);

        Node newNode = new Node();

        newNode.setLocation(finalPos.x, finalPos.z);
        newNode.setAzimuth(goalHeading);

        if (pathData.getSegmentsList().get(pathData.getSegmentsList().size() - 1).isReversing()) {
            newNode.setReversing(true);
        }

        finalPath.add(newNode);

        //Save the final path in the path data
        pathData.setPathCoordinates(finalPath);
    }


}
