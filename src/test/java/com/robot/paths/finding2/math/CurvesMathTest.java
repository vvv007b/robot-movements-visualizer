package com.robot.paths.finding2.math;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mannimarco on 17/04/2017.
 */
public class CurvesMathTest {

    CurvesMath curvesMath;
    @Before
    public void setUp() throws Exception {
        curvesMath = new CurvesMath(10);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getRightCircleCenterPos() throws Exception {
        Vector3 pos = new Vector3(0,0,0);
        Vector3 center = curvesMath.getRightCircleCenterPos(pos, 0);
        System.out.println(center);
        //TODO
    }

    @Test
    public void getLeftCircleCenterPos() throws Exception {
        Vector3 pos = new Vector3(0,0,0);
        Vector3 center = curvesMath.getLeftCircleCenterPos(pos, 0);
        System.out.println(center);
        //TODO
    }

    @Test
    public void getCccTangents() throws Exception {
    }

    @Test
    public void getArcLength() throws Exception {
    }

    @Test
    public void isTurningLeft() throws Exception {
    }

    @Test
    public void ccTurnCc() throws Exception {
    }

    @Test
    public void LSLorRSR() throws Exception {
    }

    @Test
    public void RSLorLSR() throws Exception {
    }

    @Test
    public void addCoordinatesToPath() throws Exception {
    }

}