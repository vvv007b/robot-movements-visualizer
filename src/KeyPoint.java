/**
 * Created by bocharov_n on 13.11.15.
 */
public class KeyPoint extends RoundButton {
    private int x, y, t, v;

    private Trajectory parentTrajectory;

    public KeyPoint(String label){
        super(label);
    }

    public KeyPoint(String label, int x, int y, int t, int v){
        super(label);
        this.x = x;
        this.y = y;
        this.t = t;
        this.v = v;
    }

    public int getx() {
        return x;
    }

    public void setx(int x) {
        this.x = x;
    }

    public int gety() {
        return y;
    }

    public void sety(int y) {
        this.y = y;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public Trajectory getParentTrajectory() {
        return parentTrajectory;
    }

    public void setParentTrajectory(Trajectory parentTrajectory) {
        this.parentTrajectory = parentTrajectory;
    }
}
