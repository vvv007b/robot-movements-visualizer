package ru.mcst.RobotGroup.PathsLinking;

/**
 * Created by bocharov_n on 11.12.15.
 */
class StraightLine {
    public static final int POLAR = 0;
    public static final int SLOPE = 1;

    private double k;   //slope
    private double b;   //vertical shift
    private double a;   //angle in degrees
    private double r;   //radius - vector

    public StraightLine(double x, double y, int eqType){
        switch (eqType)
        {
            case StraightLine.POLAR:
                this.a = x;
                this.r = y;
                calculateSlope();
                break;
            case StraightLine.SLOPE:
                this.k = x;
                this.b = y;
                calculatePolar();
                break;
        }
    }

    public boolean isContains(double x, double y, double accuracy){
        return Math.abs(x * Math.cos(Math.toRadians(this.a)) + y * Math.sin(Math.toRadians(this.a)) - this.r) <= accuracy;
    }

    public void calculateSlope(){
        if(a % 180 != 0) {
            this.k = -Math.cos(Math.toRadians(a)) / Math.sin(Math.toRadians(a));
            this.b = r / Math.sin(a);
        }
        else {
//            System.out.println("Vertical line. Can't compute slope. a="+a);
        }
    }

    public void calculatePolar(){
//        if()
        // TODO: complete calculations of sin a and r
        // cos a = - k /sqrt(k^2 +1)
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
}
