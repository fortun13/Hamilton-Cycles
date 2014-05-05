package algorithm;

import graph.GraphElements;

import java.util.LinkedList;

/**
 * Created by Marek on 2014-04-09.
 * Iterface for Algorithms witch will calculate Hamilton's cycles
 */
public interface Algorithm {
    /**
     * Method that returns calculated Hamilton's cycle
     * @return List of successive stages in Hamilton's cycle
     */
    LinkedList<GraphElements.MyVertex> getCycle();
}
