package graph;

import edu.uci.ics.jung.graph.SparseMultigraph;

import java.util.LinkedList;
import java.util.Random;


/**
 * @author Jakub Fortunka
 *
 */
public class NonGenetic implements Algorithm {

    private int count=0,no=1;
    private SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g;
    private int N;
    private boolean[] visitedVertex;
    private LinkedList<GraphElements.MyVertex> path = new LinkedList<GraphElements.MyVertex>();
    private GraphElements.MyVertex start;

    public NonGenetic(SparseMultigraph<GraphElements.MyVertex, GraphElements.MyEdge> g) {
        N = g.getVertexCount();
        visitedVertex = new boolean[N];
        for (int i=0;i<N;i++) visitedVertex[i]=false;
        this.g = g;
        int i = 0;
        Random rnd = new Random();
        int randomVertex = rnd.nextInt(g.getVertexCount());
        for (GraphElements.MyVertex v : g.getVertices()) {
            if (randomVertex == i) {
                start = v;
                break;
            }
            i++;
        }
    }

    @Override
    public LinkedList<GraphElements.MyVertex> getCycle() {
        if (searchForCycle(start)) return path;
        else return null;
    }

    private boolean searchForCycle(GraphElements.MyVertex v) {
        visitedVertex[v.getNumber()] = true;
        count++;
        for (GraphElements.MyVertex nextVertex : g.getSuccessors(v)) {
            if (nextVertex.getNumber() == start.getNumber()) {
                if (count == N) {
                    path.add(0,v);
                    return true;
                }
            }
            if (!visitedVertex[nextVertex.getNumber()]) {
                if (searchForCycle(nextVertex)) {
                    path.add(no,v);
                    no++;
                    return true;
                }
            }
        }
        visitedVertex[v.getNumber()] = false;
        count--;
        return false;
    }


}
