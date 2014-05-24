package algorithm;

/**
 * Created by marek on 24.05.14.
 */
public class Best implements CrossingScheme {
    @Override
    public String toString() {
        return "Pierwszy najlepszy";
    }

    @Override
    public int next(int i) {
        return 0;
    }
}
