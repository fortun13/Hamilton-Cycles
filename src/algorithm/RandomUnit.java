package algorithm;

import java.util.Random;

/**
 * Created by marek on 24.05.14.
 */
public class RandomUnit implements CrossingScheme {
    @Override
    public String toString() {
        return "Losowy osobnik";
    }
    @Override
    public int next(int i) {
        return new Random().nextInt(i);
    }
}
