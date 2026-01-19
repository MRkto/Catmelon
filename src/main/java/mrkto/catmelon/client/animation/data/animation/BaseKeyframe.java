package mrkto.catmelon.client.animation.data.animation;

public abstract class BaseKeyframe<C extends BaseKeyframe<C>> {
    protected double time;

    protected C next;
    protected C prev;

    public BaseKeyframe(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public C getNext() {
        return next;
    }

    public C getPrev() {
        return prev;
    }
}
