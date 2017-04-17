package com.robot.paths.finding2.math;

/**
 * A 3-dimensional, double-precision, floating-point vector.
 */

public class Vector3 {

    /**
     * The x coordinate.
     */
    public double x;

    /**
     * The y coordinate.
     */
    public double y;

    /**
     * The z coordinate.
     */
    public double z;

    public Vector3() {
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 vec) {
        set(vec);
    }

    /**
     * Returns new Vector3 which are sub
     * of vectors t1 and t2 (this = t1 - t2).
     *
     * @param t1 the first vector
     * @param t2 the second vector
     */
    public static Vector3 sub(Vector3 t1, Vector3 t2) {
        Vector3 vector3 = new Vector3();
        vector3.x = t1.x - t2.x;
        vector3.y = t1.y - t2.y;
        vector3.z = t1.z - t2.z;
        return vector3;
    }

    /**
     * Returns new Vector3 which are sum
     * of vectors t1 and t2 (this = t1 - t2).
     *
     * @param t1 the first vector
     * @param t2 the second vector
     */
    public static Vector3 add(Vector3 t1, Vector3 t2) {
        Vector3 vector3 = new Vector3();
        vector3.x = t1.x + t2.x;
        vector3.y = t1.y + t2.y;
        vector3.z = t1.z + t2.z;
        return vector3;
    }

    /**
     * Returns new Vector3 which are multiplication
     * of vectors t and value value (this = t1 * value).
     *
     * @param t1 the first vector
     * @param value  the value
     */
    public static Vector3 mul(Vector3 t1, double value) {
        Vector3 vector3 = new Vector3();
        vector3.x = t1.x * value;
        vector3.y = t1.y * value;
        vector3.z = t1.z * value;
        return vector3;
    }

    public void set(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Multiplies this vector by the specified scalar value.
     * Returns result
     *
     * @param scale the scalar value
     */
    public Vector3 mul(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        return this;
    }

    /**
     * Sets the value of this vector to the difference of
     * itself and vector t1 (this = this - t1) .
     *
     * @param t1 the other vector
     */
    public void sub(Vector3 t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
    }


    /**
     * Sets the value of this vector to the sum of
     * itself and vector t1 (this = this + t1) .
     *
     * @param t1 the other vector
     */
    public void add(Vector3 t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
    }

    /**
     * Returns the length of this vector.
     *
     * @return the length of this vector
     */
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Normalize this vector.
     */
    public void normalize() {
        double norm = 1.0 / length();
        this.x = this.x * norm;
        this.y = this.y * norm;
        this.z = this.z * norm;
    }

    /**
     * Sets this vector to be the vector cross product of vectors v1 and v2.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     */
    public void cross(Vector3 v1, Vector3 v2) {
        double tmpX;
        double tmpY;

        tmpX = v1.y * v2.z - v1.z * v2.y;
        tmpY = v2.x * v1.z - v2.z * v1.x;
        this.z = v1.x * v2.y - v1.y * v2.x;
        this.x = tmpX;
        this.y = tmpY;
    }

    /**
     * Computes the dot product of this vector and vector v1.
     *
     * @param v1 the other vector
     * @return the dot product of this vector and v1
     */
    public double dot(Vector3 v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }

    /**
     * Returns the hashcode for this <code>Vec3f</code>.
     *
     * @return a hash code for this <code>Vec3f</code>.
     */
    @Override
    public int hashCode() {
        long bits = 7L;
        bits = 31L * bits + Double.doubleToLongBits(x);
        bits = 31L * bits + Double.doubleToLongBits(y);
        bits = 31L * bits + Double.doubleToLongBits(z);
        return (int) (bits ^ (bits >> 32));
    }

    /**
     * Determines whether or not two 3D points or vectors are equal.
     * Two instances of <code>Vec3d</code> are equal if the values of their
     * <code>x</code>, <code>y</code> and <code>z</code> member fields,
     * representing their position in the coordinate space, are the same.
     *
     * @param obj an object to be compared with this <code>Vec3d</code>
     * @return <code>true</code> if the object to be compared is
     * an instance of <code>Vec3d</code> and has
     * the same values; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Vector3) {
            Vector3 vec = (Vector3) obj;
            return (x == vec.x) && (y == vec.y) && (z == vec.z);
        }
        return false;
    }

    /**
     * Returns a <code>String</code> that represents the value
     * of this <code>Vec3f</code>.
     *
     * @return a string representation of this <code>Vec3f</code>.
     */
    @Override
    public String toString() {
        return "Vec3d[" + x + ", " + y + ", " + z + "]";
    }
}
