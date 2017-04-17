package com.robot.paths.finding2.math;

import com.robot.paths.finding2.Node;
import com.robot.paths.finding2.math.Vector3;
import com.robot.paths.finding2.reedsShepp.Path;
import com.robot.paths.finding2.reedsShepp.Segment;

import java.util.List;

/**
 * Created by mannimarco on 13/04/2017.
 * Math for ReedsSheep paths
 */
public class CurvesMath {
    private double turningRadius;

    public CurvesMath(double turningRadius) {
        this.turningRadius = turningRadius;
    }

    //Right circle
    public Vector3 getRightCircleCenterPos(Vector3 carPos, double heading) {
        Vector3 rightCirclePos = new Vector3(0, 0, 0);

        //90 degrees to the right of the car
        rightCirclePos.x = carPos.x + turningRadius * Math.sin(heading + (Math.PI / 2f));

        rightCirclePos.z = carPos.z + turningRadius * Math.cos(heading + (Math.PI / 2f));

        return rightCirclePos;
    }

    //Left circle
    public Vector3 getLeftCircleCenterPos(Vector3 carPos, double heading) {
        Vector3 rightCirclePos = new Vector3(0, 0, 0);

        //90 degrees to the left of the car
        rightCirclePos.x = carPos.x + turningRadius * Math.sin(heading - (Math.PI / 2f));

        rightCirclePos.z = carPos.z + turningRadius * Math.cos(heading - (Math.PI / 2f));

        return rightCirclePos;
    }

    //Get the CCC tangent points
    public void getCccTangents(
            Vector3 startCircle,
            Vector3 goalCircle,
            boolean isRlr,
            Path path) {
        //The distance between the circles
        double distance = Vector3.sub(startCircle, goalCircle).length();

        //The angle between the goal and the new circle we create
        double theta = Math.acos(distance / (4f * turningRadius));

        //But we need to modify the angle theta if the circles are not on the same line
        Vector3 vector1 = Vector3.sub(goalCircle, startCircle);

        //Different depending on if we calculate LRL or RLR
        if (!isRlr) {
            theta = Math.atan2(vector1.z, vector1.x) + theta;
        } else {
            theta = Math.atan2(vector1.z, vector1.x) - theta;
        }


        //Calculate the position of the third circle
        double x = startCircle.x + 2f * turningRadius * Math.cos(theta);
        double y = startCircle.y;
        double z = startCircle.z + 2f * turningRadius * Math.sin(theta);

        Vector3 middleCircleCenter = new Vector3(x, y, z);


        //Calculate the tangent points
        Vector3 vector2 = Vector3.sub(startCircle, middleCircleCenter);
        Vector3 vector3 = Vector3.sub(goalCircle, middleCircleCenter);
        vector2.normalize();
        vector3.normalize();//TODO: why?

        Vector3 startTangent = Vector3.add(middleCircleCenter, vector2.mul(turningRadius));
        Vector3 goalTangent = Vector3.add(middleCircleCenter, vector3.mul(turningRadius));


        //Save everything
        path.setMiddleCircleCoordinate(middleCircleCenter);

        path.setStartTangent(startTangent);
        path.setGoalTangent(goalTangent);
    }

    //Calculate the length of an circle arc depending on which direction we are driving
    //isLeftCircle should be true if we are reversing in a right circle
    public double getArcLength(
            Vector3 circleCenterPos,
            Vector3 startPos,
            Vector3 goalPos,
            Segment segment) {
        //To get the arc length of each path we need to know which way we are turning
        boolean isLeftCircle = isTurningLeft(segment.isTurningLeft(), segment.isReversing());

        Vector3 vector1 = Vector3.sub(startPos, circleCenterPos);
        Vector3 vector2 = Vector3.sub(goalPos, circleCenterPos);

        double theta = Math.atan2(vector2.z, vector2.x) - Math.atan2(vector1.z, vector1.x);

        if (theta < 0f && isLeftCircle) {
            theta += 2f * Math.PI;
        } else if (theta > 0 && !isLeftCircle) {
            theta -= 2f * Math.PI;
        }

        return Math.abs(theta * turningRadius);         //TODO: why radians multiplying by meters? CHECK IT
    }

