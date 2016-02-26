package ru.mcst.RobotGroup.PathsLinking;

/**
 * Created by bocharov_n on 19.02.16.
 */
public class InOutVector {
    VisitedPoint startPoint, endPoint;

    public InOutVector(){
        startPoint = null;
        endPoint = null;
    }

    public InOutVector(VisitedPoint startPoint, VisitedPoint endPoint){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }
}
