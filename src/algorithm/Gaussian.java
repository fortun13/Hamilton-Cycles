package algorithm;

import java.util.Random;

/**
 * Created by marek on 24.05.14.
 */
public class Gaussian implements CrossingScheme {
    @Override
    public String toString() {
        return "Rozkład normalny";
    }

    @Override
    public int next(int i) {
        return (int) (Math.abs(new Random().nextGaussian()*2)%i);
    }
}