    boolean isTurningLeft(boolean isTurningLeft, boolean isReversing) {
        //Forward in a right circle is false
        //Reversing in a right circle is true
        //Reversing in a left circle is false
        //Forward in a left circle if true

        //L+
        if (isTurningLeft && !isReversing) {
            return isTurningLeft = true;
        }
        //L-
        if (isTurningLeft && isReversing) {
            return isTurningLeft = false;
        }
        //R+
        if (!isTurningLeft && !isReversing) {
            return isTurningLeft = false;
        }
        //R-
        if (!isTurningLeft && isReversing) {
            return isTurningLeft = true;
        }

        return isTurningLeft;
    }


    //TODO: niht comprende
    //CC|CC
    public void ccTurnCc(
            Vector3 startCircle,
            Vector3 goalCircle,
            boolean isBottom,
            Path pathData) {
        //The distance between the circles
        double dist = Vector3.sub(startCircle, goalCircle).length();

        double rad = turningRadius;

        double a = Math.sqrt((2f * rad * 2f * rad) - ((rad - (dist * 0.5f)) * (rad - (dist * 0.5f))));

        //The angle we need to find the first circle center
        double theta = Math.acos(a / (2f * rad)) + (Math.PI / 2);

        //Need to modify theta if the circles are not on the same height (z)
        double atan2 = Math.atan2(goalCircle.z - startCircle.z, goalCircle.x - startCircle.x);

        if (isBottom) {
            theta = atan2 - theta;
        } else {
            theta = atan2 + theta;
        }

        //Center of the circle A
        double Ax = startCircle.x + 2f * rad * Math.cos(theta);
        double Az = startCircle.z + 2f * rad * Math.sin(theta);

        Vector3 circleAPos = new Vector3(Ax, 0f, Az);

        //The direction between the start circle and the goal circle
        //is the same as the direction between the outer circles
        Vector3 dirVec = Vector3.sub(goalCircle, startCircle);
        dirVec.normalize();

        //And the distance between the outer circles is 2r
        //So the position of the second circle is
        Vector3 circleBPos = Vector3.add(circleAPos, Vector3.mul(dirVec, 2f * rad));

        //Now we can calculate the 3 tangent positions
        Vector3 dirVecA = Vector3.sub(startCircle, circleAPos);
        dirVecA.normalize();
        Vector3 dirVecB = Vector3.sub(goalCircle, circleBPos);
        dirVecB.normalize();

        Vector3 startTangent = Vector3.add(circleAPos, Vector3.mul(dirVecA, rad));

        Vector3 middleTangent = Vector3.add(circleAPos, Vector3.mul(dirVec, rad));

        Vector3 goalTangent = Vector3.add(circleBPos, Vector3.mul(dirVecB, rad));

        //Save everything
        pathData.setStartTangent(startTangent);
        pathData.setMiddleTangent(middleTangent);
        pathData.setGoalTangent(goalTangent);

        pathData.setMiddleCircleCoordinate(circleAPos);
        pathData.setMiddleCircleCoordinate2(circleBPos);
    }


    //Outer tangent (LSL and RSR)
    public void LSLorRSR(
            Vector3 startCircle,
            Vector3 goalCircle,
            boolean isBottom,
            Path path) {
        //The angle to the first tangent coordinate is always 90 degrees if the both circles have the same radius
        double theta = Math.PI / 2;

        //Need to modify theta if the circles are not on the same height (z)
        theta += Math.atan2(goalCircle.z - startCircle.z, goalCircle.x - startCircle.x);

        //Add pi to get the "bottom" coordinate which is on the opposite side (180 degrees = pi)
        if (isBottom) {
            theta += Math.PI;
        }

        //The coordinates of the first tangent points
        double xT1 = startCircle.x + turningRadius * Math.cos(theta);
        double zT1 = startCircle.z + turningRadius * Math.sin(theta);

        //To get the second coordinate we need a direction
        //This direction is the same as the direction between the center pos of the circles
        Vector3 dirVec = Vector3.sub(goalCircle, startCircle);

        double xT2 = xT1 + dirVec.x;
        double zT2 = zT1 + dirVec.z;

        //The final coordinates of the tangent lines
        path.setStartTangent(new Vector3(xT1, 0.1f, zT1));

        path.setGoalTangent(new Vector3(xT2, 0.1f, zT2));
    }


