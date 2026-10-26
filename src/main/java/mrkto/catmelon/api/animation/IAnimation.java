package mrkto.catmelon.api.animation;

import mrkto.catmelon.api.Pose;

import java.util.List;

public interface IAnimation {
    void animatePose(Pose pose, double time);
    void collectEvents(double fromTime, double toTime, List<IAction> out);
    double getLength();
    boolean isLooping();
}
