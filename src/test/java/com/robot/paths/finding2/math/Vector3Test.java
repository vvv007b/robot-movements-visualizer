package com.robot.paths.finding2.math;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mannimarco on 17/04/2017.
 */
public class Vector3Test {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sub() throws Exception {
        Vector3 vec1 = new Vector3(10, 10, 10);
        Vector3 vec2 = new Vector3(4, 5, 6);

        Vector3 vec3 = Vector3.sub(vec1, vec2);

        Assert.assertTrue(vec3.x == 6 & vec3.y == 5 & vec3.z == 4); //result should be correct
        Assert.assertTrue(vec1.x == 10 & vec1.y == 10 & vec1.z == 10); //condition of source vectors don't change
        Assert.assertTrue(vec2.x == 4 & vec2.y == 5 & vec2.z == 6);
    }

    @Test
    public void add() throws Exception {
        Vector3 vec1 = new Vector3(10, 10, 10);
        Vector3 vec2 = new Vector3(4, 5, 6);

        Vector3 vec3 = Vector3.add(vec1, vec2);

        Assert.assertTrue(vec3.x == 14 & vec3.y == 15 & vec3.z == 16); //result should be correct
        Assert.assertTrue(vec1.x == 10 & vec1.y == 10 & vec1.z == 10); //condition of source vectors don't change
        Assert.assertTrue(vec2.x == 4 & vec2.y == 5 & vec2.z == 6);

    }

    @Test
    public void mul() throws Exception {
        Vector3 vec1 = new Vector3(10, 10, 10);

        Vector3 vec2 = Vector3.mul(vec1, 5);

        Assert.assertTrue(vec2.x == 50 & vec2.y == 50 & vec2.z == 50); //result should be correct
        Assert.assertTrue(vec1.x == 10 & vec1.y == 10 & vec1.z == 10); //condition of source vectors don't change
    }

    @Test
    public void set() throws Exception {
    }

    @Test
    public void set1() throws Exception {
    }

    @Test
    public void mul1() throws Exception {
    }

    @Test
    public void sub1() throws Exception {
    }

    @Test
    public void add1() throws Exception {
    }

    @Test
    public void length() throws Exception {
        Vector3 vec1 = new Vector3(4, -3, 0);

        double length = vec1.length();

        Assert.assertTrue(length == 5); //result should be correct
        Assert.assertTrue(vec1.x == 4 & vec1.y == -3 & vec1.z == 0); //condition of source vectors don't change
    }

    @Test
    public void normalize() throws Exception {
        Vector3 vec = new Vector3(10, 5, 4);

        vec.normalize();

        Assert.assertTrue(vec.length() == 1);
    }

}