    //Inner tangent (RSL and LSR)
    public void RSLorLSR(
            Vector3 startCircle,
            Vector3 goalCircle,
            boolean isBottom,
            Path path) {
        //Find the distance between the circles
        double D = Vector3.sub(startCircle, goalCircle).length();

        double R = turningRadius;

        //If the circles have the same radius we can use cosine and not the law of cosines 
        //to calculate the angle to the first tangent coordinate 
        double theta = Math.acos((2f * R) / D);

        //If the circles is LSR, then the first tangent pos is on the other side of the center line
        if (isBottom) {
            theta *= -1f;
        }

        //Need to modify theta if the circles are not on the same height            
        theta += Math.atan2(goalCircle.z - startCircle.z, goalCircle.x - startCircle.x);

        //The coordinates of the first tangent point
        double xT1 = startCircle.x + turningRadius * Math.cos(theta);
        double zT1 = startCircle.z + turningRadius * Math.sin(theta);

        //To get the second tangent coordinate we need the direction of the tangent
        //To get the direction we move up 2 circle radius and end up at this coordinate
        double xT1_tmp = startCircle.x + 2f * turningRadius * Math.cos(theta);
        double zT1_tmp = startCircle.z + 2f * turningRadius * Math.sin(theta);

        //The direction is between the new coordinate and the center of the target circle
        Vector3 dirVec = Vector3.sub(goalCircle, new Vector3(xT1_tmp, 0f, zT1_tmp));

        //The coordinates of the second tangent point is the 
        double xT2 = xT1 + dirVec.x;
        double zT2 = zT1 + dirVec.z;

        //The final coordinates of the tangent lines
        path.setStartTangent(new Vector3(xT1, 0.1f, zT1));

        path.setGoalTangent(new Vector3(xT2, 0.1f, zT2));
    }

    //Loops through segments of a path and add new coordinates to the final path
    public void addCoordinatesToPath(
            Vector3 currentPos,
            double theta,
            List<Node> finalPath,
            Segment segment) {
        //How far we are driving each update, the accuracy will improve if we lower the driveDistance
        //But not too low because then the path will be less accurate because of rounding errors
        double driveDistance = 0.02f;

        //How many segments has this line?
        int segments = (int) (segment.getPathLength() / driveDistance);     //TODO: check FloorToInt correction


        //Always add the first position manually
        Node newNode = new Node();

        newNode.setLocation(currentPos.x, currentPos.z);  //TODO: x & z??
        newNode.setAzimuth(theta);

        if (segment.isReversing()) {
            newNode.setReversing(true);
        }

        finalPath.add(newNode);


        //Loop through all segments
        //i = 1 because we already added the first coordinate
        for (int i = 1; i < segments; i++) {
            //Can we improve the accuracy with Heuns method?
            //Probably not because speed is constant


            //Update the position
            if (segment.isReversing()) {
                currentPos.x -= driveDistance * Math.sin(theta);
                currentPos.z -= driveDistance * Math.cos(theta);
            } else {
                currentPos.x += driveDistance * Math.sin(theta);
                currentPos.z += driveDistance * Math.cos(theta);
            }


            //Don't update the heading if we are driving straight
            if (segment.isTurning()) {
                //Which way are we turning?
                double turnParameter = -1f;

                if (!segment.isTurningLeft()) {
                    turnParameter = 1f;
                }

                if (segment.isReversing()) {
                    turnParameter *= -1f;
                }

                //Update the heading
                theta += (driveDistance / turningRadius) * turnParameter;
            }


            //Add the new position and heading to the final path
            //Don't add all segments because 0.02 m per segment is too detailed
            //The real drive distance in the Hybrid A* tree is 1.05, so 52 = 1.05 / 0.02
            if (i % 52 == 0) {
                newNode = new Node();

                newNode.setLocation(currentPos.x, currentPos.z);
                newNode.setAzimuth(theta);              //toDO: check degrees or radians?

                if (segment.isReversing()) {
                    newNode.setReversing(true);
                }

                finalPath.add(newNode);
            }
        }
    }


    public double getTurningRadius() {
        return turningRadius;
    }

    public void setTurningRadius(double turningRadius) {
        this.turningRadius = turningRadius;
    }
}
