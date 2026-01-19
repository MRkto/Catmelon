package mrkto.catmelon.client.animation.data.animation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KeyframesTimeLine<C extends BaseKeyframe<C>> {
    public List<C> keyframes;
    public KeyframesTimeLine(List<C> keyframesUnsorted) {
        keyframes = keyframesUnsorted;
        sort();
    }

    public C getKeyframe(int index) {
        return keyframes.get(index);
    }
    public List<C> getKeyframes() {
        return Collections.unmodifiableList(keyframes);
    }
    private void sort() {
        this.keyframes.sort(Comparator.comparingDouble(a -> a.time));

        for (int i = 0; i < this.keyframes.size(); i++) {
            C current = this.keyframes.get(i);
            current.next = i + 1 < this.keyframes.size() ? this.keyframes.get(i + 1) : null;
            current.prev = i > 0 ? this.keyframes.get(i - 1) : null;
        }
    }
}
